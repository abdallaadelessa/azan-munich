package com.alifwyaa.azanmunich.domain.internal.notifications.local

import android.Manifest
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver.SCHEME_ANDROID_RESOURCE
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.alifwyaa.azanmunich.data.internal.model.SharedAzanType
import com.alifwyaa.azanmunich.domain.extensions.toNotificationSound
import com.alifwyaa.azanmunich.domain.internal.notifications.local.receiver.SingleNotificationBroadcastReceiver
import com.alifwyaa.azanmunich.domain.model.SharedNotificationModel
import com.alifwyaa.azanmunich.domain.model.SharedPlatformInfo
import com.alifwyaa.azanmunich.domain.model.settings.SharedAppSound
import com.alifwyaa.azanmunich.domain.services.SharedDateTimeService
import com.alifwyaa.azanmunich.domain.services.SharedLogService
import com.alifwyaa.azanmunich.domain.services.SharedNotificationSchedulerService
import com.alifwyaa.azanmunich.domain.services.SharedSettingsService
import com.alifwyaa.azanmunich.extensions.getLaunchPendingIntent
import com.alifwyaa.azanmunich.extensions.isPostNotificationPermissionGranted
import com.alifwyaa.azanmunich.shared.R
import kotlinx.serialization.json.Json
import kotlin.time.DurationUnit

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
        createLocalNotificationChannels(appContext)
    }

    //region Configuration

    companion object {
        const val PARAM_KEY = "json_shared_notification_model"
    }

    fun modelToJson(model: SharedNotificationModel): String =
        Json.encodeToString(model)

    fun modelFromJson(modelInJson: String): SharedNotificationModel =
        Json.decodeFromString(modelInJson)

    //endregion

    //region Sound

    private fun getNotificationSoundUriForNotificationChannel(
        context: Context,
        sharedSettingsAppSound: SharedAppSound,
        sharedAzanType: SharedAzanType
    ): Uri {
        val notificationSound: SharedNotificationModel.Sound =
            sharedSettingsAppSound.toNotificationSound(sharedAzanType)

        return getNotificationSoundUriForNotification(
            context = context,
            sound = notificationSound
        )
    }

    private fun getNotificationSoundUriForNotification(
        context: Context,
        sound: SharedNotificationModel.Sound,
    ): Uri = when (sound) {
        SharedNotificationModel.Sound.DEFAULT ->
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        SharedNotificationModel.Sound.SOUND1_FAJR,
        SharedNotificationModel.Sound.SOUND1_OTHERS ->
            "${SCHEME_ANDROID_RESOURCE}://${context.packageName}/${R.raw.azan_sound1}".toUri()
    }

    //endregion

    //region SharedLocalNotificationsService

    actual suspend fun isPermissionGranted(): Boolean =
        isPostNotificationPermissionGranted(appContext)

    actual suspend fun requestPermission(): Boolean =
        error("Permission request should not be handled here.")

    actual suspend fun isNotificationAdded(id: String): Boolean = true

    actual suspend fun addNotifications(models: List<SharedNotificationModel>): Boolean {
        // Schedule Notifications
        runCatching {
            createLocalNotificationChannels(appContext)
            models.forEach { item ->
                val timeFromNowInSeconds: Long = dateTimeService.getDurationUntil(
                    dateModel = item.dateModel,
                    timeModel = item.timeModel
                ).toDouble(DurationUnit.MILLISECONDS).toLong()

                if (timeFromNowInSeconds <= 0) return@forEach

                val id: Int = item.id.hashCode()

                val intent: Intent = getLocalBroadcastIntent().apply {
                    putExtra(
                        PARAM_KEY,
                        modelToJson(item)
                    )
                }

                val pendingIntent: PendingIntent = getLocalBroadcastPendingIntent(id, intent)

                val futureInMillis: Long = System.currentTimeMillis() + timeFromNowInSeconds

                if (AlarmManagerCompat.canScheduleExactAlarms(alarmManager)) {
                    AlarmManagerCompat.setExactAndAllowWhileIdle(
                        alarmManager,
                        AlarmManager.RTC_WAKEUP,
                        futureInMillis,
                        pendingIntent
                    )
                }
            }
        }.onFailure { e ->
            logService.e(throwable = e, report = true)
        }

        return true
    }

    actual suspend fun cancelNotifications(ids: List<String>): Boolean {
        try {
            ids.forEach { item ->
                val intent: Intent = getLocalBroadcastIntent()
                val pendingIntent: PendingIntent =
                    getLocalBroadcastPendingIntent(item.hashCode(), intent)
                alarmManager.cancel(pendingIntent)
            }
        } catch (e: Throwable) {
            logService.e(throwable = e, report = true)
        }
        return true
    }

    private fun getLocalBroadcastPendingIntent(id: Int, intent: Intent) =
        PendingIntent.getBroadcast(
            appContext,
            id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

    private fun getLocalBroadcastIntent() =
        Intent(appContext, SingleNotificationBroadcastReceiver::class.java)

    //endregion

    //region Show Notification

    fun createLocalNotificationChannels(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val sharedAppSound = settingsService.appSoundModel.appSound
                createLocalNotificationChannels(context, sharedAppSound)
            }
        } catch (e: Throwable) {
            logService.e(throwable = e, report = true)
        }
    }

    private fun createLocalNotificationChannels(context: Context, appSound: SharedAppSound) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = NotificationManagerCompat.from(context)

            // Delete all channels
            notificationManager.notificationChannels.forEach {
                notificationManager.deleteNotificationChannel(it.id)
            }

            SharedAzanType.entries.forEach { azanType ->
                val channelId = SharedNotificationSchedulerService.getChannelId(
                    sharedAzanType = azanType,
                    sharedAppSound = appSound
                )

                val channelName: String =
                    azanType.toString().lowercase().replaceFirstChar { it.uppercase() }

                val channelSoundUri = getNotificationSoundUriForNotificationChannel(
                    context = context,
                    sharedSettingsAppSound = appSound,
                    sharedAzanType = azanType
                )

                val notificationChannel: NotificationChannel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Notifications for $channelName"
                    enableLights(true)
                    enableVibration(true)
                    lightColor = Color.YELLOW
                    setSound(
                        channelSoundUri,
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setLegacyStreamType(AudioManager.STREAM_ALARM)
                            .build()
                    )
                }

                notificationManager.createNotificationChannel(notificationChannel)
            }
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showLocalNotification(context: Context, model: SharedNotificationModel) {
        val notificationManager: NotificationManagerCompat =
            NotificationManagerCompat.from(context)

        val channelId: String = model.categoryId

        // Verify channel exists on Android 8+ (Oreo)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val existingChannel = notificationManager.getNotificationChannel(channelId)
            if (existingChannel == null) {
                createLocalNotificationChannels(context)
            }
        }

        val builder = NotificationCompat.Builder(context, channelId).apply {
            setChannelId(channelId)
            setSmallIcon(R.drawable.ic_notification_azan)
            setColor(ContextCompat.getColor(context, R.color.colorBackground))
            setDefaults(Notification.DEFAULT_LIGHTS)
            setContentText(model.title)
            setCategory(NotificationCompat.CATEGORY_ALARM)
            setPriority(NotificationCompat.PRIORITY_MAX)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setAutoCancel(true)
            setContentIntent(getLaunchPendingIntent(context))
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                setSound(
                    getNotificationSoundUriForNotification(context = context, sound = model.sound),
                    AudioManager.STREAM_ALARM
                )
                setLights(Color.YELLOW, 500, 500)
            }
        }

        notificationManager.notify(model.id.hashCode(), builder.build())
    }

    //endregion
}