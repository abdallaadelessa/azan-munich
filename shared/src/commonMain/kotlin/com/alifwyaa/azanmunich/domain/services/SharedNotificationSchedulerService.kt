package com.alifwyaa.azanmunich.domain.services

import com.alifwyaa.azanmunich.data.internal.model.SharedAzanType
import com.alifwyaa.azanmunich.domain.extensions.toNotificationSound
import com.alifwyaa.azanmunich.domain.internal.platform.SharedDispatchers
import com.alifwyaa.azanmunich.domain.internal.platform.SharedLocalNotificationsService
import com.alifwyaa.azanmunich.domain.model.SharedAzanModel
import com.alifwyaa.azanmunich.domain.model.SharedNotificationModel
import com.alifwyaa.azanmunich.domain.model.events.SharedNotificationsChangedEvent
import com.alifwyaa.azanmunich.domain.model.settings.SharedAppSound
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

/**
 * @author Created by Abdullah Essa on 24.05.21.
 */
@Suppress("UnusedPrivateMember")
@OptIn(ExperimentalTime::class)
class SharedNotificationSchedulerService @Suppress("LongParameterList") constructor(
    private val appScope: SharedAppScope,
    private val logService: SharedLogService,
    private val localizationService: SharedLocalizationService,
    private val dateTimeService: SharedDateTimeService,
    private val azanService: SharedAzanService,
    private val settingsService: SharedSettingsService,
    private val localNotificationsService: SharedLocalNotificationsService,
    preferenceService: SharedPreferenceService
) {

    private var scheduleNotificationsJob: Job? = null

    init {
        // Reschedule notifications if the notification settings is changed
        appScope.launch(SharedDispatchers.Main) {
            settingsService.eventsFlow
                .mapNotNull { it as? SharedNotificationsChangedEvent }
                .collect { startScheduleNotificationsJob() }
        }
    }

    private var notificationsInPrefs: List<SharedNotificationModel> by preferenceService.json(
        key = PREFS_KEY_LAST_NOTIFICATIONS,
        defaultValue = emptyList()
    )

    /**
     * Job scheduler
     */
    @Suppress("MagicNumber")
    val scheduleTimeFromNowInSeconds: Double = Duration.convert(
        PERIODIC_TASK_NUM_OF_DAYS_IN_FUTURE.toDouble(),
        DurationUnit.DAYS,
        DurationUnit.SECONDS
    )

    //region Main Logic

    /**
     * Start the schedule notifications job
     */
    fun startScheduleNotificationsJob() {
        scheduleNotificationsJob?.cancel()
        scheduleNotificationsJob =
            appScope.launch(SharedDispatchers.Main) { scheduleNotifications() }
    }

    /**
     * Used directly from IOS
     * Sync the azan time in the notifications manager
     */
    suspend fun scheduleNotifications() = withContext(SharedDispatchers.Main) {
        // Permission
        if (!localNotificationsService.isPermissionGranted()) {
            val isGranted = localNotificationsService.requestPermission()
            if (!isGranted) return@withContext
        }

        ensureActive()

        // Compute

        logMessage("scheduleNotifications : computeNewNotifications")

        val newNotifications = computeNewNotifications()

        if (newNotifications.isEmpty()) return@withContext

        ensureActive()

        // Cancel old notifications
        val oldNotifications = notificationsInPrefs

        ensureActive()

        cancelOldNotifications(oldNotifications)

        logMessage("scheduleNotifications : cancelOldNotifications")

        ensureActive()

        // Add new notifications
        addNewNotifications(newNotifications)

        logMessage("scheduleNotifications : addNewNotifications")
    }

    private suspend fun addNewNotifications(newNotifications: List<SharedNotificationModel>) {
        if (newNotifications.isNotEmpty()) {
            logData("New notifications", newNotifications)
            notificationsInPrefs = newNotifications
            localNotificationsService.addNotifications(newNotifications)
            logData("Current Notifications in Preferences", notificationsInPrefs)
        }
    }

    private suspend fun cancelOldNotifications(oldNotifications: List<SharedNotificationModel>) {
        if (oldNotifications.isNotEmpty()) {
            logData("Old Notifications in Preferences", oldNotifications)
            localNotificationsService.cancelNotifications(oldNotifications.map { it.id })
            notificationsInPrefs = emptyList()
            logData("Current Notifications in Preferences", notificationsInPrefs)
        }
    }

    //endregion

    //region Test

    @Suppress("UndocumentedPublicFunction", "MagicNumber")
    suspend fun testNotification(text: String) {
        if (!localNotificationsService.isPermissionGranted()) {
            val isGranted = localNotificationsService.requestPermission()
            if (!isGranted) return
        }

        val id = buildString { repeat(8) { append(('a'..'z').random()) } }

        val dateModel = dateTimeService.getToday()

        val timeModel = dateTimeService.getNow().run { copy(second = second + 1) }

        val sharedAppSound = settingsService.appSoundModel.appSound

        val sharedAzanType = SharedAzanType.DHUHR

        localNotificationsService.addNotifications(
            listOf(
                SharedNotificationModel(
                    id = id,
                    categoryId = getChannelId(
                        sharedAzanType = sharedAzanType,
                        sharedAppSound = settingsService.appSoundModel.appSound
                    ),
                    title = text,
                    sound = sharedAppSound.toNotificationSound(sharedAzanType),
                    payload = emptyMap(),
                    dateModel = dateModel,
                    timeModel = timeModel,
                )
            )
        )

        //localNotificationsService.cancelNotifications(listOf(id))
    }

    //endregion

    //region Helpers

    private suspend fun computeNewNotifications() =
        azanService.getAzanList(SCHEDULE_NOTIFICATIONS_NUM_OF_DAYS_IN_FUTURE).mapNotNull { model ->
            val timeFromNowInSeconds = dateTimeService.getDurationUntil(
                dateModel = model.dateModel,
                timeModel = model.timeModel
            ).toDouble(DurationUnit.SECONDS)

            // Filter with model in the future
            if (timeFromNowInSeconds <= 0) return@mapNotNull null

            model.toSharedNotificationModel()
        }

    private fun SharedAzanModel.toSharedNotificationModel(): SharedNotificationModel {
        val sharedAppSound = settingsService.appSoundModel.appSound

        val categoryId = getChannelId(
            sharedAzanType = azanType,
            sharedAppSound = sharedAppSound
        )

        val sound: SharedNotificationModel.Sound = sharedAppSound.toNotificationSound(azanType)

        return SharedNotificationModel(
            id = id,
            categoryId = categoryId,
            title = localizationService.getLocalNotificationTitle(this),
            sound = sound,
            payload = mapOf(PAYLOAD_ID to id),
            dateModel = dateModel,
            timeModel = timeModel,
            isEnabled = settingsService.isAzanEnabled(azanType)
        )
    }

    private fun logMessage(message: String) {
        if (ENABLE_LOGS.not()) return
        logService.m { message }
    }

    private suspend fun logData(title: String, notifications: List<SharedNotificationModel>) {
        if (ENABLE_LOGS.not()) return
        logService.p {
            appendLine("----------> $title")
            notifications.forEach {
                val isAdded = localNotificationsService.isNotificationAdded(it.id)
                val timeFromNowInSeconds = dateTimeService.getDurationUntil(
                    dateModel = it.dateModel,
                    timeModel = it.timeModel
                ).toDouble(DurationUnit.SECONDS)
                val dateTime = Clock.System.now()
                    .plus(Duration.seconds(timeFromNowInSeconds))
                    .toLocalDateTime(SharedDateTimeService.appTimeZone)
                it.run {
                    appendLine(
                        "$id $title $sound"
                                + " Date: (${dateTime.dayOfMonth}/${dateTime.monthNumber}/${dateTime.year})"
                                + " Time: (${dateTime.hour}:${dateTime.minute})"
                                + " isAdded: $timeFromNowInSeconds $isAdded"
                    )
                }
            }
            appendLine("---------->")
        }
    }

    //endregion

    companion object {
        private const val ENABLE_LOGS = false
        private const val PREFS_KEY_LAST_NOTIFICATIONS = "KEY_LAST_NOTIFICATIONS"
        private const val PAYLOAD_ID = "ID"
        private const val PERIODIC_TASK_NUM_OF_DAYS_IN_FUTURE: Int = 2
        private const val SCHEDULE_NOTIFICATIONS_NUM_OF_DAYS_IN_FUTURE: Int = 6

        /**
         * @return the android channel id for the given values
         */
        fun getChannelId(sharedAzanType: SharedAzanType, sharedAppSound: SharedAppSound): String =
            "$sharedAzanType-$sharedAppSound"
    }
}
