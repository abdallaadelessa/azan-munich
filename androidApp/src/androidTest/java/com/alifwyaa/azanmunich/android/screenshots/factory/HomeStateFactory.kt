package com.alifwyaa.azanmunich.android.screenshots.factory

import com.alifwyaa.azanmunich.android.screenshots.AppConfig
import com.alifwyaa.azanmunich.android.screenshots.AppConfig.azanTimes
import com.alifwyaa.azanmunich.android.ui.screens.common.DataPlaceHolder
import com.alifwyaa.azanmunich.android.ui.screens.home.HomeViewModel
import com.alifwyaa.azanmunich.data.internal.model.SharedAzanType
import com.alifwyaa.azanmunich.domain.SharedApp
import com.alifwyaa.azanmunich.domain.SharedStrings
import com.alifwyaa.azanmunich.domain.model.SharedAzanModel
import com.alifwyaa.azanmunich.domain.model.SharedDateModel
import com.alifwyaa.azanmunich.domain.model.SharedTimeModel
import com.alifwyaa.azanmunich.domain.model.SharedTimeUntil

object HomeStateFactory {
    private fun getAzanList(sharedApp: SharedApp) = DataPlaceHolder.Success(
        listOf(
            SharedAzanModel(
                "",
                SharedAzanType.FAJR,
                sharedApp.localizationService.strings.fajr,
                azanTimes[0],
                SharedDateModel(1, 2, 2021),
                SharedTimeModel(1, 1),
            ),
            SharedAzanModel(
                "",
                SharedAzanType.SUNRISE,
                sharedApp.localizationService.strings.sunrise,
                azanTimes[1],
                SharedDateModel(1, 2, 2021),
                SharedTimeModel(1, 1),
            ),
            SharedAzanModel(
                "",
                SharedAzanType.DHUHR,
                sharedApp.localizationService.strings.dhuhr,
                azanTimes[2],
                SharedDateModel(1, 2, 2021),
                SharedTimeModel(1, 1),
            ),
            SharedAzanModel(
                "",
                SharedAzanType.ASR,
                sharedApp.localizationService.strings.asr,
                azanTimes[3],
                SharedDateModel(1, 2, 2021),
                SharedTimeModel(1, 1),
            ),
            SharedAzanModel(
                "",
                SharedAzanType.MAGHRIB,
                sharedApp.localizationService.strings.maghrib,
                azanTimes[4],
                SharedDateModel(1, 2, 2021),
                SharedTimeModel(1, 1),
            ),
            SharedAzanModel(
                "",
                SharedAzanType.ISHA,
                sharedApp.localizationService.strings.isha,
                azanTimes[5],
                SharedDateModel(1, 2, 2021),
                SharedTimeModel(1, 1),
            ),

            ).map {
            HomeViewModel.State.Azan(
                model = it,
                isNextAzan = it.azanType == SharedAzanType.FAJR,
                isNotificationEnabled = it.azanType != SharedAzanType.SUNRISE
            )
        }
    )

    fun getState(sharedApp: SharedApp): HomeViewModel.State {
        val strings: SharedStrings = sharedApp.localizationService.strings
        val timeUntil = SharedTimeUntil(
            totalNumberOfSeconds = 1000,
            hours = 2,
            minutes = 59,
            seconds = 50,
        )
        val remainingTime: String = sharedApp.localizationService.getTimeUntilNextAzan(timeUntil)

        return HomeViewModel.State(
            nextAzan = DataPlaceHolder.Success(
                HomeViewModel.State.NextAzan(
                    id = "",
                    title = strings.nextPrayer,
                    text = "${strings.fajr}: ${azanTimes[0]}",
                    periodText = remainingTime,
                    dateModel = SharedDateModel(1, 1, 1),
                )
            ),
            azanDate = HomeViewModel.State.AzanDate(
                AppConfig.nowDate,
                sharedApp.dateTimeService.getWesternFormattedDate(dateModel = AppConfig.nowDate),
                sharedApp.dateTimeService.getIslamicFormattedDate(dateModel = AppConfig.nowDate),
            ),
            azanList = getAzanList(sharedApp = sharedApp),
        )
    }
}
