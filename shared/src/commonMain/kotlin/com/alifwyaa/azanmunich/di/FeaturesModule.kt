package com.alifwyaa.azanmunich.di

import com.alifwyaa.azanmunich.domain.internal.platform.SharedInAppReviewService
import com.alifwyaa.azanmunich.domain.services.SharedWidgetsDataService
import org.koin.dsl.module

/**
 * @author Created by Abdullah Essa on 25.02.22.
 */
fun getFeaturesModule() = module {
    single {
        SharedInAppReviewService(
            platformInfo = get(),
            appScope = get(),
            logService = get()
        )
    }

    single {
        SharedWidgetsDataService(
            azanService = get(),
            localizationService = get(),
            settingsService = get(),
            dateTimeService = get(),
            logService = get(),
        )
    }
}
