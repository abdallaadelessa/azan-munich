package com.alifwyaa.azanmunich.domain.internal.notifications.local

import com.alifwyaa.azanmunich.domain.model.SharedNotificationModel
import com.alifwyaa.azanmunich.domain.model.SharedPlatformInfo
import com.alifwyaa.azanmunich.domain.services.SharedDateTimeService
import com.alifwyaa.azanmunich.domain.services.SharedLogService
import com.alifwyaa.azanmunich.domain.services.SharedSettingsService

/**
 * Platform-specific local notification management.
 *
 * Handles scheduling, canceling, and managing local push notifications for prayer times.
 * Also manages widget updates. Provides permission checking and requesting capabilities.
 *
 * @author Created by Abdullah Essa on 06.06.21
 */
@Suppress("EmptyDefaultConstructor")
expect class SharedLocalNotificationsService(
    platformInfo: SharedPlatformInfo,
    dateTimeService: SharedDateTimeService,
    settingsService: SharedSettingsService,
    logService: SharedLogService,
) {
    /**
     * Checks if notification permission is granted.
     *
     * @return true if permission is granted, false otherwise
     */
    suspend fun isPermissionGranted(): Boolean

    /**
     * Requests notification permission from the user.
     *
     * @return true if permission was granted, false if denied
     */
    suspend fun requestPermission(): Boolean

    /**
     * Checks if a notification is already scheduled.
     *
     * @param id The notification ID to check
     * @return true if the notification is scheduled, false otherwise
     */
    suspend fun isNotificationAdded(id: String): Boolean

    /**
     * Schedules multiple notifications.
     *
     * @param models List of notification models to schedule
     * @return true if successful, false otherwise
     */
    suspend fun addNotifications(models: List<SharedNotificationModel>): Boolean

    /**
     * Cancels scheduled notifications.
     *
     * @param ids List of notification IDs to cancel
     * @return true if successful, false otherwise
     */
    suspend fun cancelNotifications(ids: List<String>): Boolean
}