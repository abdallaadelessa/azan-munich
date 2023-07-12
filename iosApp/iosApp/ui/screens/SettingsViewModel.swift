//
// Created by Abdullah Essa on 18.06.21.
// Copyright (c) 2021 orgName. All rights reserved.
//

import Foundation
import shared

class SettingsViewModel: ObservableObject {

    //region Properties

    private let sharedSettingsService: SharedSettingsService

    private let sharedLocalizationService: SharedLocalizationService
    
    private let sharedTrackingService: SharedTrackingService

    @Published var state: SettingsViewState = SettingsViewState()

    var strings: SharedStrings {
        sharedLocalizationService.strings
    }

    //endregion

    //region Constructor

    init(
            sharedSettingsService: SharedSettingsService,
            sharedLocalizationService: SharedLocalizationService,
            sharedTrackingService : SharedTrackingService
    ) {
        self.sharedSettingsService = sharedSettingsService
        self.sharedLocalizationService = sharedLocalizationService
        self.sharedTrackingService = sharedTrackingService
        sharedTrackingService.trackScreen(screen: Screen.settings)
        readSettings()
    }

    //endregion

    //region Updates

    func setAppLanguage(_ value: SharedAppLanguageSettingsModel) {
        sharedSettingsService.appLanguageModel = value
        updateState { state in state.copy(selectedLanguage: value) }
    }

    func setAppSound(_ value: ShardAppSoundSettingsModel) {
        sharedSettingsService.appSoundModel = value
        updateState { state in state.copy(selectedSound: value) }
    }

    func setAppTheme(_ value: SharedAppThemeSettingsModel) {
        sharedSettingsService.appThemeModel = value
        updateState { state in state.copy(selectedTheme: value) }
    }

    func setFajrEnabled(_ value: Bool) {
        sharedSettingsService.isFajrEnabled = value
        updateState { state in state.copy(isFajrEnabled: value) }
    }

    func setSunriseEnabled(_ value: Bool) {
        sharedSettingsService.isSunriseEnabled = value
        updateState { state in state.copy(isSunriseEnabled: value) }
    }

    func setDhuhrEnabled(_ value: Bool) {
        sharedSettingsService.isDhuhrEnabled = value
        updateState { state in state.copy(isDhuhrEnabled: value) }
    }

    func setAsrEnabled(_ value: Bool) {
        sharedSettingsService.isAsrEnabled = value
        updateState { state in state.copy(isAsrEnabled: value) }
    }

    func setMaghribEnabled(_ value: Bool) {
        sharedSettingsService.isMaghribEnabled = value
        updateState { state in state.copy(isMaghribEnabled: value) }
    }

    func setIshaEnabled(_ value: Bool) {
        sharedSettingsService.isIshaEnabled = value
        updateState { state in state.copy(isIshaEnabled: value) }
    }

    private func readSettings() {
        updateState { state in
            SettingsViewState(
                    selectedTheme: sharedSettingsService.appThemeModel,
                    allThemes: sharedSettingsService.appThemeList,
                    selectedLanguage: sharedSettingsService.appLanguageModel,
                    allLanguages: sharedSettingsService.appLanguageList,
                    selectedSound: sharedSettingsService.appSoundModel,
                    allSounds: sharedSettingsService.appSoundList,
                    isFajrEnabled: sharedSettingsService.isFajrEnabled,
                    isSunriseEnabled: sharedSettingsService.isSunriseEnabled,
                    isDhuhrEnabled: sharedSettingsService.isDhuhrEnabled,
                    isAsrEnabled: sharedSettingsService.isAsrEnabled,
                    isMaghribEnabled: sharedSettingsService.isMaghribEnabled,
                    isIshaEnabled: sharedSettingsService.isIshaEnabled

            )
        }
    }

    private func updateState(_ updateBlock: (SettingsViewState) -> SettingsViewState) {
        state = updateBlock(state)
    }

    //endregion
}
