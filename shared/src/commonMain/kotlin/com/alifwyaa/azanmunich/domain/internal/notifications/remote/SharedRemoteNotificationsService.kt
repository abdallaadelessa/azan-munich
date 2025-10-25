package com.alifwyaa.azanmunich.domain.internal.notifications.remote

import com.alifwyaa.azanmunich.domain.model.SharedPlatformInfo
import com.alifwyaa.azanmunich.domain.services.SharedLogService
import com.alifwyaa.azanmunich.domain.services.SharedNotificationSchedulerService

/**
 * Platform-specific Firebase Cloud Messaging (FCM) service.
 *
 * Handles push notification token management, topic subscriptions,
 * and message handling for remote notifications.
 *
 * @author Created by Abdullah Essa on 25.10.25.
 */
expect class SharedRemoteNotificationsService(
    platformInfo: SharedPlatformInfo,
    logService: SharedLogService,
    schedulerService: SharedNotificationSchedulerService,
) {

    /**
     * Checks if the app has permission to receive push notifications.
     *
     * @return True if permission is granted, false otherwise
     */
    suspend fun isPermissionGranted(): Boolean

    /**
     * Retrieves the current FCM token.
     *
     * @return The FCM token, or null if unavailable
     */
    suspend fun getToken(): String?

    /**
     * Subscribes to an FCM topic.
     *
     * @param topic The topic name to subscribe to
     */
    suspend fun subscribeToTopic(topic: String)

    /**
     * Unsubscribes from an FCM topic.
     *
     * @param topic The topic name to unsubscribe from
     */
    suspend fun unsubscribeFromTopic(topic: String)

    /**
     * Deletes the current FCM token.
     *
     * Use this when the user logs out or wants to disable push notifications.
     */
    suspend fun deleteToken()
}