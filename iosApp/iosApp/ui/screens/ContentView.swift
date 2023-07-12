//
// Created by Abdullah Essa on 27.05.21.
// Copyright (c) 2021 orgName. All rights reserved.
//

import shared
import SwiftUI
import WidgetKit

struct ContentView: View {

    private var sharedSettingsService: SharedSettingsService {
        AzanMunichApp.sharedApp!.settingsService
    }

    private var sharedLocalizationService: SharedLocalizationService {
        AzanMunichApp.sharedApp!.localizationService
    }
    
    @Environment(\.scenePhase) var currentScenePhase
    
    @State private var previousScenePhase : ScenePhase? = nil
    
    @State private var currentScreen: NavigationScreen? = .splash
    
    private let home: HomeView = HomeView()
    
    init() {
        UITableView.appearance().backgroundColor = UIColor(Colors.DEFAULT_BG)
        UITableViewCell.appearance().selectionStyle = .none
        UITableViewCell.appearance().backgroundColor = UIColor(Colors.DEFAULT_LIST_ITEM_BG)
        UITableViewCell.appearance().tintColor = UIColor(Colors.DEFAULT_HIGHLIGHT)
        let navigationBarColor = Colors.DEFAULT_BG
        UINavigationBar.appearance().barTintColor = UIColor(navigationBarColor)
        UINavigationBar.appearance().backgroundColor = UIColor(navigationBarColor)
        // Listen to shared events
        listenToSharedEvents()
    }

    var body: some View {
        NavigationView {
            ZStack {
                Colors.DEFAULT_BG.edgesIgnoringSafeArea(.all)
                if (currentScreen == .splash) {
                    // Splash Screen
                    SplashView()
                        .navigationBarTitle("")
                        .navigationBarHidden(true)
                        .onAppear {
                        DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
                            withAnimation {
                                self.currentScreen = nil
                            }
                        }
                    }
                } else {
                    // Home Screen
                    home
                        .navigationBarHidden(false)
                        .navigationBarTitleDisplayMode(.inline)
                        .navigationBarItems(
                            trailing: Image(Drawables.ICON_SETTINGS)
                                    .renderingMode(.template)
                                    .resizable()
                                    .scaledToFit()
                                    .frame(width: 20, height: 20)
                                    .foregroundColor(Colors.DEFAULT_ACCENT)
                                    .onTapGesture {
                                        self.currentScreen = NavigationScreen.settings
                                    }
                        )
                    NavigationLink(
                        destination: SettingsView()
                            .navigationBarHidden(false)
                            .navigationBarTitleDisplayMode(.inline),
                        tag: NavigationScreen.settings, selection: $currentScreen
                    ) {
                    }
                }
            }.onAppear {
                // do it once when the view is created
                updateAppTheme()
            }
            .onChange(of: currentScenePhase) { newPhase in
                // if the new phase is "active" and the old phase was "inactive" or "background"
                if newPhase == .active && previousScenePhase != nil {
                    WidgetCenter.shared.reloadAllTimelines()
                    home.reloadAndResetNextAzan()
                }
                previousScenePhase = newPhase
            }
        }
        .accentColor(Colors.DEFAULT_ACCENT)
        .navigationViewStyle(StackNavigationViewStyle())
    }

    private func listenToSharedEvents() {
        AzanMunichApp.sharedApp!.settingsService.eventsFlow
                .collect(collector: SwiftFlowCollector<SharedSettingsEvent> { event in
            switch (event) {
            case is SharedThemeChangedEvent:
                updateAppTheme()
            default:
                ()
            }
        }
        ) { (unit, error) in
            // code which is executed if the Flow object completed
        }
    }
    
    private func updateAppTheme(){
        UIApplication.shared.windows.forEach { window in
            switch(sharedSettingsService.appThemeModel.appTheme){
                case .default_:
                    window.overrideUserInterfaceStyle = UIUserInterfaceStyle.unspecified
                case .light:
                    window.overrideUserInterfaceStyle = UIUserInterfaceStyle.light
                case .dark:
                    window.overrideUserInterfaceStyle = UIUserInterfaceStyle.dark
            default:
                ()
            }
        }
    }
    
}

enum NavigationScreen: String {
    case splash
    case settings
}

class ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }

    #if DEBUG
    @objc
    class func injected() {
        UIApplication.shared.windows.first?.rootViewController =
                UIHostingController(rootView: ContentView_Previews.previews)
    }
    #endif
}
