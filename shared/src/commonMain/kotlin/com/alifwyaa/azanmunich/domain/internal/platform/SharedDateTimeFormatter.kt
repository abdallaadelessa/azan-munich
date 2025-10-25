package com.alifwyaa.azanmunich.domain.internal.platform

import kotlinx.datetime.LocalDateTime

/**
 * Platform-specific date and time formatting.
 *
 * Provides date/time formatting for both Gregorian and Islamic (Hijri) calendars
 * using platform-native formatting libraries. Supports locale-specific formatting.
 *
 * @author Created by Abdullah Essa on 01.06.21.
 */
expect object SharedDateTimeFormatter {
    /**
     * Formats a date/time using the Gregorian calendar.
     *
     * @param dateTime The date/time to format
     * @param pattern The formatting pattern (e.g., "EEE, dd MMM yyyy", "HH:mm")
     * @param localeCode The locale code for formatting (e.g., "en", "de")
     * @return Formatted date/time string
     */
    fun gregorianFormat(dateTime: LocalDateTime, pattern: String, localeCode: String): String

    /**
     * Formats a date/time using the Islamic (Hijri) calendar.
     *
     * @param dateTime The Gregorian date/time to convert and format
     * @param pattern The formatting pattern (e.g., "dd MMMM yyyy")
     * @param localeCode The locale code for formatting (e.g., "en", "de")
     * @return Formatted Islamic date string
     */
    fun hijriFormat(dateTime: LocalDateTime, pattern: String, localeCode: String): String
}
