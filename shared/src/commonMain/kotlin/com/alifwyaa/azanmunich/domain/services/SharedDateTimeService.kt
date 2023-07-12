package com.alifwyaa.azanmunich.domain.services

import com.alifwyaa.azanmunich.domain.internal.platform.SharedDateTimeFormatter
import com.alifwyaa.azanmunich.domain.model.SharedDateModel
import com.alifwyaa.azanmunich.domain.model.SharedTimeModel
import com.alifwyaa.azanmunich.domain.model.SharedTimeUntil
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

/**
 * @author Created by Abdullah Essa on 24.05.21.
 */
@Suppress("TooManyFunctions")
@OptIn(ExperimentalTime::class)
class SharedDateTimeService(
    private val settingsService: SharedSettingsService,
    private val localizationService: SharedLocalizationService,
) {

    //region Properties

    internal val todayInstant: Instant
        get() = Clock.System.now()

    val nowLocalDateTime: LocalDateTime
        get() = todayInstant.toLocalDateTime(appTimeZone)

    //endregion

    //region Checks

    /**
     * @return true if [dateModel] is today otherwise false
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
     * @return true if [dateModel] was yesterday otherwise false
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
     * @return true if [dateModel] is tomorrow otherwise false
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
     * @return true if the given [dateModel] and [timeModel] are after now otherwise false
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
     * @return duration until given date and time
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
     * @return the number of milliseconds until given date and time
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
     * @return now time [SharedTimeModel]
     */
    fun getNow(): SharedTimeModel = nowLocalDateTime.run {
        SharedTimeModel(
            hour = hour,
            minute = minute,
            second = second
        )
    }

    /**
     * @return today's [SharedDateModel]
     */
    fun getToday(): SharedDateModel = nowLocalDateTime.run {
        SharedDateModel(
            day = dayOfMonth,
            month = monthNumber,
            year = year
        )
    }

    /**
     * @return the [SharedDateModel] after the given [dateModel]
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
     * @return the [SharedDateModel] before the given [dateModel]
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
     * @return a list of [SharedDateModel] from the given [daysFromNow] including today
     */
    fun getDaysFromNow(daysFromNow: Int): List<SharedDateModel> =
        mutableListOf<LocalDateTime>().apply {
            for (i in 0..daysFromNow) {
                add(todayInstant.plus(Duration.days(i)).toLocalDateTime(appTimeZone))
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
     * @return the default formatted time
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
     * @return the western date format for the given [dateModel]
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
     * @return the islamic date format for the given [dateModel]
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
     * @return the western date format for the given [dateModel]
     */
    fun getWesternFormattedDate(dateModel: SharedDateModel): String = when {
        wasYesterday(dateModel) -> localizationService.strings.yesterday
        isToday(dateModel) -> localizationService.strings.today
        isTomorrow(dateModel) -> localizationService.strings.tomorrow
        else -> getWesternFormattedDate(dateModel, DATE_WESTERN_PATTERN)
    }


    /**
     * @return the islamic date format for the given [dateModel]
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
