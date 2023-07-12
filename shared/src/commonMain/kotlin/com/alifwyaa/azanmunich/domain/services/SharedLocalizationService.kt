package com.alifwyaa.azanmunich.domain.services

import com.alifwyaa.azanmunich.data.internal.model.SharedAzanType
import com.alifwyaa.azanmunich.domain.SharedStrings
import com.alifwyaa.azanmunich.domain.SharedStringsEnglish
import com.alifwyaa.azanmunich.domain.SharedStringsGerman
import com.alifwyaa.azanmunich.domain.model.SharedAzanModel
import com.alifwyaa.azanmunich.domain.model.SharedTimeUntil
import com.alifwyaa.azanmunich.domain.model.settings.SharedAppLocale

/**
 * @author Created by Abdullah Essa on 18.06.21.
 */
class SharedLocalizationService(
    private val settingsService: () -> SharedSettingsService
) {
    //region Strings

    /**
     * Shared strings based on the [SharedSettingsService.sharedAppLocale]
     */
    val strings: SharedStrings
        get() = when (settingsService().sharedAppLocale) {
            SharedAppLocale.ENGLISH -> SharedStringsEnglish
            SharedAppLocale.GERMAN -> SharedStringsGerman
        }

    //endregion

    //region Expressions

    /**
     * @return the ui name for the given [SharedAzanType]
     */
    fun getAzanDisplayName(azanType: SharedAzanType): String = when (azanType) {
        SharedAzanType.FAJR -> strings.fajr
        SharedAzanType.SUNRISE -> strings.sunrise
        SharedAzanType.DHUHR -> strings.dhuhr
        SharedAzanType.ASR -> strings.asr
        SharedAzanType.MAGHRIB -> strings.maghrib
        SharedAzanType.ISHA -> strings.isha
    }

    /**
     * @return the ui text for the given [SharedTimeUntil]
     */
    fun getTimeUntilNextAzan(timeUntil: SharedTimeUntil): String {
        val hours: Int = timeUntil.hours
        val minutes: Int = timeUntil.minutes
        val seconds: Int = timeUntil.seconds

        var displayText = ""

        if (hours > 0) {
            displayText += "${strings.`in`} $hours " + when (hours) {
                1 -> strings.hour
                else -> strings.hours
            }
        }

        if (minutes > 0) {
            val minutesText = "$minutes " + when (minutes) {
                1 -> strings.minute
                else -> strings.minutes
            }
            displayText += if (displayText.isEmpty()) {
                "${strings.`in`} $minutesText"
            } else {
                " $minutesText"
            }
        }

        if (seconds > -1) {
            val secondsText = "$seconds " + when (seconds) {
                1 -> strings.second
                else -> strings.seconds
            }
            displayText += if (displayText.isEmpty()) {
                "${strings.`in`} $secondsText"
            } else {
                " $secondsText"
            }
        }

        return displayText
    }

    /**
     * @return the notification title for the given [SharedAzanModel]
     */
    fun getLocalNotificationTitle(model: SharedAzanModel): String =
        "${model.displayName} ${strings.at} ${model.displayTime}"

    //endregion
}
