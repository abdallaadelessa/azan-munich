package com.alifwyaa.azanmunich.di

import com.alifwyaa.azanmunich.domain.internal.reviews.SharedInAppReviewService
import com.alifwyaa.azanmunich.domain.services.SharedWidgetsService
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
        SharedWidgetsService(
            azanService = get(),
            localizationService = get(),
            settingsService = get(),
            dateTimeService = get(),
            logService = get(),
            coreWidgetsService = get(),
        )
    }
}
