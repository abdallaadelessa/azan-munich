package com.alifwyaa.azanmunich.domain.services

import com.alifwyaa.azanmunich.data.internal.model.SharedAzanType
import com.alifwyaa.azanmunich.domain.SharedErrorCodes
import com.alifwyaa.azanmunich.domain.model.SharedAzanModel
import com.alifwyaa.azanmunich.domain.model.SharedDateModel
import com.alifwyaa.azanmunich.domain.model.SharedResult
import com.alifwyaa.azanmunich.domain.model.SharedTimeModel
import com.alifwyaa.azanmunich.domain.model.widgets.SharedAndroidDayWidgetData
import com.alifwyaa.azanmunich.domain.model.widgets.SharedAndroidNewPrayerWidgetData
import com.alifwyaa.azanmunich.domain.model.widgets.SharedIosDayWidgetData
import com.alifwyaa.azanmunich.domain.model.widgets.SharedIosNextPrayerWidgetData
import kotlinx.datetime.toLocalDateTime
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

/**
 * @author Created by Abdullah Essa on 25.02.22.
 */
class SharedWidgetsDataService(
    private val azanService: SharedAzanService,
    private val localizationService: SharedLocalizationService,
    private val settingsService: SharedSettingsService,
    private val dateTimeService: SharedDateTimeService,
    private val logService: SharedLogService,
) {

    //region Android

    /**
     * @return the next prayer widget data
     */
    suspend fun getNextPrayerWidgetData(): SharedResult<SharedAndroidNewPrayerWidgetData> {
        val nextAzan: SharedAzanModel = getNextAzanWithoutSunrise()
            ?: return SharedResult.Error(
                id = SharedErrorCodes.NO_RESULT,
                message = localizationService.strings.errorNoResult,
            )
        return SharedResult.Success(
            SharedAndroidNewPrayerWidgetData(
                displayName = nextAzan.displayName,
                displayTime = nextAzan.displayTime,
            )
        )
    }

    /**
     * @return the day widget data
     */
    suspend fun getDayWidgetData(): SharedResult<SharedAndroidDayWidgetData> {
        val nextAzan: SharedAzanModel = getNextAzanWithoutSunrise()
            ?: return SharedResult.Error(
                id = SharedErrorCodes.NO_RESULT,
                message = localizationService.strings.errorNoResult,
            )

        val nextAzanDay: SharedDateModel = nextAzan.dateModel

        val azanList: SharedResult<List<SharedAzanModel>> = getAzanListWithoutSunrise(nextAzanDay)

        return azanList.mapSuccess { result: List<SharedAzanModel> ->
            val items: List<SharedAndroidDayWidgetData.Item> = result.map { item: SharedAzanModel ->
                SharedAndroidDayWidgetData.Item(
                    azanType = item.azanType,
                    displayName = item.displayName,
                    displayTime = item.displayTime,
                    isNextAzan = nextAzan.id == item.id,
                    isAzanEnabled = settingsService.isAzanEnabled(item.azanType),
                )
            }
            SharedAndroidDayWidgetData(items = items)
        }
    }

    //endregion

    //region IOS

    /**
     * @return the next prayer widget data
     */
    suspend fun getIosNextPrayerWidgetData(): SharedIosNextPrayerWidgetData {
        val defaultData = SharedIosNextPrayerWidgetData(
            title = localizationService.strings.nextPrayer,
            timeline = emptyList(),
            nextUpdateInHours = IOS_NEXT_UPDATE_IN_HOURS
        )

        val timeline = mutableListOf<SharedIosNextPrayerWidgetData.TimelineItem>()

        val nextAzan: SharedAzanModel = getNextAzanWithoutSunrise() ?: return defaultData

        var nextAzanDay: SharedDateModel = nextAzan.dateModel

        //-------> Next Azan Day
        val firstDayTimelineItems = getIosNextPrayerWidgetDayTimeline(
            previousAzan = timeline.lastOrNull()?.azanModel,
            dayModel = nextAzanDay
        )?.filter { it.azanModel.timeModel.toSeconds >= nextAzan.timeModel.toSeconds }
            ?: return defaultData
        timeline.addAll(firstDayTimelineItems)

        //-------> Next Days
        @Suppress("UnusedPrivateMember")
        for (index in 0 until IOS_TIMELINE_FUTURE_DAYS) {
            nextAzanDay = dateTimeService.getDayAfter(nextAzanDay)
            val nextDaysTimelineItems = getIosNextPrayerWidgetDayTimeline(
                previousAzan = timeline.lastOrNull()?.azanModel,
                dayModel = nextAzanDay
            ) ?: break
            timeline.addAll(nextDaysTimelineItems)
        }

        return defaultData.copy(timeline = timeline).apply { log() }
    }

    /**
     * @return the day widget data
     */
    suspend fun getIosDayWidgetData(): SharedIosDayWidgetData {
        val defaultData = SharedIosDayWidgetData(
            title = localizationService.strings.dayWidgetTitle,
            timeline = emptyList(),
            nextUpdateInHours = IOS_NEXT_UPDATE_IN_HOURS
        )

        val timeline = mutableListOf<SharedIosDayWidgetData.TimelineItem>()

        val nextAzan: SharedAzanModel = getNextAzanWithoutSunrise() ?: return defaultData

        var nextAzanDay: SharedDateModel = nextAzan.dateModel

        //-------> First Day Timelines
        val firstDayTimelineItems = getIosDayWidgetDayTimelines(
            previousAzan = timeline.lastOrNull()?.nextAzanModel,
            dayModel = nextAzanDay
        )?.filter { it.nextAzanModel.timeModel.toSeconds >= nextAzan.timeModel.toSeconds }
            ?: return defaultData
        timeline.addAll(firstDayTimelineItems)

        //-------> Next Days Timelines
        @Suppress("UnusedPrivateMember")
        for (index in 0 until IOS_TIMELINE_FUTURE_DAYS) {
            nextAzanDay = dateTimeService.getDayAfter(nextAzanDay)
            val nextDaysTimelineItems = getIosDayWidgetDayTimelines(
                previousAzan = timeline.lastOrNull()?.nextAzanModel,
                dayModel = nextAzanDay
            ) ?: break
            timeline.addAll(nextDaysTimelineItems)
        }

        return defaultData.copy(timeline = timeline).apply { log() }
    }

    //=====>

    private suspend fun getIosNextPrayerWidgetDayTimeline(
        previousAzan: SharedAzanModel?,
        dayModel: SharedDateModel
    ): List<SharedIosNextPrayerWidgetData.TimelineItem>? {

        val timelineItemsForGivenDay: MutableList<SharedIosNextPrayerWidgetData.TimelineItem> =
            mutableListOf()

        val dayAzanTimes = getAzanListWithoutSunrise(dayModel).getOrNull() ?: return null

        var lastAddedAzan: SharedAzanModel? = previousAzan

        dayAzanTimes.forEach { currentAzan ->
            timelineItemsForGivenDay.add(
                SharedIosNextPrayerWidgetData.TimelineItem(
                    startAfterNowInSeconds = getStartTimeInSeconds(lastAddedAzan),
                    azanModel = currentAzan,
                    displayName = currentAzan.displayName,
                    displayTime = currentAzan.displayTime,
                )
            )
            lastAddedAzan = currentAzan
        }

        return timelineItemsForGivenDay
    }

    private suspend fun getIosDayWidgetDayTimelines(
        previousAzan: SharedAzanModel?,
        dayModel: SharedDateModel
    ): List<SharedIosDayWidgetData.TimelineItem>? {

        val timelineItemsForGivenDay = mutableListOf<SharedIosDayWidgetData.TimelineItem>()

        val dayAzanTimes = getAzanListWithoutSunrise(dayModel).getOrNull() ?: return null

        val templateTimelineItem: SharedIosDayWidgetData.TimelineItem =
            SharedIosDayWidgetData.TimelineItem(
                startAfterNowInSeconds = 0L,
                nextAzanModel = SharedAzanModel(
                    id = "",
                    azanType = SharedAzanType.DHUHR,
                    displayName = "",
                    displayTime = "",
                    dateModel = SharedDateModel(0, 0, 0),
                    timeModel = SharedTimeModel(0, 0),
                ),
                items = dayAzanTimes.map { azan ->
                    SharedIosDayWidgetData.TimelineItem.Item(
                        displayName = azan.displayName,
                        displayTime = azan.displayTime,
                        isNextAzan = false,
                        azanType = azan.azanType
                    )
                }
            )

        var lastAddedAzan: SharedAzanModel? = previousAzan

        dayAzanTimes.forEach { nextAzan ->
            timelineItemsForGivenDay.add(
                templateTimelineItem.copy(
                    startAfterNowInSeconds = getStartTimeInSeconds(lastAddedAzan),
                    nextAzanModel = nextAzan,
                    items = templateTimelineItem.items.map {
                        it.copy(isNextAzan = it.azanType == nextAzan.azanType)
                    }
                )
            )
            lastAddedAzan = nextAzan
        }

        return timelineItemsForGivenDay
    }

    @OptIn(ExperimentalTime::class)
    private fun SharedIosNextPrayerWidgetData.log() {
        if (ENABLE_LOGS.not()) return
        logService.p {
            val nextUpdateDate = dateTimeService.todayInstant.plus(
                nextUpdateInHours.toDuration(DurationUnit.HOURS)
            ).toLocalDateTime(SharedDateTimeService.appTimeZone)
            appendLine("Title: $title")
            appendLine("NextUpdate: $nextUpdateDate")
            timeline.forEach {
                val startAfterDate = dateTimeService.todayInstant.plus(
                    it.startAfterNowInSeconds.toDuration(DurationUnit.HOURS)
                ).toLocalDateTime(SharedDateTimeService.appTimeZone)
                appendLine("StartAfterDate $startAfterDate")
                appendLine("DisplayName ${it.displayName}")
                appendLine("DisplayTime ${it.displayTime}")
                appendLine("--------------------")
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun SharedIosDayWidgetData.log() {
        if (ENABLE_LOGS.not()) return
        logService.p {
            val nextUpdateDate = dateTimeService.todayInstant.plus(
                nextUpdateInHours.toDuration(DurationUnit.HOURS)
            ).toLocalDateTime(SharedDateTimeService.appTimeZone)
            appendLine("Title: $title")
            appendLine("NextUpdate: $nextUpdateDate")
            timeline.forEach {
                val startAfterDate = dateTimeService.todayInstant.plus(
                    it.startAfterNowInSeconds.toDuration(DurationUnit.HOURS)
                ).toLocalDateTime(SharedDateTimeService.appTimeZone)
                val names = it.items.fold("") { sum, i -> "$sum ${i.displayName}" }
                val times = it.items.fold("") { sum, i -> "$sum ${i.displayTime}" }
                val isNext = it.items.fold("") { sum, i -> "$sum ${i.isNextAzan}" }
                appendLine("StartAfterDate $startAfterDate")
                appendLine("DisplayName $names")
                appendLine("DisplayTime $times")
                appendLine("IsNext $isNext")
                appendLine("--------------------")
            }
        }
    }

    private fun getStartTimeInSeconds(previousAzan: SharedAzanModel?): Long {
        if (previousAzan == null) return 0
        return dateTimeService.getTimeUntil(
            dateModel = previousAzan.dateModel,
            timeModel = previousAzan.timeModel
        ).totalNumberOfSeconds + 1
    }

    //endregion

    //region Common

    private suspend fun getAzanListWithoutSunrise(dateModel: SharedDateModel): SharedResult<List<SharedAzanModel>> =
        azanService.getAzanList(dateModel)
            .mapSuccess { list -> list.filter { it.azanType != SharedAzanType.SUNRISE } }

    private suspend fun getNextAzanWithoutSunrise(): SharedAzanModel? {
        val nextAzan = azanService.getNextAzan().getOrNull() ?: return null
        return if (nextAzan.azanType == SharedAzanType.SUNRISE) {
            azanService.getAzanList(nextAzan.dateModel).getOrNull()
                ?.firstOrNull { it.azanType == SharedAzanType.DHUHR }
        } else {
            nextAzan
        }
    }

    //endregion

    companion object {
        private const val IOS_TIMELINE_FUTURE_DAYS = 6
        private const val IOS_NEXT_UPDATE_IN_HOURS = 24 * 1
        private const val ENABLE_LOGS = true
    }

}
