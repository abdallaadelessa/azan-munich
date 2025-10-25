package com.alifwyaa.azanmunich.domain.internal.notifications.local.receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.alifwyaa.azanmunich.domain.internal.notifications.local.SharedLocalNotificationsService
import com.alifwyaa.azanmunich.domain.model.SharedNotificationModel
import com.alifwyaa.azanmunich.domain.services.SharedLogService
import com.alifwyaa.azanmunich.domain.services.SharedWidgetsService
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * @author Created by Abdullah Essa on 12.09.21.
 */
class SingleNotificationBroadcastReceiver : BroadcastReceiver(), KoinComponent {

    private val logService by inject<SharedLogService>()
    private val localNotificationsService by inject<SharedLocalNotificationsService>()
    private val widgetsService by inject<SharedWidgetsService>()

    override fun onReceive(context: Context, intent: Intent) {
        try {
            val model: SharedNotificationModel =
                intent.getStringExtra(SharedLocalNotificationsService.PARAM_KEY)
                    ?.let { localNotificationsService.modelFromJson(it) }
                    ?: throw RuntimeException("SharedNotificationModel is null")

            // Update widgets
            runBlocking { widgetsService.notifyWidgets() }

            // Show notification
            runCatching {
                showNotificationIfPermitted(
                    context = context,
                    model = model
                )
            }.onFailure { error ->
                reportError(error = error)
            }

        } catch (error: Throwable) {
            reportError(error = error)
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotificationIfPermitted(
        context: Context,
        model: SharedNotificationModel,
    ) {
        val isPermissionGranted = runBlocking { localNotificationsService.isPermissionGranted() }
        if (model.isEnabled && isPermissionGranted) {
            localNotificationsService.showLocalNotification(context, model = model)
        }
    }

    private fun reportError(error: Throwable) {
        logService.e(throwable = error, report = true)
    }
}