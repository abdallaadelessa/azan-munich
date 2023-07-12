package com.alifwyaa.azanmunich.di

import com.alifwyaa.azanmunich.data.internal.SharedAzanDataParser
import com.alifwyaa.azanmunich.data.internal.platform.SharedAzanDataSource
import com.alifwyaa.azanmunich.domain.services.SharedAzanService
import org.koin.dsl.module

/**
 * @author Created by Abdullah Essa on 25.02.22.
 */
fun getDataModule() = module {
    single {
        SharedAzanDataParser()
    }

    single {
        SharedAzanDataSource(parser = get())
    }

    single {
        SharedAzanService(
            dataSource = get(),
            dateTimeService = get(),
            localizationService = get(),
            logService = get(),
        )
    }
}
