package com.alifwyaa.azanmunich.domain.internal.notifications.remote

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.alifwyaa.azanmunich.domain.model.SharedPlatformInfo
import com.alifwyaa.azanmunich.domain.services.SharedLogService
import com.alifwyaa.azanmunich.domain.services.SharedNotificationSchedulerService
import com.alifwyaa.azanmunich.extensions.getLaunchPendingIntent
import com.alifwyaa.azanmunich.extensions.isPostNotificationPermissionGranted
import com.alifwyaa.azanmunich.shared.R
import com.alifwyaa.azanmunich.workers.AzanPeriodicJobScheduler
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

/**
 * Android implementation of Firebase Cloud Messaging service.
 *
 * Wraps Firebase Messaging SDK to provide token management and topic subscriptions.
 *
 * @author Created by Abdullah Essa on 25.10.25.
 */
actual class SharedRemoteNotificationsService actual constructor(
    platformInfo: SharedPlatformInfo,
    private val logService: SharedLogService,
    private val schedulerService: SharedNotificationSchedulerService,
) {

    private val appContext: Context =
        (platformInfo as SharedPlatformInfo.Android).appContext as Context

    private val firebaseMessaging by lazy { FirebaseMessaging.getInstance() }

    init {
        createFcmNotificationChannel(appContext)
    }

    //region Configuration

    companion object {
        const val FCM_CHANNEL_ID = "fcm_default_channel"
        const val FCM_CHANNEL_NAME = "Push Notifications"
        const val PAYLOAD_KEY_REFRESH_PRAYER_TIMES = "refresh"
    }

    //endregion

    //region Token & Topic Management

    /**
     * Retrieves the current FCM token.
     *
     * @return The FCM token, or null if unavailable
     */
    actual suspend fun getToken(): String? = try {
        val token = firebaseMessaging.token.await()
        logService.m { "FCM Token retrieved: $token" }
        token
    } catch (e: Exception) {
        logService.e(throwable = e, report = true) {
            appendLine("Failed to get FCM token")
        }
        null
    }

    /**
     * Deletes the current FCM token.
     *
     * Use this when the user logs out or wants to disable push notifications.
     */
    actual suspend fun deleteToken() {
        try {
            firebaseMessaging.deleteToken().await()
            logService.m { "FCM token deleted" }
        } catch (e: Exception) {
            logService.e(throwable = e, report = true) {
                appendLine("Failed to delete FCM token")
            }
        }
    }

    /**
     * Subscribes to an FCM topic.
     *
     * @param topic The topic name to subscribe to
     */
    actual suspend fun subscribeToTopic(topic: String) {
        try {
            firebaseMessaging.subscribeToTopic(topic).await()
            logService.m { "Subscribed to FCM topic: $topic" }
        } catch (e: Exception) {
            logService.e(throwable = e, report = true) {
                appendLine("Failed to subscribe to FCM topic: $topic")
            }
        }
    }

    /**
     * Unsubscribes from an FCM topic.
     *
     * @param topic The topic name to unsubscribe from
     */
    actual suspend fun unsubscribeFromTopic(topic: String) {
        try {
            firebaseMessaging.unsubscribeFromTopic(topic).await()
            logService.m { "Unsubscribed from FCM topic: $topic" }
        } catch (e: Exception) {
            logService.e(throwable = e, report = true) {
                appendLine("Failed to unsubscribe from FCM topic: $topic")
            }
        }
    }

    //endregion

    //region Show Notification

    actual suspend fun isPermissionGranted(): Boolean =
        isPostNotificationPermissionGranted(appContext)

    /**
     * Creates a notification channel for FCM messages.
     * @param context The application context
     * @return true if channel was created successfully
     */
    fun createFcmNotificationChannel(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = NotificationManagerCompat.from(context)
            val channel = NotificationChannel(
                FCM_CHANNEL_ID,
                FCM_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Firebase Cloud Messaging notifications"
                enableLights(true)
                enableVibration(true)
                setSound(
                    getNotificationSoundUri(),
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setLegacyStreamType(AudioManager.STREAM_ALARM)
                        .build()
                )
            }

            notificationManager.createNotificationChannel(channel)
            return true
        }
        return false
    }

    /**
     * Shows an FCM notification.
     *
     * @param channelId The notification channel ID
     * @param title The notification title
     * @param body The notification body
     * @param data Additional data payload
     */
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showFcmNotification(
        context: Context,
        title: String?,
        body: String?,
        data: Map<String, String> = emptyMap()
    ) {
        val notificationManager: NotificationManagerCompat =
            NotificationManagerCompat.from(context)

        val channelId: String = FCM_CHANNEL_ID

        // Verify channel exists on Android 8+ (Oreo)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val existingChannel = notificationManager.getNotificationChannel(channelId)
            if (existingChannel == null) {
                createFcmNotificationChannel(context)
            }
        }

        // Build notification
        val builder = NotificationCompat.Builder(context, channelId).apply {
            setChannelId(channelId)
            setSmallIcon(R.drawable.ic_notification_azan)
            setColor(ContextCompat.getColor(context, R.color.colorBackground))
            setDefaults(Notification.DEFAULT_LIGHTS)
            setContentTitle(title ?: "Azan Munich")
            setContentText(body.orEmpty())
            setCategory(NotificationCompat.CATEGORY_MESSAGE)
            setPriority(NotificationCompat.PRIORITY_HIGH)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setAutoCancel(true)
            setContentIntent(getLaunchPendingIntent(context))
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                setSound(
                    getNotificationSoundUri(),
                    AudioManager.STREAM_NOTIFICATION
                )
            }
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    private fun getNotificationSoundUri(): Uri =
        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

    //endregion

    //region Data Payload Handling

    /**
     * Handles data payload from FCM message.
     *
     * Process custom data sent from the server.
     *
     * @param data The data payload
     */
    fun handleDataPayload(data: Map<String, String>) {
        if (data.isEmpty()) return
        when {
            data.containsKey(PAYLOAD_KEY_REFRESH_PRAYER_TIMES) -> {
                // Trigger prayer times refresh
                logService.m { "Triggering prayer times refresh from FCM data payload" }
                AzanPeriodicJobScheduler.schedule(appContext)
            }
        }
    }

    //endregion
}