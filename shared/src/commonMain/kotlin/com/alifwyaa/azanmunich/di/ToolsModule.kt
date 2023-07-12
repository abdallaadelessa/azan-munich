package com.alifwyaa.azanmunich.di

import com.alifwyaa.azanmunich.domain.internal.platform.SharedCrashReportingService
import com.alifwyaa.azanmunich.domain.internal.platform.SharedTrackingService
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
}
