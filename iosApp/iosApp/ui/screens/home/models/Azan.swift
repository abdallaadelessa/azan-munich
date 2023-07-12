//
// Created by Abdullah Essa on 28.05.21.
// Copyright (c) 2021 orgName. All rights reserved.
//

import shared

// swiftlint:disable nesting
struct Azan: Hashable {
    let id: String
    let displayName: String
    let displayTime: String
    let azanType: SharedAzanType
    let date: SharedDateModel
    let time: SharedTimeModel
    let isNext: Bool
    let isEnabled: Bool

    init(
            id: String,
            displayName: String,
            displayTime: String,
            azanType: SharedAzanType,
            date: SharedDateModel,
            time: SharedTimeModel,
            isNext: Bool,
            isEnabled: Bool
    ) {
        self.id = id
        self.displayName = displayName
        self.displayTime = displayTime
        self.azanType = azanType
        self.date = date
        self.time = time
        self.isNext = isNext
        self.isEnabled = isEnabled
    }

    func copy(
            id: String? = nil,
            displayName: String? = nil,
            displayTime: String? = nil,
            azanType: SharedAzanType? = nil,
            date: SharedDateModel? = nil,
            time: SharedTimeModel? = nil,
            isNext: Bool? = nil,
            isEnabled: Bool? = nil
    ) -> Azan {
        return Azan(
                id: id ?? self.id,
                displayName: displayName ?? self.displayName,
                displayTime: displayTime ?? self.displayTime,
                azanType: azanType ?? self.azanType,
                date: date ?? self.date,
                time: time ?? self.time,
                isNext: isNext ?? self.isNext,
                isEnabled: isEnabled ?? self.isEnabled
        )
    }

}
