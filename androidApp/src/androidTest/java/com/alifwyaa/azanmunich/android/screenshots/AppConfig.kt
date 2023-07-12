package com.alifwyaa.azanmunich.android.screenshots

import com.alifwyaa.azanmunich.domain.model.SharedDateModel
import com.alifwyaa.azanmunich.domain.model.SharedTimeModel
import com.alifwyaa.azanmunich.domain.model.settings.SharedAppLocale

/**
 * @author Created by Abdullah Essa on 07.03.22.
 */
object AppConfig {
    val nowDate = SharedDateModel(day = 7, month = 3, year = 2022)
    val nowTime = SharedTimeModel(hour = 1, minute = 59, second = 59)
    val locale: SharedAppLocale = SharedAppLocale.GERMAN
    const val appVersion: String = "1.0.9"
    val azanTimes = listOf(
        "05:00",
        "06:39",
        "12:30",
        "15:31",
        "18:11",
        "19:43",
    )
}
