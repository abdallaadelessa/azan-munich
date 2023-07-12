//
// Created by Abdullah Essa on 28.05.21.
// Copyright (c) 2021 orgName. All rights reserved.
//

import shared

struct AzanDate {
    let date: SharedDateModel
    let westernFormattedDate: String
    let islamicFormattedDate: String

    init(
            date: SharedDateModel,
            westernFormattedDate: String,
            islamicFormattedDate: String
    ) {
        self.date = date
        self.westernFormattedDate = westernFormattedDate
        self.islamicFormattedDate = islamicFormattedDate
    }
}
