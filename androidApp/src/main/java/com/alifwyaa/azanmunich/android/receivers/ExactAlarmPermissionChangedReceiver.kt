package com.alifwyaa.azanmunich.android.receivers

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import com.alifwyaa.azanmunich.android.extensions.sharedApp

/**
 * BroadcastReceiver that listens for changes to the SCHEDULE_EXACT_ALARM permission.
 *
 * This receiver is triggered when the user grants or revokes the exact alarm permission
 * on Android 12+ (API 31+). When the permission is granted, it automatically schedules
 * prayer time notifications.
 *
 * Registration in AndroidManifest.xml:
 * ```xml
 * <receiver
 *     android:name=".ExactAlarmPermissionChangedReceiver"
 *     android:enabled="true"
 *     android:exported="false">
 *     <intent-filter>
 *         <action android:name="android.app.action.SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED" />
 *     </intent-filter>
 * </receiver>
 * ```
 *
 * Features:
 * - Automatically detects when exact alarm permission is granted
 * - Schedules prayer time notifications when permission becomes available
 * - Only runs on Android 12+ (API 31+) where the permission exists
 * - Logs permission state changes for debugging
 *
 * @author Created by Abdullah Essa
 */
@RequiresApi(Build.VERSION_CODES.S)
class ExactAlarmPermissionChangedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val sharedApp = context.sharedApp
        try {
            if (intent.action != AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED) {
                return
            }
            val alarmManager = context.getSystemService<AlarmManager>() ?: return
            if (alarmManager.canScheduleExactAlarms()) {
                sharedApp.notificationSchedulerService.startScheduleNotificationsJob()
            }
        } catch (e: Exception) {
            sharedApp.logService.e(throwable = e, report = true)
        }
    }
}