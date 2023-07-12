package com.alifwyaa.azanmunich.di

import com.alifwyaa.azanmunich.domain.internal.platform.SharedLocalNotificationsService
import com.alifwyaa.azanmunich.domain.services.SharedNotificationSchedulerService
import org.koin.dsl.module

/**
 * @author Created by Abdullah Essa on 25.02.22.
 */
fun getNotificationsModule() = module {
    single {
        SharedLocalNotificationsService(
            platformInfo = get(),
            dateTimeService = get(),
            settingsService = get(),
            logService = get()
        )
    }

    single {
        SharedNotificationSchedulerService(
            appScope = get(),
            logService = get(),
            localizationService = get(),
            dateTimeService = get(),
            azanService = get(),
            settingsService = get(),
            localNotificationsService = get(),
            preferenceService = get(),
        )
    }
}
