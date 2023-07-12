//
//  AzanMunichWidgets.swift
//  iosWidgetsExtension
//
//  Created by Abdullah Essa on 28.03.22.
//  Copyright © 2022 orgName. All rights reserved.
//
import WidgetKit
import SwiftUI

@main
struct GameWidgets: WidgetBundle {
    @WidgetBundleBuilder
    var body: some Widget {
        DayWidget()
        NextPrayerWidget()
    }
}
