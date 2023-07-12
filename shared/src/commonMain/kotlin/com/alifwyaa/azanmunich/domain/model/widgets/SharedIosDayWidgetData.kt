package com.alifwyaa.azanmunich.domain.model.widgets

import com.alifwyaa.azanmunich.data.internal.model.SharedAzanType
import com.alifwyaa.azanmunich.domain.model.SharedAzanModel

/**
 * Day ios widget
 */
data class SharedIosDayWidgetData(
    val title: String,
    val timeline: List<TimelineItem>,
    val nextUpdateInHours: Int,
) {
    /**
     * Timeline Item of the ios WidgetKit
     */
    data class TimelineItem(
        val startAfterNowInSeconds: Long,
        val nextAzanModel: SharedAzanModel,
        val items: List<Item>,
    ) {
        /**
         * Timeline Item Prayer Cell
         */
        data class Item(
            val displayName: String,
            val displayTime: String,
            val isNextAzan: Boolean,
            val azanType: SharedAzanType,
        ) {
            @Suppress("MagicNumber")
            val displayNameShort = displayName.take(4)
        }
    }
}
