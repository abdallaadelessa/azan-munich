package com.alifwyaa.azanmunich.android.screenshots.factory

import androidx.compose.runtime.Composable
import com.alifwyaa.azanmunich.android.screenshots.AppConfig
import com.alifwyaa.azanmunich.android.ui.screens.settings.SettingsViewModel
import com.alifwyaa.azanmunich.domain.SharedApp

/**
 * @author Created by Abdullah Essa on 06.03.22.
 */
object SettingsStateFactory {
    @Composable
    fun getState(sharedApp: SharedApp): SettingsViewModel.State {
        val strings = sharedApp.localizationService.strings
        val settingsService = sharedApp.settingsService
        return SettingsViewModel.State(
            generalSectionTitle = strings.generalSettings,
            notificationsSectionTitle = strings.notificationSettings,
            infoSectionTitle = strings.infoSettings,
            themeSettings = SettingsViewModel.State.PickerModel(
                title = strings.appTheme,
                positiveBtn = strings.ok,
                negativeBtn = strings.cancel,
                selectedValue = settingsService.appThemeModel,
                allValues = settingsService.appThemeList,
            ),
            languageSettings = SettingsViewModel.State.PickerModel(
                title = strings.appLanguage,
                positiveBtn = strings.ok,
                negativeBtn = strings.cancel,
                selectedValue = settingsService.appLanguageModel,
                allValues = settingsService.appLanguageList,
            ),
            soundSettings = SettingsViewModel.State.PickerModel(
                title = strings.appSound,
                positiveBtn = strings.ok,
                negativeBtn = strings.cancel,
                selectedValue = settingsService.appSoundModel,
                allValues = settingsService.appSoundList,
            ),
            fajrSettings = SettingsViewModel.State.SwitchModel(
                title = strings.fajr,
                value = settingsService.isFajrEnabled
            ),
            sunriseSettings = SettingsViewModel.State.SwitchModel(
                title = strings.sunrise,
                value = settingsService.isSunriseEnabled
            ),
            dhuhrSettings = SettingsViewModel.State.SwitchModel(
                title = strings.dhuhr,
                value = settingsService.isDhuhrEnabled
            ),
            asrSettings = SettingsViewModel.State.SwitchModel(
                title = strings.asr,
                value = settingsService.isAsrEnabled
            ),
            maghribSettings = SettingsViewModel.State.SwitchModel(
                title = strings.maghrib,
                value = settingsService.isMaghribEnabled
            ),
            ishaSettings = SettingsViewModel.State.SwitchModel(
                title = strings.isha,
                value = settingsService.isIshaEnabled
            ),
            versionInfo = SettingsViewModel.State.InfoModel(
                strings.appVersion,
                AppConfig.appVersion,
            ),
            aboutInfo = SettingsViewModel.State.InfoModel(
                strings.about,
                strings.aboutDescription,
            ),
            dialog = SettingsViewModel.State.DialogPickerState.Hidden
        )
    }
}
