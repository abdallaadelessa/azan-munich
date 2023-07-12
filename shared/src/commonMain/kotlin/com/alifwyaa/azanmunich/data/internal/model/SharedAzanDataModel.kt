package com.alifwyaa.azanmunich.data.internal.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @author Created by Abdullah Essa on 18.05.21.
 */
@Serializable
data class SharedAzanDataModel(
    @SerialName("id")
    val id: String,
    @SerialName("day")
    val day: Int,
    @SerialName("month")
    val month: Int,
    @SerialName("year")
    val year: Int,
    @SerialName("time")
    val time: String,
    @SerialName("azan")
    val azan: Int,
) {
    /**
     * Firebase constants
     */
    object Firebase {
        const val COLLECTION_NAME = "azan-times"
        const val ID = "id"
        const val DAY_OF_MONTH = "dayOfMonth"
        const val MONTH_OF_YEAR = "monthOfYear"
        const val YEAR = "year"
        const val TIME = "time"
        const val AZAN = "azan"
    }
}
