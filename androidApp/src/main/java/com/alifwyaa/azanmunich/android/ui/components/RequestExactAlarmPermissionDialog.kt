package com.alifwyaa.azanmunich.android.ui.components

import android.app.AlarmManager
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.getSystemService
import com.alifwyaa.azanmunich.android.extensions.dialogHorizontalMargin
import com.alifwyaa.azanmunich.domain.SharedStrings

/**
 * Composable function that handles exact alarm permission requests on Android 12+ (API 31+).
 *
 * This function manages the entire permission flow including:
 * - Checking if exact alarm permission is already granted using canScheduleExactAlarms()
 * - Showing educational dialog explaining why exact alarms are needed
 * - Launching system settings to grant permission
 * - Handling user's decision
 *
 * Usage:
 * ```kotlin
 * setContent {
 *     RequestExactAlarmPermissionDialog(
 *         sharedStrings = strings,
 *         onGranted = {
 *             // Permission granted - proceed with scheduling
 *         },
 *         onDenied = {
 *             // Permission denied - handle accordingly
 *         }
 *     )
 * }
 * ```
 *
 * Features:
 * - Automatically checks if permission is already granted
 * - Shows educational dialog explaining the importance of exact alarms
 * - Directs users to system settings to grant permission
 * - Fully localized (supports English and German)
 * - Fully declarative using Jetpack Compose
 *
 * @param sharedStrings Localized strings for dialog content
 * @param onGranted Callback invoked when permission is granted
 * @param onDenied Callback invoked when permission is denied
 *
 * @author Created by Abdullah Essa
 */
@Composable
fun RequestExactAlarmPermissionDialog(
    sharedStrings: SharedStrings,
    onGranted: () -> Unit,
    onInProgress: () -> Unit,
    onDenied: () -> Unit
) {
    val context = LocalContext.current
    val alarmManager = remember { context.getSystemService<AlarmManager>() }

    // Dialog state
    var showPermissionDialog by remember { mutableStateOf(false) }

    // Check permission on first composition
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager?.canScheduleExactAlarms() == true) {
                // Permission already granted
                onGranted()
            } else {
                // Show dialog to explain and direct user to settings
                showPermissionDialog = true
            }
        } else {
            // Android 11 and below don't need this permission
            onGranted()
        }
    }

    // Permission Dialog
    if (showPermissionDialog) {
        ExactAlarmPermissionDialog(
            sharedStrings = sharedStrings,
            onOpenSettings = {
                showPermissionDialog = false
                onInProgress()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    context.startActivity(intent)
                }
            },
            onDismiss = {
                showPermissionDialog = false
                onDenied()
            }
        )
    }
}

/**
 * Shows a dialog explaining why exact alarm permission is needed.
 *
 * This dialog directs the user to system settings where they can grant the permission.
 * The dialog presents:
 * - A clear title explaining the purpose
 * - Benefits of granting the permission (precise prayer time notifications)
 * - Two options: "Open Settings" (launches system settings) and "Not Now" (dismisses)
 *
 * All text is localized via [SharedStrings] supporting multiple languages.
 *
 * @param sharedStrings Localized strings for the dialog content
 * @param onOpenSettings Callback when user taps "Open Settings"
 * @param onDismiss Callback when user dismisses the dialog
 */
@Composable
private fun ExactAlarmPermissionDialog(
    sharedStrings: SharedStrings,
    onOpenSettings: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        modifier = Modifier.padding(horizontal = LocalConfiguration.current.dialogHorizontalMargin),
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = sharedStrings.exactAlarmPermissionTitle,
                style = MaterialTheme.typography.h6
            )
        },
        text = {
            Text(
                text = sharedStrings.exactAlarmPermissionMessage,
                color = MaterialTheme.colors.onPrimary,
                style = MaterialTheme.typography.body1
            )
        },
        confirmButton = {
            TextButton(onClick = onOpenSettings) {
                Text(
                    text = sharedStrings.openSettings,
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onPrimary,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = sharedStrings.notNow,
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onPrimary,
                )
            }
        },
        properties = DialogProperties(
            dismissOnClickOutside = true
        )
    )
}
