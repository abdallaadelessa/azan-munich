package com.alifwyaa.azanmunich.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @author Created by Abdullah Essa on 24.05.21.
 *
 * Represents a day
 */
@Serializable
data class SharedDateModel(
    @SerialName("day")
    val day: Int,
    @SerialName("month")
    val month: Int,
    @SerialName("year")
    val year: Int
)
