package com.alifwyaa.azanmunich.domain.internal.notifications.remote.service

import android.annotation.SuppressLint
import com.alifwyaa.azanmunich.domain.internal.notifications.remote.SharedRemoteNotificationsService
import com.alifwyaa.azanmunich.domain.services.SharedLogService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Firebase Cloud Messaging service for handling push notifications.
 *
 * Handles incoming FCM messages and token refresh events.
 * Displays notifications and tracks analytics events.
 *
 * @author Created by Abdullah Essa on 25.10.25.
 */
class AzanFirebaseMessagingService : FirebaseMessagingService(), KoinComponent {
    private val logService by inject<SharedLogService>()
    private val remoteNotificationsService by inject<SharedRemoteNotificationsService>()

    /**
     * Called when a new FCM token is generated.
     *
     * This happens on initial app installation and whenever the token is refreshed.
     *
     * @param token The new FCM token
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        logService.m { "FCM token refreshed: $token" }
    }

    /**
     * Called when a message is received.
     *
     * Handles both notification and data messages.
     *
     * @param message The received remote message
     */
    @SuppressLint("MissingPermission")
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val remoteMessageNotification = message.notification
        val remoteMessageData = message.data

        logService.m {
            """
            FCM message received from: ${message.from}
            Notification: ${remoteMessageNotification?.title} - ${remoteMessageNotification?.body}
            Data: $remoteMessageData
            """.trimIndent()
        }

        // Handle data payload
        runCatching {
            remoteNotificationsService.handleDataPayload(remoteMessageData)
        }.onFailure {
            logService.e(throwable = it, report = true)
        }

        // Handle notification payload
        runCatching {
            val permissionGranted = runBlocking { remoteNotificationsService.isPermissionGranted() }
            if (permissionGranted) {
                remoteMessageNotification?.let { notification ->
                    remoteNotificationsService.showFcmNotification(
                        context = application,
                        title = notification.title,
                        body = notification.body,
                        data = remoteMessageData
                    )
                }
            }
        }.onFailure {
            logService.e(throwable = it, report = true)
        }
    }
}