//
// Created by Abdullah Essa on 18.06.21.
// Copyright (c) 2021 orgName. All rights reserved.
//

import shared
import SwiftUI

struct SettingsView: View {

    @ObservedObject
    private var viewModel: SettingsViewModel = SettingsViewModel(
            sharedSettingsService: AzanMunichApp.sharedApp!.settingsService,
            sharedLocalizationService: AzanMunichApp.sharedApp!.localizationService,
            sharedTrackingService: AzanMunichApp.sharedApp!.trackingService
    )

    var body: some View {
        
        let listItemBgColor =  Colors.DEFAULT_LIST_ITEM_BG
        
        Form {
            Section(header: Text(viewModel.strings.generalSettings)) {
                Picker(
                        selection: Binding<SharedAppThemeSettingsModel>(
                                get: { () -> SharedAppThemeSettingsModel in viewModel.state.selectedTheme },
                                set: { value in viewModel.setAppTheme(value) }
                        ),
                        label: Text(viewModel.strings.appTheme)
                ) {
                    ForEach(viewModel.state.allThemes, id: \.self) {
                        Text($0.displayName).tag($0.appTheme)
                    }
                }
                Picker(
                        selection: Binding<SharedAppLanguageSettingsModel>(
                                get: { () -> SharedAppLanguageSettingsModel in viewModel.state.selectedLanguage },
                                set: { value in viewModel.setAppLanguage(value) }
                        ),
                        label: Text(viewModel.strings.appLanguage)
                ) {
                    ForEach(viewModel.state.allLanguages, id: \.self) {
                        Text($0.displayName).tag($0.appLocale)
                    }
                }

            }.listRowBackground(listItemBgColor)
            
            
            Section(header: Text(viewModel.strings.notificationSettings)) {
                Picker(
                        selection: Binding<ShardAppSoundSettingsModel>(
                                get: { () -> ShardAppSoundSettingsModel in viewModel.state.selectedSound },
                                set: { value in viewModel.setAppSound(value) }
                        ),
                        label: Text(viewModel.strings.appSound)
                ) {
                    ForEach(viewModel.state.allSounds, id: \.self) {
                        Text($0.displayName).tag($0.appSound)
                    }
                }
                Toggle(
                        isOn: Binding<Bool>(
                                get: { () -> Bool in viewModel.state.isFajrEnabled },
                                set: { boolValue in viewModel.setFajrEnabled(boolValue) }
                        )
                ) {
                    Text(viewModel.strings.fajr)
                }
                Toggle(
                        isOn: Binding<Bool>(
                                get: { () -> Bool in viewModel.state.isSunriseEnabled },
                                set: { boolValue in viewModel.setSunriseEnabled(boolValue) }
                        )
                ) {
                    Text(viewModel.strings.sunrise)
                }
                Toggle(
                        isOn: Binding<Bool>(
                                get: { () -> Bool in viewModel.state.isDhuhrEnabled },
                                set: { boolValue in viewModel.setDhuhrEnabled(boolValue) }
                        )
                ) {
                    Text(viewModel.strings.dhuhr)
                }
                Toggle(
                        isOn: Binding<Bool>(
                                get: { () -> Bool in viewModel.state.isAsrEnabled },
                                set: { boolValue in viewModel.setAsrEnabled(boolValue) }
                        )
                ) {
                    Text(viewModel.strings.asr)
                }
                Toggle(
                        isOn: Binding<Bool>(
                                get: { () -> Bool in viewModel.state.isMaghribEnabled },
                                set: { boolValue in viewModel.setMaghribEnabled(boolValue) }
                        )
                ) {
                    Text(viewModel.strings.maghrib)
                }
                Toggle(
                        isOn: Binding<Bool>(
                                get: { () -> Bool in viewModel.state.isIshaEnabled },
                                set: { boolValue in viewModel.setIshaEnabled(boolValue) }
                        )
                ) {
                    Text(viewModel.strings.isha)
                }
            }.listRowBackground(listItemBgColor)
            
            Section(header: Text(viewModel.strings.infoSettings)) {
                HStack(alignment: .center){
                    Text(viewModel.strings.about)
                    Spacer()
                    Text(viewModel.strings.aboutDescription)
                }
                HStack(alignment: .center){
                    Text(viewModel.strings.appVersion)
                    Spacer()
                    Text(Bundle.main.appVersion)
                }
            }.listRowBackground(listItemBgColor)
            
        }
        .background(Colors.DEFAULT_BG)
        .foregroundColor(Colors.DEFAULT_ACCENT)      
        .navigationBarTitle(viewModel.strings.settings)
    }
}

class SettingsView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            NavigationLink(destination:SettingsView().navigationBarTitleDisplayMode(.inline).navigationBarTitle("Settings")) {
                Text("Hello, World!")
            }
        }
    }

    #if DEBUG
    @objc
    class func injected() {
        UIApplication.shared.windows.first?.rootViewController =
                UIHostingController(rootView: SettingsView_Previews.previews)
    }
    #endif
}
