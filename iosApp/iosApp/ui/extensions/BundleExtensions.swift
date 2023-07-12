//
//  BundleExtensions.swift
//  iosApp
//
//  Created by Abdullah Essa on 30.01.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation

extension Bundle {
    var releaseVersionNumber: String? {
        return infoDictionary?["CFBundleShortVersionString"] as? String
    }
    var buildVersionNumber: String? {
        return infoDictionary?["CFBundleVersion"] as? String
    }
    var appVersion: String {
        return "\(releaseVersionNumber ?? "1.0").\(buildVersionNumber ?? "0")"
    }
}
