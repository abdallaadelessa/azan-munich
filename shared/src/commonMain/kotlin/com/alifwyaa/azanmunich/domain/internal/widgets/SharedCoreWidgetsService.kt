package com.alifwyaa.azanmunich.domain.internal.widgets

import com.alifwyaa.azanmunich.domain.model.SharedPlatformInfo
import com.alifwyaa.azanmunich.domain.services.SharedLogService

/**
 * Core service for managing home screen widgets across platforms.
 *
 * @author Created by Abdullah Essa on 24.10.21.
 */
expect class SharedCoreWidgetsService(
    platformInfo: SharedPlatformInfo,
    logService: SharedLogService,
) {
    /**
     * Triggers widget updates on the platform.
     *
     * Notifies home screen widgets to refresh their data.
     */
    suspend fun notifyWidgets()
}