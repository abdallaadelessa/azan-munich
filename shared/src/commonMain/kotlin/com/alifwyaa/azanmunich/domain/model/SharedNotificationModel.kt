package com.alifwyaa.azanmunich.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @author Created by Abdullah Essa on 04.06.21.
 */
@Serializable
data class SharedNotificationModel(
    @SerialName("id")
    val id: String,
    @SerialName("categoryId")
    val categoryId: String,
    @SerialName("title")
    val title: String,
    @SerialName("sound")
    val sound: Sound,
    @SerialName("payload")
    val payload: Map<String, String>,
    @SerialName("dateModel")
    val dateModel: SharedDateModel,
    @SerialName("timeModel")
    val timeModel: SharedTimeModel,
    @SerialName("isEnabled")
    val isEnabled: Boolean = true,
) {
    /**
     * Sound types
     */
    @Serializable
    enum class Sound {
        @SerialName("DEFAULT")
        DEFAULT,

        @SerialName("SOUND1_FAJR")
        SOUND1_FAJR,

        @SerialName("SOUND1_OTHERS")
        SOUND1_OTHERS,
    }
}
