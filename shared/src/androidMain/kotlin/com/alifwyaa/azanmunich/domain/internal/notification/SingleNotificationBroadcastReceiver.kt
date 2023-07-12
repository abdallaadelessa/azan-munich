package com.alifwyaa.azanmunich.domain.internal.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.alifwyaa.azanmunich.domain.internal.notification.NotificationUtils.PARAM_KEY
import com.alifwyaa.azanmunich.domain.internal.notification.NotificationUtils.modelFromJson
import com.alifwyaa.azanmunich.domain.internal.notification.NotificationUtils.showSingleNotification
import com.alifwyaa.azanmunich.domain.model.SharedNotificationModel
import com.alifwyaa.azanmunich.extensions.sharedApp
import com.alifwyaa.azanmunich.widget.AndroidWidgetsService

/**
 * @author Created by Abdullah Essa on 12.09.21.
 */
class SingleNotificationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        try {
            val model: SharedNotificationModel =
                intent.getStringExtra(PARAM_KEY)
                    ?.let { modelFromJson(it) }
                    ?: throw RuntimeException("SharedNotificationModel is null")

            // Update widgets
            kotlin.runCatching {
                AndroidWidgetsService.triggerUpdate(context = context)
            }.onFailure { error ->
                reportError(context = context, error = error)
            }

            // Show notification
            kotlin.runCatching {
                if (model.isEnabled) {
                    showSingleNotification(context = context, model = model)
                }
            }.onFailure { error ->
                reportError(context = context, error = error)
            }

        } catch (error: Throwable) {
            reportError(context = context, error = error)
        }
    }

    private fun reportError(context: Context, error: Throwable) {
        context.sharedApp.logService.e(throwable = error, report = true)
    }
}
