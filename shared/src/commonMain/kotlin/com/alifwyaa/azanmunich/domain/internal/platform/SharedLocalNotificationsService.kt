@file:Suppress("UnusedImports")

package com.alifwyaa.azanmunich.domain.internal.platform

import com.alifwyaa.azanmunich.domain.model.SharedNotificationModel
import com.alifwyaa.azanmunich.domain.model.SharedPlatformInfo
import com.alifwyaa.azanmunich.domain.services.SharedDateTimeService
import com.alifwyaa.azanmunich.domain.services.SharedLogService
import com.alifwyaa.azanmunich.domain.services.SharedSettingsService

/**
 * @author Created by Abdullah Essa on 06.06.21.
 */
@Suppress("EmptyDefaultConstructor")
expect class SharedLocalNotificationsService(
    platformInfo: SharedPlatformInfo,
    dateTimeService: SharedDateTimeService,
    settingsService: SharedSettingsService,
    logService: SharedLogService,
) {

    /**
     * Check the notification permission is granted
     */
    suspend fun isPermissionGranted(): Boolean

    /**
     * Request the notification permission
     */
    suspend fun requestPermission(): Boolean

    /**
     * Check if the notification is already added to the pending notifications
     */
    suspend fun isNotificationAdded(id: String): Boolean

    /**
     * Add notification to the pending notifications
     */
    suspend fun addNotifications(models: List<SharedNotificationModel>): Boolean

    /**
     * remove notification from the pending notifications
     */
    suspend fun cancelNotifications(ids: List<String>): Boolean
}
