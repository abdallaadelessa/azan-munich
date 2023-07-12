package com.alifwyaa.azanmunich.domain.internal.platform

import cocoapods.FirebaseCore.FIRApp
import cocoapods.FirebaseCrashlytics.FIRCrashlytics
import cocoapods.FirebaseCrashlytics.FIRExceptionModel
import com.alifwyaa.azanmunich.domain.services.SharedAppScope
import com.alifwyaa.azanmunich.domain.model.SharedPlatformInfo
import com.alifwyaa.azanmunich.domain.services.SharedSettingsService

/**
 * @author Created by Abdullah Essa on 01.10.21.
 */
actual class SharedCrashReportingService actual constructor(
    private val platformInfo: SharedPlatformInfo,
    appScope: SharedAppScope,
    settingsService: SharedSettingsService
) {
    private val crashlytics: FIRCrashlytics
        get() = FIRCrashlytics.Companion.crashlytics()

    /**
     * Init
     */
    internal actual fun init() {
        FIRApp.configure()
        crashlytics.setCrashlyticsCollectionEnabled(platformInfo.isRelease)
    }

    /**
     * Update primary color
     */
    actual fun updateInstaBubblePrimaryColor(hexColor: String) {
        // do nothing
    }

    /**
     * Report bug
     */
    actual fun report(throwable: Throwable) {
        val error = FIRExceptionModel.exceptionModelWithName(
            name = throwable.message ?: throwable::class.simpleName.orEmpty(),
            reason = throwable.stackTraceToString()
        )
        crashlytics.recordExceptionModel(error)
    }

}
