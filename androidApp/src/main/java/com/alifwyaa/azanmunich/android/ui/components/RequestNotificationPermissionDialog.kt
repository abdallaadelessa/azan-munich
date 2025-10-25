package com.alifwyaa.azanmunich.android.ui.components

import android.Manifest
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.app.ActivityCompat
import com.alifwyaa.azanmunich.android.extensions.dialogHorizontalMargin
import com.alifwyaa.azanmunich.domain.SharedStrings
import com.alifwyaa.azanmunich.extensions.isPostNotificationPermissionGranted

/**
 * Composable function that handles notification permission requests on Android 13+ (API 33).
 *
 * This function manages the entire permission flow including:
 * - Checking if permission is already granted
 * - Requesting permission when needed
 * - Showing rationale dialog when user previously denied
 * - Showing denial dialog when user denies permission
 * - Using localized strings from SharedStrings
 *
 * Usage:
 * ```kotlin
 * setContent {
 *     RequestNotificationPermission(
 *         onGranted = {
 *             // Permission granted - schedule notifications
 *             AzanPeriodicJobScheduler.schedule(context)
 *         },
 *         onDenied = {
 *             // Permission denied - handle accordingly
 *         }
 *     )
 *
 *     // Your other composables...
 * }
 * ```
 *
 * Features:
 * - Automatically checks if permission is already granted
 * - Shows educational dialog explaining why permission is needed
 * - Respects user's decision if permission is denied
 * - Fully localized (supports English and German)
 * - Fully declarative using Jetpack Compose
 *
 * @param onGranted Callback invoked when permission is granted (either already granted or newly granted)
 * @param onDenied Callback invoked when permission is denied by the user
 *
 * @author Created by Abdullah Essa
 */
@Composable
fun RequestNotificationPermissionDialog(
    sharedStrings: SharedStrings,
    onGranted: () -> Unit,
    onDenied: () -> Unit
) {
    val context = LocalContext.current
    val strings = sharedStrings

    // Dialog states
    var showRationaleDialog by remember { mutableStateOf(false) }
    var showDeniedDialog by remember { mutableStateOf(false) }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onGranted()
        } else {
            onDenied()
            showDeniedDialog = true
        }
    }

    // Check and request permission on first composition
    LaunchedEffect(Unit) {
        if (isPostNotificationPermissionGranted(context)) {
            // Permission already granted
            onGranted()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check if we should show rationale
            val activity = context as? ComponentActivity
            if (activity != null && ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            ) {
                // Show rationale dialog first
                showRationaleDialog = true
            } else {
                // Request permission directly
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Rationale Dialog
    if (showRationaleDialog) {
        PermissionRationaleDialog(
            sharedStrings = strings,
            onAllow = {
                showRationaleDialog = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            },
            onDismiss = {
                showRationaleDialog = false
            }
        )
    }

    // Denied Dialog
    if (showDeniedDialog) {
        PermissionDeniedDialog(
            sharedStrings = strings,
            onDismiss = {
                showDeniedDialog = false
            }
        )
    }
}

/**
 * Shows an educational dialog explaining why notification permission is needed.
 *
 * This dialog is shown when the user has previously denied the permission.
 * The dialog presents:
 * - A clear title explaining the purpose
 * - Benefits of granting the permission (prayer time reminders)
 * - Two options: "Allow" (proceeds with permission request) and "Not Now" (dismisses)
 *
 * All text is localized via [SharedStrings] supporting multiple languages.
 *
 * @param sharedStrings Localized strings for the dialog content
 * @param onAllow Callback when user taps "Allow"
 * @param onDismiss Callback when user dismisses the dialog
 */
@Composable
private fun PermissionRationaleDialog(
    sharedStrings: SharedStrings,
    onAllow: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        modifier = Modifier.padding(horizontal = LocalConfiguration.current.dialogHorizontalMargin),
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = sharedStrings.notificationPermissionTitle,
                style = MaterialTheme.typography.h6
            )
        },
        text = {
            Text(
                text = sharedStrings.notificationPermissionMessage,
                color = MaterialTheme.colors.onPrimary,
                style = MaterialTheme.typography.body1
            )
        },
        confirmButton = {
            TextButton(onClick = onAllow) {
                Text(
                    text = sharedStrings.allow,
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
 * @param sharedStrings Localized strings for the dialog content
 * @param onDismiss Callback when user dismisses the dialog
 */
@Composable
private fun PermissionDeniedDialog(
    sharedStrings: SharedStrings,
    onDismiss: () -> Unit
) {
    AlertDialog(
        modifier = Modifier.padding(horizontal = LocalConfiguration.current.dialogHorizontalMargin),
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = sharedStrings.notificationPermissionDeniedTitle,
                style = MaterialTheme.typography.h6
            )
        },
        text = {
            Text(
                text = sharedStrings.notificationPermissionDeniedMessage,
                color = MaterialTheme.colors.onPrimary,
                style = MaterialTheme.typography.body1
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = sharedStrings.ok,
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
