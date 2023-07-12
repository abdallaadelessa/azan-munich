package com.alifwyaa.azanmunich.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @author Created by Abdullah Essa on 01.06.21.
 */
@Serializable
data class SharedTimeModel(
    @SerialName("hour")
    val hour: Int,
    @SerialName("minute")
    val minute: Int,
    @SerialName("second")
    val second: Int = 0,
) : Comparator<SharedTimeModel> {

    @Suppress("MagicNumber")
    val toSeconds: Int
        get() = (hour * 60 * 60) + (minute * 60) + (second)

    override fun compare(a: SharedTimeModel, b: SharedTimeModel): Int =
        a.toSeconds.compareTo(b.toSeconds)
}
