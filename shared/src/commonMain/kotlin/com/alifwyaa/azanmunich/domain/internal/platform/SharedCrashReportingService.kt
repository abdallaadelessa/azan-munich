package com.alifwyaa.azanmunich.domain.internal.platform

import com.alifwyaa.azanmunich.domain.services.SharedAppScope
import com.alifwyaa.azanmunich.domain.model.SharedPlatformInfo
import com.alifwyaa.azanmunich.domain.services.SharedSettingsService

/**
 * @author Created by Abdullah Essa on 01.10.21.
 */
expect class SharedCrashReportingService(
    platformInfo: SharedPlatformInfo,
    appScope: SharedAppScope,
    settingsService: SharedSettingsService
) {
    /**
     * Init
     */
    internal fun init()

    /**
     * Update primary color
     */
    fun updateInstaBubblePrimaryColor(hexColor: String)

    /**
     * Report bug
     */
    fun report(throwable: Throwable)
}
