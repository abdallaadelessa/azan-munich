package com.alifwyaa.azanmunich.widget

import android.content.Context

/**
 * @author Created by Abdullah Essa on 24.10.21.
 */
internal object AndroidWidgetsService {

    /**
     * Trigger update
     */
    fun triggerUpdate(context: Context) {
        kotlin.runCatching { DayWidget.triggerUpdate(context) }
        kotlin.runCatching { NextPrayerWidget.triggerUpdate(context) }
    }
}
