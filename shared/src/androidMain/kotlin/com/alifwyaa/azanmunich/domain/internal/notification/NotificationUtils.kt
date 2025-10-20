package com.alifwyaa.azanmunich.domain.internal.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver.SCHEME_ANDROID_RESOURCE
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.alifwyaa.azanmunich.data.internal.model.SharedAzanType
import com.alifwyaa.azanmunich.domain.extensions.toNotificationSound
import com.alifwyaa.azanmunich.domain.model.SharedNotificationModel
import com.alifwyaa.azanmunich.domain.model.settings.SharedAppSound
import com.alifwyaa.azanmunich.domain.services.SharedNotificationSchedulerService
import com.alifwyaa.azanmunich.shared.R
import kotlinx.serialization.json.Json

/**
 * @author Created by Abdullah Essa on 12.09.21.
 */
object NotificationUtils {

    //region Notification Json

    const val PARAM_KEY = "json_shared_notification_model"

    fun modelToJson(model: SharedNotificationModel): String =
        Json.encodeToString(model)

    fun modelFromJson(modelInJson: String): SharedNotificationModel =
        Json.decodeFromString(modelInJson)

    //endregion

    //region Notification Sound

    private fun getNotificationSoundUriForNotificationChannel(
        appContext: Context,
        sharedSettingsAppSound: SharedAppSound,
        sharedAzanType: SharedAzanType
    ): Uri {
        val notificationSound: SharedNotificationModel.Sound =
            sharedSettingsAppSound.toNotificationSound(sharedAzanType)

        return getNotificationSoundUriForNotification(
            appContext = appContext,
            sound = notificationSound
        )
    }

    private fun getNotificationSoundUriForNotification(
        appContext: Context,
        sound: SharedNotificationModel.Sound,
    ): Uri = when (sound) {
        SharedNotificationModel.Sound.DEFAULT ->
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        SharedNotificationModel.Sound.SOUND1_FAJR,
        SharedNotificationModel.Sound.SOUND1_OTHERS ->
            "${SCHEME_ANDROID_RESOURCE}://${appContext.packageName}/${R.raw.azan_sound1}".toUri()
    }

    //endregion

    //region Notification Channels

    fun updateNotificationChannels(context: Context, appSound: SharedAppSound) {
        val notificationManager = NotificationManagerCompat.from(context)

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

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
                    appContext = context,
                    sharedSettingsAppSound = appSound,
                    sharedAzanType = azanType
                )

                val notificationChannel: NotificationChannel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    enableLights(true)
                    lightColor = Color.YELLOW
                    setSound(
                        channelSoundUri,
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .setLegacyStreamType(AudioManager.STREAM_NOTIFICATION)
                            .build()
                    )
                }

                notificationManager.createNotificationChannel(notificationChannel)
            }
        }
    }

    //endregion

    //region Show Notification

    /**
     * Check if notification permission is granted
     *
     * @param context The application context
     * @return true if permission is granted, false otherwise
     */
    fun isPermissionGranted(context: Context): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // For Android versions below 13 (API 33), notification permission is granted by default
            true
        }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showSingleNotification(context: Context, model: SharedNotificationModel) {
        val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)

        val channelId: String = model.categoryId

        val activity = getPendingIntent(context)

        val soundUri: Uri = getNotificationSoundUriForNotification(
            appContext = context,
            sound = model.sound
        )

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification_azan)
            .setContentText(model.title)
            .setSound(soundUri, AudioManager.STREAM_NOTIFICATION)
            .setContentIntent(
                activity
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(model.id.hashCode(), builder.build())
    }

    //endregion

    //region Helpers

    fun getPendingIntent(context: Context): PendingIntent = PendingIntent.getActivity(
        context,
        0,
        getIntent(context),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    private fun getIntent(context: Context): Intent? {
        val intent: Intent? = context.packageManager.getLaunchIntentForPackage(
            context.packageName
        )?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return intent
    }

    //endregion
}
