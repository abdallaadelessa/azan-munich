package com.alifwyaa.azanmunich.android

import android.Manifest
import android.app.Activity
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.alifwyaa.azanmunich.domain.SharedStrings
import com.alifwyaa.azanmunich.domain.internal.notification.NotificationUtils.isPermissionGranted
import com.alifwyaa.azanmunich.extensions.sharedApp

/**
 * Handles notification permission requests on Android 13+ (API 33) using the modern
 * ActivityResultLauncher API.
 *
 * This class manages the entire permission flow including:
 * - Checking if permission is already granted
 * - Requesting permission when needed
 * - Showing rationale dialog when user previously denied
 * - Showing denial dialog when user denies permission
 * - Using localized strings from SharedStrings
 *
 * Usage:
 * ```kotlin
 * override fun onCreate(savedInstanceState: Bundle?) {
 *     super.onCreate(savedInstanceState)
 *
 *     NotificationPermissionRequester().startIfNecessary(this) {
 *         // Permission granted - schedule notifications
 *         AzanPeriodicJobScheduler.schedule(context = this)
 *     }
 * }
 * ```
 *
 * Features:
 * - Automatically checks if permission is already granted
 * - Shows educational dialog explaining why permission is needed
 * - Respects user's decision if permission is denied
 * - Fully localized (supports English and German)
 *
 * @author Created by Abdullah Essa
 */
class NotificationPermissionRequester {
    private lateinit var activityResultLauncher: ActivityResultLauncher<String>

    /**
     * Starts the permission request flow if notification permission is not already granted.
     *
     * This method:
     * 1. Registers an ActivityResultLauncher for handling permission results
     * 2. Checks if permission is already granted
     * 3. If granted, immediately calls [onGranted]
     * 4. If not granted, requests permission (may show rationale dialog first)
     * 5. Shows denial dialog if user denies permission
     *
     * IMPORTANT: This must be called in onCreate() before the activity is fully created,
     * as it registers an ActivityResultLauncher.
     *
     * @param activity The AppCompatActivity requesting permission
     * @param onGranted Callback invoked when permission is granted (either already granted or newly granted)
     */
    fun startIfNecessary(activity: AppCompatActivity, onGranted: () -> Unit, onDenied: () -> Unit) {
        activityResultLauncher = registerPermissionLauncher(
            activity = activity,
            onGranted = {
                onGranted()
            },
            onDenied = {
                onDenied()
                showPermissionDeclinedDialog(activity)
            }
        )

        if (isPermissionGranted(activity)) {
            onGranted()
        } else {
            requestPermission(
                activity = activity,
                launcher = activityResultLauncher
            )
        }

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun getNotificationPermission(): String = Manifest.permission.POST_NOTIFICATIONS

    /**
     * Register a permission launcher in your Activity
     * IMPORTANT: This must be called before onCreate() returns (usually in onCreate)
     *
     * @param activity The AppCompatActivity
     * @param onGranted Callback when permission is granted
     * @param onDenied Callback when permission is denied
     * @return ActivityResultLauncher that can be used to request permission
     */
    private fun registerPermissionLauncher(
        activity: AppCompatActivity,
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ): ActivityResultLauncher<String> {
        return activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                onGranted()
            } else {
                onDenied()
            }
        }
    }

    /**
     * Requests notification permission from the user.
     *
     * If the user has previously denied the permission, shows a rationale dialog explaining
     * why the permission is needed before requesting it again. Otherwise, directly requests
     * the permission via the system dialog.
     *
     * Only requests permission on Android 13+ (API 33), as notification permission is
     * automatically granted on older versions.
     *
     * @param activity The Activity context
     * @param launcher The ActivityResultLauncher for handling the permission request result
     */
    private fun requestPermission(
        activity: Activity,
        launcher: ActivityResultLauncher<String>,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ActivityCompat.shouldShowRequestPermissionRationale(
                    activity, getNotificationPermission()
                ) -> {
                    // Show rationale dialog explaining why we need the permission
                    showPermissionRationaleDialog(activity, launcher)
                }

                else -> {
                    // Directly request the permission
                    launcher.launch(getNotificationPermission())
                }
            }
        }
    }

    /**
     * Shows an educational dialog explaining why notification permission is needed.
     *
     * This dialog is shown when the user has previously denied the permission
     * (detected via [ActivityCompat.shouldShowRequestPermissionRationale]).
     *
     * The dialog presents:
     * - A clear title explaining the purpose
     * - Benefits of granting the permission (prayer time reminders)
     * - Two options: "Allow" (proceeds with permission request) and "Not Now" (dismisses)
     *
     * All text is localized via [SharedStrings] supporting multiple languages.
     *
     * @param activity The Activity context for showing the dialog
     * @param launcher The ActivityResultLauncher to invoke if user taps "Allow"
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun showPermissionRationaleDialog(
        activity: Activity,
        launcher: ActivityResultLauncher<String>
    ) {
        val strings: SharedStrings = activity.sharedApp.localizationService.strings

        AlertDialog.Builder(activity)
            .setTitle(strings.notificationPermissionTitle)
            .setMessage(strings.notificationPermissionMessage)
            .setPositiveButton(strings.allow) { dialog, _ ->
                dialog.dismiss()
                launcher.launch(getNotificationPermission())
            }
            .setNegativeButton(strings.notNow) { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }

    /**
     * Shows a dialog informing the user that prayer time notifications are disabled.
     *
     * This dialog is displayed when the user denies the notification permission.
     * It follows Android best practices by:
     * - Acknowledging the user's decision without pressure
     * - Explaining what features are affected (no automatic reminders)
     * - Informing that the app is still usable for viewing prayer times
     * - Mentioning permission can be enabled later in app settings
     * - NOT linking to system settings or trying to convince the user
     *
     * All text is localized via [SharedStrings] supporting multiple languages.
     *
     * @param activity The Activity context for showing the dialog
     */
    private fun showPermissionDeclinedDialog(activity: Activity) {
        val strings: SharedStrings = activity.sharedApp.localizationService.strings

        AlertDialog.Builder(activity)
            .setTitle(strings.notificationPermissionDeniedTitle)
            .setMessage(strings.notificationPermissionDeniedMessage)
            .setPositiveButton(strings.ok) { dialog, _ -> dialog.dismiss() }
            .setCancelable(true)
            .show()
    }
}