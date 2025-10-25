@file:OptIn(ExperimentalTime::class)

package com.alifwyaa.azanmunich.domain.services

import com.alifwyaa.azanmunich.domain.internal.platform.SharedDateTimeFormatter
import com.alifwyaa.azanmunich.domain.model.SharedDateModel
import com.alifwyaa.azanmunich.domain.model.SharedTimeModel
import com.alifwyaa.azanmunich.domain.model.SharedTimeUntil
import kotlinx.datetime.DateTimeUnit
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

/**
 * Provides date and time operations for the Azan Munich application.
 *
 * Handles date/time calculations, comparisons, formatting, and conversions
 * using the CET timezone. Supports both Gregorian and Islamic (Hijri) calendars.
 *
 * @author Created by Abdullah Essa on 24.05.21.
 */
@Suppress("TooManyFunctions")
class SharedDateTimeService(
    private val settingsService: SharedSettingsService,
    private val localizationService: SharedLocalizationService,
) {

    //region Properties

    /**
     * Current time as an [Instant].
     */
    internal val todayInstant: Instant
        get() = Clock.System.now()

    /**
     * Current date and time in the application's timezone (CET).
     */
    val nowLocalDateTime: LocalDateTime
        get() = todayInstant.toLocalDateTime(appTimeZone)

    //endregion

    //region Checks

    /**
     * Checks if the given date is today.
     *
     * @param dateModel The date to check
     * @return true if the date is today, false otherwise
     */
    fun isToday(dateModel: SharedDateModel): Boolean {
        val nowDate: LocalDate = nowLocalDateTime.date
        val dayDate = LocalDate(
            year = dateModel.year,
            monthNumber = dateModel.month,
            dayOfMonth = dateModel.day
        )
        return dayDate == nowDate
    }

    /**
     * Checks if the given date was yesterday.
     *
     * @param dateModel The date to check
     * @return true if the date was yesterday, false otherwise
     */
    fun wasYesterday(dateModel: SharedDateModel): Boolean {
        val nowDate: LocalDate = nowLocalDateTime.date
        val dayAfterToday = getDayBefore(
            SharedDateModel(
                year = nowDate.year,
                month = nowDate.monthNumber,
                day = nowDate.dayOfMonth,
            )
        )
        return dayAfterToday == dateModel
    }

    /**
     * Checks if the given date is tomorrow.
     *
     * @param dateModel The date to check
     * @return true if the date is tomorrow, false otherwise
     */
    fun isTomorrow(dateModel: SharedDateModel): Boolean {
        val nowDate: LocalDate = nowLocalDateTime.date
        val dayAfterToday = getDayAfter(
            SharedDateModel(
                year = nowDate.year,
                month = nowDate.monthNumber,
                day = nowDate.dayOfMonth,
            )
        )
        return dayAfterToday == dateModel
    }

    /**
     * Checks if the given date and time are in the future.
     *
     * @param dateModel The date to check
     * @param timeModel The time to check
     * @return true if the date and time are after now, false otherwise
     */
    fun isTimeAfter(dateModel: SharedDateModel, timeModel: SharedTimeModel): Boolean {
        val nowDateTime: LocalDateTime = nowLocalDateTime
        val azanDateTime = LocalDateTime(
            year = dateModel.year,
            monthNumber = dateModel.month,
            dayOfMonth = dateModel.day,
            hour = timeModel.hour,
            minute = timeModel.minute,
            second = nowDateTime.second,
            nanosecond = nowDateTime.nanosecond,
        )
        return azanDateTime > nowDateTime
    }

    //endregion

    //region Duration

    /**
     * Calculates the duration from now until the given date and time.
     *
     * @param dateModel The target date
     * @param timeModel The target time
     * @return Duration from now until the specified date and time
     */
    internal fun getDurationUntil(
        dateModel: SharedDateModel,
        timeModel: SharedTimeModel
    ): Duration {
        val futureInstant = LocalDateTime(
            year = dateModel.year,
            monthNumber = dateModel.month,
            dayOfMonth = dateModel.day,
            hour = timeModel.hour,
            minute = timeModel.minute,
            second = timeModel.second,
        ).toInstant(appTimeZone)
        return futureInstant - todayInstant
    }

    /**
     * Calculates the time remaining until the given date and time.
     *
     * @param dateModel The target date
     * @param timeModel The target time
     * @return Time until specified date and time, broken down into hours, minutes, and seconds
     */
    fun getTimeUntil(
        dateModel: SharedDateModel,
        timeModel: SharedTimeModel
    ): SharedTimeUntil {
        val totalNumberOfSeconds =
            getDurationUntil(dateModel, timeModel).toDouble(DurationUnit.SECONDS)

        @Suppress("MagicNumber")
        val hours: Int = (totalNumberOfSeconds / 60 / 60).toInt()

        @Suppress("MagicNumber")
        val minutes: Int = (totalNumberOfSeconds / 60 % 60).toInt()

        @Suppress("MagicNumber")
        val seconds: Int = (totalNumberOfSeconds % 60).toInt()
        return SharedTimeUntil(
            totalNumberOfSeconds = totalNumberOfSeconds.toLong(),
            hours = hours,
            minutes = minutes,
            seconds = seconds
        )
    }

    //endregion

    //region Generate Day Model

    /**
     * Gets the current time.
     *
     * @return Current time as [SharedTimeModel]
     */
    fun getNow(): SharedTimeModel = nowLocalDateTime.run {
        SharedTimeModel(
            hour = hour,
            minute = minute,
            second = second
        )
    }

    /**
     * Gets today's date.
     *
     * @return Today's date as [SharedDateModel]
     */
    fun getToday(): SharedDateModel = nowLocalDateTime.run {
        SharedDateModel(
            day = dayOfMonth,
            month = monthNumber,
            year = year
        )
    }

    /**
     * Gets the day after the given date.
     *
     * @param dateModel The reference date
     * @return The date one day after the given date
     */
    fun getDayAfter(dateModel: SharedDateModel): SharedDateModel = LocalDate(
        year = dateModel.year,
        monthNumber = dateModel.month,
        dayOfMonth = dateModel.day
    ).plus(DateTimeUnit.DateBased.DayBased(1)).run {
        SharedDateModel(
            day = dayOfMonth,
            month = monthNumber,
            year = year
        )
    }

    /**
     * Gets the day before the given date.
     *
     * @param dateModel The reference date
     * @return The date one day before the given date
     */
    fun getDayBefore(dateModel: SharedDateModel): SharedDateModel = LocalDate(
        year = dateModel.year,
        monthNumber = dateModel.month,
        dayOfMonth = dateModel.day
    ).minus(DateTimeUnit.DateBased.DayBased(1)).run {
        SharedDateModel(
            day = dayOfMonth,
            month = monthNumber,
            year = year
        )
    }

    /**
     * Generates a list of dates from today to a specified number of days in the future.
     *
     * @param daysFromNow Number of days to generate (inclusive of today)
     * @return List of dates from today through the specified number of future days
     */
    fun getDaysFromNow(daysFromNow: Int): List<SharedDateModel> =
        mutableListOf<LocalDateTime>().apply {
            for (i in 0..daysFromNow) {
                add(todayInstant.plus(i.days).toLocalDateTime(appTimeZone))
            }
        }.map {
            SharedDateModel(
                day = it.dayOfMonth,
                month = it.monthNumber,
                year = it.year
            )
        }

    //endregion

    //region Format

    /**
     * Formats a time according to the specified pattern and current locale.
     *
     * @param dateModel The date for the time
     * @param timeModel The time to format
     * @param pattern The formatting pattern (e.g., "HH:mm")
     * @return Formatted time string
     */
    fun getFormattedTime(
        dateModel: SharedDateModel,
        timeModel: SharedTimeModel,
        pattern: String
    ): String {
        val dateTime = LocalDateTime(
            year = dateModel.year,
            monthNumber = dateModel.month,
            dayOfMonth = dateModel.day,
            hour = timeModel.hour,
            minute = timeModel.minute
        )
        return SharedDateTimeFormatter.gregorianFormat(
            dateTime = dateTime,
            pattern = pattern,
            localeCode = settingsService.sharedAppLocale.code
        )
    }

    /**
     * Formats a date using the Gregorian calendar.
     *
     * @param dateModel The date to format
     * @param pattern The formatting pattern (e.g., "EEE, dd MMM yyyy")
     * @return Formatted Gregorian date string
     */
    fun getWesternFormattedDate(
        dateModel: SharedDateModel,
        pattern: String
    ): String =
        SharedDateTimeFormatter.gregorianFormat(
            dateTime = LocalDateTime(
                year = dateModel.year,
                monthNumber = dateModel.month,
                dayOfMonth = dateModel.day,
                hour = 0,
                minute = 0,
                nanosecond = 0
            ),
            pattern = pattern,
            localeCode = settingsService.sharedAppLocale.code
        )

    /**
     * Formats a date using the Islamic (Hijri) calendar.
     *
     * @param dateModel The date to format
     * @param pattern The formatting pattern (e.g., "dd MMMM yyyy")
     * @return Formatted Islamic date string
     */
    fun getIslamicFormattedDate(
        dateModel: SharedDateModel,
        pattern: String
    ): String = SharedDateTimeFormatter.hijriFormat(
        dateTime = LocalDateTime(
            year = dateModel.year,
            monthNumber = dateModel.month,
            dayOfMonth = dateModel.day,
            hour = 0,
            minute = 0,
            nanosecond = 0
        ),
        pattern = pattern,
        localeCode = settingsService.sharedAppLocale.code
    )

    /**
     * Formats a date using the Gregorian calendar with relative terms.
     *
     * Returns "Yesterday", "Today", or "Tomorrow" when applicable,
     * otherwise uses the default western pattern.
     *
     * @param dateModel The date to format
     * @return Formatted date string with relative terms when applicable
     */
    fun getWesternFormattedDate(dateModel: SharedDateModel): String = when {
        wasYesterday(dateModel) -> localizationService.strings.yesterday
        isToday(dateModel) -> localizationService.strings.today
        isTomorrow(dateModel) -> localizationService.strings.tomorrow
        else -> getWesternFormattedDate(dateModel, DATE_WESTERN_PATTERN)
    }


    /**
     * Formats a date using the default Islamic (Hijri) calendar pattern.
     *
     * @param dateModel The date to format
     * @return Formatted Islamic date string using the default pattern
     */
    fun getIslamicFormattedDate(dateModel: SharedDateModel): String =
        getIslamicFormattedDate(dateModel, DATE_ISLAMIC_PATTERN)

    //endregion

    companion object {
        val appTimeZone: TimeZone = TimeZone.of("CET")
        const val DATE_WESTERN_PATTERN = "EEE, dd MMM yyyy"
        const val DATE_ISLAMIC_PATTERN = "dd MMMM yyyy"
        const val TIME_FORMAT = "HH:mm"
    }
}
