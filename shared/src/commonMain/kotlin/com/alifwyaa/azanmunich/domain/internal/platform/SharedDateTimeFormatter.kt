package com.alifwyaa.azanmunich.domain.internal.platform

import kotlinx.datetime.LocalDateTime

/**
 * @author Created by Abdullah Essa on 01.06.21.
 */
expect object SharedDateTimeFormatter {
    fun gregorianFormat(dateTime: LocalDateTime, pattern: String, localeCode: String): String

    fun hijriFormat(dateTime: LocalDateTime, pattern: String, localeCode: String): String
}
