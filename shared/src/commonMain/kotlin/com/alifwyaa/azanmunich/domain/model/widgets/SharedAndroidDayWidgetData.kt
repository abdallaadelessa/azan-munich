package com.alifwyaa.azanmunich.domain.model.widgets

import com.alifwyaa.azanmunich.data.internal.model.SharedAzanType

/**
 * @author Created by Abdullah Essa on 25.02.22.
 */
data class SharedAndroidDayWidgetData(
    val items: List<Item>
) {
    /**
     * Item
     */
    data class Item(
        val azanType: SharedAzanType,
        val displayName: String,
        val displayTime: String,
        val isNextAzan: Boolean,
        val isAzanEnabled: Boolean
    ) {
        @Suppress("MagicNumber")
        val displayNameShort = displayName.take(4)
    }
}
