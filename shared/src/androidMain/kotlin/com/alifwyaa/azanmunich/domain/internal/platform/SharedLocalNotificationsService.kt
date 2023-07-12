@file:Suppress("UnreachableCode", "UnusedImports")

package com.alifwyaa.azanmunich.domain.internal.platform

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.AlarmManagerCompat
import com.alifwyaa.azanmunich.domain.internal.notification.NotificationUtils
import com.alifwyaa.azanmunich.domain.internal.notification.SingleNotificationBroadcastReceiver
import com.alifwyaa.azanmunich.domain.model.SharedNotificationModel
import com.alifwyaa.azanmunich.domain.model.SharedPlatformInfo
import com.alifwyaa.azanmunich.domain.services.SharedDateTimeService
import com.alifwyaa.azanmunich.domain.services.SharedLogService
import com.alifwyaa.azanmunich.domain.services.SharedSettingsService
import com.alifwyaa.azanmunich.widget.AndroidWidgetsService
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime


/**
 * @author Created by Abdullah Essa on 06.06.21.
 */
@Suppress("RedundantSuspendModifier", "NotImplementedDeclaration", "UndocumentedPublicFunction")
actual class SharedLocalNotificationsService actual constructor(
    platformInfo: SharedPlatformInfo,
    private val dateTimeService: SharedDateTimeService,
    private val settingsService: SharedSettingsService,
    private val logService: SharedLogService,
) {

    private val appContext: Context =
        (platformInfo as SharedPlatformInfo.Android).appContext as Context

    private val alarmManager: AlarmManager
            by lazy { appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager }

    init {
        updateNotificationChannels()
    }

    //region SharedLocalNotificationsService

    actual suspend fun isPermissionGranted(): Boolean = true

    actual suspend fun requestPermission(): Boolean = true

    actual suspend fun isNotificationAdded(id: String): Boolean = true

    @OptIn(ExperimentalTime::class)
    actual suspend fun addNotifications(models: List<SharedNotificationModel>): Boolean {
        // Schedule Notifications
        kotlin.runCatching {
            updateNotificationChannels()
            models.forEach { item ->
                val timeFromNowInSeconds: Long = dateTimeService.getDurationUntil(
                    dateModel = item.dateModel,
                    timeModel = item.timeModel
                ).toDouble(DurationUnit.MILLISECONDS).toLong()

                if (timeFromNowInSeconds <= 0) return@forEach

                val id: Int = item.id.hashCode()

                val intent: Intent = getIntent().apply {
                    putExtra(
                        NotificationUtils.PARAM_KEY,
                        NotificationUtils.modelToJson(item)
                    )
                }

                val pendingIntent: PendingIntent = getPendingIntent(id, intent)

                val futureInMillis: Long = System.currentTimeMillis() + timeFromNowInSeconds

                AlarmManagerCompat.setExactAndAllowWhileIdle(
                    alarmManager,
                    AlarmManager.RTC_WAKEUP,
                    futureInMillis,
                    pendingIntent
                )
            }
        }.onFailure { e ->
            logService.e(throwable = e, report = true)
        }

        // Update Widgets
        kotlin.runCatching {
            AndroidWidgetsService.triggerUpdate(context = appContext)
        }.onFailure { e ->
            logService.e(throwable = e, report = true)
        }

        return true
    }

    actual suspend fun cancelNotifications(ids: List<String>): Boolean {
        try {
            ids.forEach { item ->
                val intent: Intent = getIntent()
                val pendingIntent: PendingIntent = getPendingIntent(item.hashCode(), intent)
                alarmManager.cancel(pendingIntent)
            }
        } catch (e: Throwable) {
            logService.e(throwable = e, report = true)
        }
        return true
    }

    //endregion

    //region Helpers

    private fun updateNotificationChannels() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val sharedAppSound = settingsService.appSoundModel.appSound
                NotificationUtils.updateNotificationChannels(appContext, sharedAppSound)
            }
        } catch (e: Throwable) {
            logService.e(throwable = e, report = true)
        }
    }

    private fun getPendingIntent(id: Int, intent: Intent) = PendingIntent.getBroadcast(
        appContext,
        id,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    private fun getIntent() =
        Intent(appContext, SingleNotificationBroadcastReceiver::class.java)

    //endregion
}
