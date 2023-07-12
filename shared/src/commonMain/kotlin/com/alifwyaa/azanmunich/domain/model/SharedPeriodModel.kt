package com.alifwyaa.azanmunich.domain.model

/**
 * @author Created by Abdullah Essa on 01.06.21.
 */
data class SharedPeriodModel(
    val wholeHours: Long,
    val wholeMinutes: Long,
) {
    val hasHours: Boolean = wholeHours > 0
    val hasMinutes: Boolean = wholeMinutes > 0
    val isOneHour: Boolean = wholeHours == 1L
    val isOneMinute: Boolean = wholeMinutes == 1L
}
