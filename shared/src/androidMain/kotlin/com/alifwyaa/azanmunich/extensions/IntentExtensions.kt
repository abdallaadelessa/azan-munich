package com.alifwyaa.azanmunich.extensions

import android.app.PendingIntent
import android.content.Context
import android.content.Intent

/**
 * Creates an intent to launch the main activity.
 *
 * @return Intent to launch the app, or null if package not found
 */
internal fun getLaunchIntent(context: Context): Intent {
    return requireNotNull(
        context.packageManager.getLaunchIntentForPackage(context.packageName)
    ).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
}

/**
 * Creates a pending intent for launching the main activity.
 *
 * @return PendingIntent for the main activity
 */
internal fun getLaunchPendingIntent(
    context: Context,
    intent: Intent = getLaunchIntent(context)
): PendingIntent =
    PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )