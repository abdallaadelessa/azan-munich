package com.alifwyaa.azanmunich.domain

import com.alifwyaa.azanmunich.data.internal.platform.SharedAzanDataSource
import com.alifwyaa.azanmunich.di.getAppModule
import com.alifwyaa.azanmunich.di.getDataModule
import com.alifwyaa.azanmunich.di.getFeaturesModule
import com.alifwyaa.azanmunich.di.getNotificationsModule
import com.alifwyaa.azanmunich.di.getToolsModule
import com.alifwyaa.azanmunich.domain.internal.platform.SharedCrashReportingService
import com.alifwyaa.azanmunich.domain.internal.platform.SharedTrackingService
import com.alifwyaa.azanmunich.domain.model.SharedPlatformInfo
import com.alifwyaa.azanmunich.domain.services.SharedAppScope
import com.alifwyaa.azanmunich.domain.services.SharedAzanService
import com.alifwyaa.azanmunich.domain.services.SharedDateTimeService
import com.alifwyaa.azanmunich.domain.services.SharedLocalizationService
import com.alifwyaa.azanmunich.domain.services.SharedLogService
import com.alifwyaa.azanmunich.domain.services.SharedNotificationSchedulerService
import com.alifwyaa.azanmunich.domain.services.SharedSettingsService
import com.alifwyaa.azanmunich.domain.services.SharedUserService
import com.alifwyaa.azanmunich.domain.services.SharedWidgetsDataService
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

/**
 * @author Created by Abdullah Essa on 04.06.21.
 */
class SharedApp(val platformInfo: SharedPlatformInfo) {

    //region Koin Dependency Injection

    private val koinApplication: KoinApplication by lazy {
        startKoin {
            modules(
                getAppModule(platformInfo = platformInfo),
                getDataModule(),
                getToolsModule(),
                getFeaturesModule(),
                getNotificationsModule(),
            )
        }
    }

    private val koin: Koin get() = koinApplication.koin

    //endregion

    //region Exposed Services

    /**
     * App Scope
     */
    val appScope: SharedAppScope by koin.inject()

    /**
     * The Logging service
     */
    val logService: SharedLogService by koin.inject()

    /**
     * The Tracking service
     */
    val trackingService: SharedTrackingService by koin.inject()

    /**
     * The localization service
     */
    val localizationService: SharedLocalizationService by koin.inject()

    /**
     * The date time service
     */
    val dateTimeService: SharedDateTimeService by koin.inject()

    /**
     * The azan service
     */
    val azanService: SharedAzanService by koin.inject()

    /**
     * The azan scheduler service
     */
    val notificationSchedulerService: SharedNotificationSchedulerService by koin.inject()

    /**
     * The settings service
     */
    val settingsService: SharedSettingsService by koin.inject()

    /**
     * The widgets data service
     */
    val widgetsDataService: SharedWidgetsDataService by koin.inject()

    //endregion

    //region Public Methods

    /**
     * Called from
     * the onCreate of the application class in android
     * or from the application() didFinishLaunching in ios
     */
    fun onCreate() {
        // Order matters
        koin.get<SharedCrashReportingService>().init()
        koin.get<SharedTrackingService>().init()
        koin.get<SharedAzanDataSource>().init()
    }

    /**
     * Called from
     * the onCreate of the MainActivity class in android
     * or from the ContentView in ios
     *
     * @param view is the main activity in case of android
     */
    fun onViewCreated(view: Any = Unit) {
        koin.get<SharedUserService>().onViewCreated(view)
    }

    /**
     * Called whenever the theme is changed
     */
    fun onThemeChanged(isDarkTheme: Boolean) {
        koin.get<SharedCrashReportingService>()
            .updateInstaBubblePrimaryColor(if (isDarkTheme) "#202020" else "#FFAB72")
    }

    //endregion
}
