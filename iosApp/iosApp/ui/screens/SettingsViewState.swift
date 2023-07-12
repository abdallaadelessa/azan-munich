//
// Created by Abdullah Essa on 18.06.21.
// Copyright (c) 2021 orgName. All rights reserved.
//

import shared

struct SettingsViewState {
    let selectedTheme: SharedAppThemeSettingsModel
    let allThemes: [SharedAppThemeSettingsModel]
    let selectedLanguage: SharedAppLanguageSettingsModel
    let allLanguages: [SharedAppLanguageSettingsModel]
    let selectedSound: ShardAppSoundSettingsModel
    let allSounds: [ShardAppSoundSettingsModel]
    let isFajrEnabled: Bool
    let isSunriseEnabled: Bool
    let isDhuhrEnabled: Bool
    let isAsrEnabled: Bool
    let isMaghribEnabled: Bool
    let isIshaEnabled: Bool

    init(
        selectedTheme: SharedAppThemeSettingsModel = SharedAppThemeSettingsModel(displayName: "", appTheme: SharedAppTheme.default_),
        allThemes: [SharedAppThemeSettingsModel] = [],
        selectedLanguage: SharedAppLanguageSettingsModel = SharedAppLanguageSettingsModel(displayName: "", appLocale: SharedAppLocale.german),
        allLanguages: [SharedAppLanguageSettingsModel] = [],
        selectedSound: ShardAppSoundSettingsModel = ShardAppSoundSettingsModel(displayName: "", appSound: SharedAppSound.default_),
        allSounds: [ShardAppSoundSettingsModel] = [],
        isFajrEnabled: Bool = false,
        isSunriseEnabled: Bool = false,
        isDhuhrEnabled: Bool = false,
        isAsrEnabled: Bool = false,
        isMaghribEnabled: Bool = false,
        isIshaEnabled: Bool = false
    ) {
        self.selectedTheme = selectedTheme
        self.allThemes = allThemes
        self.selectedLanguage = selectedLanguage
        self.allLanguages = allLanguages
        self.selectedSound = selectedSound
        self.allSounds = allSounds
        self.isFajrEnabled = isFajrEnabled
        self.isSunriseEnabled = isSunriseEnabled
        self.isDhuhrEnabled = isDhuhrEnabled
        self.isAsrEnabled = isAsrEnabled
        self.isMaghribEnabled = isMaghribEnabled
        self.isIshaEnabled = isIshaEnabled
    }

    func copy(
        selectedTheme: SharedAppThemeSettingsModel? = nil,
        allThemes: [SharedAppThemeSettingsModel]? = nil,
        selectedLanguage: SharedAppLanguageSettingsModel? = nil,
        allLanguages: [SharedAppLanguageSettingsModel]? = nil,
        selectedSound: ShardAppSoundSettingsModel? = nil,
        allSounds: [ShardAppSoundSettingsModel]? = nil,
        isFajrEnabled: Bool? = nil,
        isSunriseEnabled: Bool? = nil,
        isDhuhrEnabled: Bool? = nil,
        isAsrEnabled: Bool? = nil,
        isMaghribEnabled: Bool? = nil,
        isIshaEnabled: Bool? = nil
    ) -> SettingsViewState {
        SettingsViewState(
                selectedTheme: selectedTheme ?? self.selectedTheme,
                allThemes: allThemes ?? self.allThemes,
                selectedLanguage: selectedLanguage ?? self.selectedLanguage,
                allLanguages: allLanguages ?? self.allLanguages,
                selectedSound: selectedSound ?? self.selectedSound,
                allSounds: allSounds ?? self.allSounds,
                isFajrEnabled: isFajrEnabled ?? self.isFajrEnabled,
                isSunriseEnabled: isSunriseEnabled ?? self.isSunriseEnabled,
                isDhuhrEnabled: isDhuhrEnabled ?? self.isDhuhrEnabled,
                isAsrEnabled: isAsrEnabled ?? self.isAsrEnabled,
                isMaghribEnabled: isMaghribEnabled ?? self.isMaghribEnabled,
                isIshaEnabled: isIshaEnabled ?? self.isIshaEnabled
        )
    }
}
