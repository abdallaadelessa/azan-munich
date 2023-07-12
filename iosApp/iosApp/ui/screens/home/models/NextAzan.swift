//
// Created by Abdullah Essa on 01.06.21.
// Copyright (c) 2021 orgName. All rights reserved.
//

import shared

struct NextAzan: Hashable {
    let azan: Azan
    let periodText: String

    init(azan: Azan, periodText: String) {
        self.azan = azan
        self.periodText = periodText
    }
}