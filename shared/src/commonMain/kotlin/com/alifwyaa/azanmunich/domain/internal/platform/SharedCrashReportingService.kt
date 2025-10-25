package com.alifwyaa.azanmunich.domain.internal.platform

import com.alifwyaa.azanmunich.domain.services.SharedAppScope
import com.alifwyaa.azanmunich.domain.model.SharedPlatformInfo
import com.alifwyaa.azanmunich.domain.services.SharedSettingsService

/**
 * Platform-specific crash reporting and bug tracking.
 *
 * Integrates with platform-specific crash reporting services (e.g., Crashlytics).
 * Handles crash initialization, customization, and error reporting.
 *
 * @author Created by Abdullah Essa on 01.10.21.
 */
expect class SharedCrashReportingService(
    platformInfo: SharedPlatformInfo,
    appScope: SharedAppScope,
    settingsService: SharedSettingsService
) {
    /**
     * Initializes the crash reporting service.
     *
     * Called during app startup to configure crash reporting.
     */
    internal fun init()

    /**
     * Updates the primary color for the bug reporting UI.
     *
     * @param hexColor Hex color code (e.g., "#FF5733")
     */
    fun updateInstaBubblePrimaryColor(hexColor: String)

    /**
     * Reports an error to the crash reporting service.
     *
     * @param throwable The error to report
     */
    fun report(throwable: Throwable)
}
