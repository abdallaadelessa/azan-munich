package com.alifwyaa.azanmunich.domain.model.widgets

import com.alifwyaa.azanmunich.domain.model.SharedAzanModel

/**
 * Next Prayer ios widget
 */
data class SharedIosNextPrayerWidgetData(
    val title: String,
    val timeline: List<TimelineItem>,
    val nextUpdateInHours: Int,
) {
    /**
     * Timeline Item of the ios WidgetKit
     */
    data class TimelineItem(
        val startAfterNowInSeconds: Long,
        val azanModel: SharedAzanModel,
        val displayName: String,
        val displayTime: String,
    )
}
