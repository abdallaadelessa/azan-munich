//
//  WidgetsSharedData.swift
//  iosWidgetsExtension
//
//  Created by Abdullah Essa on 18.03.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import shared

class Singleton  {
    static let sharedApp = getSharedApp()
    
    private static func getSharedApp() -> SharedApp {
        var isDebug: Bool = false
        #if DEBUG
        isDebug = true
        #endif
        let platformInfo = SharedPlatformInfo.Ios.init(isDebug:isDebug)
        let sharedApp = SharedApp(platformInfo: platformInfo)
        sharedApp.onCreate()
        return sharedApp
    }
}
