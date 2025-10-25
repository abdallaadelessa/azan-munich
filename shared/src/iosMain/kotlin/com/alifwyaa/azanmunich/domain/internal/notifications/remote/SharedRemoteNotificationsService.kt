package com.alifwyaa.azanmunich.domain.internal.platform

import com.alifwyaa.azanmunich.domain.model.SharedPlatformInfo
import com.alifwyaa.azanmunich.domain.services.SharedLogService

/**
 * iOS implementation of messaging service (stub).
 *
 * This is a placeholder implementation. iOS push notifications can be implemented
 * using Apple Push Notification Service (APNs) in the future.
 *
 * @author Created by Abdullah Essa on 25.10.25.
 */
actual class SharedRemoteNotificationsService(
    platformInfo: SharedPlatformInfo,
    private val logService: SharedLogService,
) {
    /**
     * Retrieves the current FCM token (iOS not implemented).
     *
     * @return null (not implemented for iOS)
     */
    actual suspend fun getToken(): String? {
        logService.m { "FCM getToken not implemented for iOS" }
        return null
    }

    /**
     * Subscribes to an FCM topic (iOS not implemented).
     *
     * @param topic The topic name to subscribe to
     */
    actual suspend fun subscribeToTopic(topic: String) {
        logService.m { "FCM subscribeToTopic not implemented for iOS" }
    }

    /**
     * Unsubscribes from an FCM topic (iOS not implemented).
     *
     * @param topic The topic name to unsubscribe from
     */
    actual suspend fun unsubscribeFromTopic(topic: String) {
        logService.m { "FCM unsubscribeFromTopic not implemented for iOS" }
    }

    /**
     * Deletes the current FCM token (iOS not implemented).
     */
    actual suspend fun deleteToken() {
        logService.m { "FCM deleteToken not implemented for iOS" }
    }
}
