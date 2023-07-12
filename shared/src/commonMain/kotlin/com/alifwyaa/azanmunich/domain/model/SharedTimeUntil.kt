package com.alifwyaa.azanmunich.domain.model

/**
 * @author Created by Abdullah Essa on 15.06.21.
 */
data class SharedTimeUntil(
    val totalNumberOfSeconds: Long,
    val hours: Int,
    val minutes: Int,
    val seconds: Int,
) {
    val hasHours: Boolean = hours > 0
    val hasMinutes: Boolean = minutes > 0
    val hasSeconds: Boolean = seconds > 0
    val isInFuture: Boolean = totalNumberOfSeconds > 0
}
