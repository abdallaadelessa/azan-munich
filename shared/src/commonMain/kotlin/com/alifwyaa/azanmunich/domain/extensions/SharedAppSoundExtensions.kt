package com.alifwyaa.azanmunich.domain.extensions

import com.alifwyaa.azanmunich.data.internal.model.SharedAzanType
import com.alifwyaa.azanmunich.domain.model.SharedNotificationModel
import com.alifwyaa.azanmunich.domain.model.settings.SharedAppSound

/**
 * @author Created by Abdullah Essa on 05.10.21.
 */

/**
 * Convert app sound to notification sound
 */
fun SharedAppSound.toNotificationSound(type: SharedAzanType): SharedNotificationModel.Sound =
    when (this) {
        SharedAppSound.DEFAULT -> SharedNotificationModel.Sound.DEFAULT
        SharedAppSound.SOUND1 -> when (type) {
            SharedAzanType.FAJR -> SharedNotificationModel.Sound.SOUND1_FAJR
            else -> SharedNotificationModel.Sound.SOUND1_OTHERS
        }
    }
