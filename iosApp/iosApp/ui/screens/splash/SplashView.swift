//
// Created by Abdullah Essa on 27.05.21.
// Copyright (c) 2021 orgName. All rights reserved.
//

import shared
import SwiftUI

struct SplashView: View {

    var body: some View {
        let appName = AzanMunichApp.sharedApp!.localizationService.strings.appName
        VStack {
            Image(Drawables.ICON_SPLASH)
                    .resizable()
                    .scaledToFit()
                .frame(width: 200, height: (200/1.2))
             Text(appName)
                .foregroundColor(Colors.DEFAULT_ACCENT)
                    .font(Font.largeTitle)
        }
    }
}

class SplashView_Previews: PreviewProvider {
    static var previews: some View {
        SplashView()
    }

    #if DEBUG
    @objc
    class func injected() {
        UIApplication.shared.windows.first?.rootViewController =
                UIHostingController(rootView: SplashView_Previews.previews)
    }
    #endif
}
