package com.alifwyaa.azanmunich.domain.internal.widgets

import android.content.Context
import com.alifwyaa.azanmunich.domain.model.SharedPlatformInfo
import com.alifwyaa.azanmunich.domain.services.SharedLogService
import com.alifwyaa.azanmunich.widget.DayWidget
import com.alifwyaa.azanmunich.widget.NextPrayerWidget

actual class SharedCoreWidgetsService actual constructor(
    private val platformInfo: SharedPlatformInfo,
    private val logService: SharedLogService,
) {
    private val appContext: Context
        get() = (platformInfo as SharedPlatformInfo.Android).appContext as Context

    /**
     * Triggers widget updates on the platform.
     *
     * Notifies home screen widgets to refresh their data.
     */
    actual suspend fun notifyWidgets() {
        runCatching {
            kotlin.runCatching { DayWidget.triggerUpdate(appContext) }
            kotlin.runCatching { NextPrayerWidget.triggerUpdate(appContext) }
        }.onFailure { e ->
            logService.e(throwable = e, report = true)
        }
    }
}