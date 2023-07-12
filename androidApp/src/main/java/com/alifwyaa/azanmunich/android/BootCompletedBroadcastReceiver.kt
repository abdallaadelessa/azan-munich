package com.alifwyaa.azanmunich.android

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.alifwyaa.azanmunich.extensions.sharedApp

/**
 * @author Created by Abdullah Essa on 13.09.21.
 */
class BootCompletedBroadcastReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.sharedApp?.notificationSchedulerService?.startScheduleNotificationsJob()
    }
}
