package com.alifwyaa.azanmunich.di

import com.alifwyaa.azanmunich.domain.model.SharedPlatformInfo
import com.alifwyaa.azanmunich.domain.services.SharedDateTimeService
import com.alifwyaa.azanmunich.domain.services.SharedSettingsService
import com.alifwyaa.azanmunich.domain.services.SharedUserService
import org.koin.dsl.module

/**
 * @author Created by Abdullah Essa on 25.02.22.
 */
fun getAppModule(platformInfo: SharedPlatformInfo) = module {
    single {
        platformInfo
    }

    single {
        SharedUserService(
            preferenceService = get(),
            logService = get(),
            inAppReviewService = get(),
        )
    }

    single {
        SharedSettingsService(
            appScope = get(),
            logService = { get() },
            trackingService = get(),
            preferenceService = { get() },
            localizationService = { get() }
        )
    }

    single {
        SharedDateTimeService(
            settingsService = get(),
            localizationService = get()
        )
    }
}
