package com.alifwyaa.azanmunich.di

import com.alifwyaa.azanmunich.domain.internal.notifications.remote.SharedRemoteNotificationsService
import com.alifwyaa.azanmunich.domain.internal.tracking.SharedCrashReportingService
import com.alifwyaa.azanmunich.domain.internal.tracking.SharedTrackingService
import com.alifwyaa.azanmunich.domain.internal.widgets.SharedCoreWidgetsService
import com.alifwyaa.azanmunich.domain.services.SharedAppScope
import com.alifwyaa.azanmunich.domain.services.SharedLocalizationService
import com.alifwyaa.azanmunich.domain.services.SharedLogService
import com.alifwyaa.azanmunich.domain.services.SharedPreferenceService
import org.koin.dsl.module

/**
 * @author Created by Abdullah Essa on 25.02.22.
 */
fun getToolsModule() = module {

    single {
        SharedAppScope()
    }

    single {
        SharedLocalizationService { get() }
    }

    single {
        SharedLogService(
            platformInfo = get(),
            crashReportingService = get()
        )
    }

    single {
        SharedTrackingService(platformInfo = get())
    }

    single {
        SharedCrashReportingService(
            platformInfo = get(),
            appScope = get(),
            settingsService = get()
        )
    }

    single {
        SharedPreferenceService(logService = get())
    }

    single {
        SharedRemoteNotificationsService(
            platformInfo = get(),
            logService = get(),
            schedulerService = get()
        )
    }

    single {
        SharedCoreWidgetsService(
            platformInfo = get(),
            logService = get(),
        )
    }
}
