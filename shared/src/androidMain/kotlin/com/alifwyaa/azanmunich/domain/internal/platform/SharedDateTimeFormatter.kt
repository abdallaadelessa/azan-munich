package com.alifwyaa.azanmunich.domain.internal.platform

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.icu.util.ULocale
import android.os.Build
import androidx.annotation.RequiresApi
import com.alifwyaa.azanmunich.domain.services.SharedDateTimeService.Companion.appTimeZone
import java.time.format.DateTimeFormatter.ofPattern
import java.util.Date
import java.util.Locale
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toJavaLocalDateTime

/**
 * @author Created by Abdullah Essa on 01.06.21.
 */
@RequiresApi(Build.VERSION_CODES.O)
actual object SharedDateTimeFormatter {
    actual fun gregorianFormat(
        dateTime: LocalDateTime,
        pattern: String,
        localeCode: String
    ): String = kotlin.runCatching {
        dateTime.toJavaLocalDateTime().format(ofPattern(pattern, Locale(localeCode)))
    }.getOrDefault("-")

    @SuppressLint("SimpleDateFormat")
    actual fun hijriFormat(
        dateTime: LocalDateTime,
        pattern: String,
        localeCode: String
    ): String = kotlin.runCatching {
        val kotlinxInstant: Instant = dateTime.toInstant(appTimeZone)
        val javaInstant: java.time.Instant = kotlinxInstant.toJavaInstant()
        val date: Date = Date.from(javaInstant)
        val uLocale: ULocale = ULocale.Builder()
            .setLocale(ULocale("@calendar=islamic-umalqura"))
            .setLanguage(localeCode)
            .build()
        val simpleDateFormat = SimpleDateFormat(pattern, uLocale)
        return simpleDateFormat.format(date)
    }.getOrDefault("-")
}
