package com.alifwyaa.azanmunich.domain.internal.platform

import com.alifwyaa.azanmunich.domain.services.SharedDateTimeService
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toInstant
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarIdentifierIslamicUmmAlQura
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.dateWithTimeIntervalSince1970

/**
 * @author Created by Abdullah Essa on 01.06.21.
 */
actual object SharedDateTimeFormatter {
    actual fun gregorianFormat(dateTime: LocalDateTime, pattern: String, localeCode: String): String =
        NSDateFormatter().apply {
            setLocale(NSLocale(localeCode))
            setDateFormat(pattern)
            setCalendar(NSCalendar.currentCalendar())
        }.stringFromDate(dateTime.toNSDate())

    actual fun hijriFormat(dateTime: LocalDateTime, pattern: String, localeCode: String): String =
        NSDateFormatter().apply {
            setLocale(NSLocale(localeCode))
            setDateFormat(pattern)
            setCalendar(NSCalendar(NSCalendarIdentifierIslamicUmmAlQura))
        }.stringFromDate(dateTime.toNSDate())

    private fun LocalDateTime.toNSDate(): NSDate {
        val epoch = toInstant(SharedDateTimeService.appTimeZone).epochSeconds.toDouble()
        return NSDate.dateWithTimeIntervalSince1970(epoch)
    }
}
