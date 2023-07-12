package com.alifwyaa.azanmunich.domain.internal.platform

import android.app.Application
import android.graphics.Color
import com.alifwyaa.azanmunich.domain.services.SharedAppScope
import com.alifwyaa.azanmunich.domain.model.SharedPlatformInfo
import com.alifwyaa.azanmunich.domain.model.events.SharedLanguageChangedEvent
import com.alifwyaa.azanmunich.domain.model.settings.SharedAppLocale
import com.alifwyaa.azanmunich.domain.services.SharedSettingsService
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.instabug.crash.CrashReporting
import com.instabug.library.Feature
import com.instabug.library.Instabug
import com.instabug.library.invocation.InstabugInvocationEvent
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * @author Created by Abdullah Essa on 01.10.21.
 */
actual class SharedCrashReportingService actual constructor(
    private val platformInfo: SharedPlatformInfo,
    private val appScope: SharedAppScope,
    private val settingsService: SharedSettingsService,
) {
    private val isRunningUnitTests: Boolean =
        (platformInfo as SharedPlatformInfo.Android).isRunningUnitTests

    private val application: Application =
        (platformInfo as SharedPlatformInfo.Android).appContext as Application

    private val instaBugSdkToken: String = if (platformInfo.isDebug) {
        "15033401d27eb6604648e77c1921ee66"
    } else {
        "0dd5b2f254a3ed98f041e46d65e8243a"
    }

    /**
     * Init
     */
    internal actual fun init() {
        if (isRunningUnitTests) return

        initFirebaseCrashlytics()
        initInstaBugSDK()
    }

    /**
     * Report bug
     */
    actual fun report(throwable: Throwable) {
        if (isRunningUnitTests) return

        // Crashlytics
        Firebase.crashlytics.recordException(throwable)
        // InstaBug
        CrashReporting.reportException(throwable)
    }

    /**
     * Primary Color
     */
    actual fun updateInstaBubblePrimaryColor(hexColor: String) {
        if (isRunningUnitTests) return

        Instabug.setPrimaryColor(Color.parseColor(hexColor))
    }

    //region Helpers

    private fun initFirebaseCrashlytics() {
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(platformInfo.isRelease)
    }

    private fun initInstaBugSDK() {
        Instabug.Builder(application, instaBugSdkToken)
            .setInvocationEvents(InstabugInvocationEvent.FLOATING_BUTTON)
            .setDebugEnabled(platformInfo.isDebug)
            .build()

        appScope.launch(SharedDispatchers.Main) {
            settingsService
                .eventsFlow
                .filterIsInstance<SharedLanguageChangedEvent>()
                .map { settingsService.appLanguageModel.appLocale }
                .collect { updateLocale(it) }
        }

        appScope.launch(SharedDispatchers.Main) {
            @Suppress("MagicNumber")
            delay(500)
            ensureActive()
            // for some reason this method does not work if it was called from the onCreate method
            updateLocale(settingsService.appLanguageModel.appLocale)
            // ANR
            CrashReporting.setAnrState(Feature.State.ENABLED)
        }
    }

    private fun updateLocale(sharedAppLocale: SharedAppLocale) {
        Instabug.setLocale(Locale(sharedAppLocale.code))
    }

    //endregion

}
