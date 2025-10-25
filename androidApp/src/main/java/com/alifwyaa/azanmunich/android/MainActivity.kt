package com.alifwyaa.azanmunich.android

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.alifwyaa.azanmunich.android.extensions.sharedApp
import com.alifwyaa.azanmunich.android.ui.AppRoute
import com.alifwyaa.azanmunich.android.ui.AppRouter
import com.alifwyaa.azanmunich.android.ui.MainNavigationGraph
import com.alifwyaa.azanmunich.android.ui.components.RequestExactAlarmPermissionDialog
import com.alifwyaa.azanmunich.android.ui.components.RequestNotificationPermissionDialog
import com.alifwyaa.azanmunich.android.ui.theme.AppTheme
import com.alifwyaa.azanmunich.domain.SharedStrings
import com.alifwyaa.azanmunich.domain.model.events.SharedLanguageChangedEvent
import com.alifwyaa.azanmunich.domain.model.events.SharedThemeChangedEvent
import com.alifwyaa.azanmunich.domain.model.settings.SharedAppTheme
import com.alifwyaa.azanmunich.domain.services.SharedLocalizationService
import com.alifwyaa.azanmunich.domain.services.SharedSettingsService
import com.alifwyaa.azanmunich.workers.AzanPeriodicJobScheduler
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map

/**
 * Main Activity
 */
class MainActivity : AppCompatActivity() {

    private val sharedSettingsService: SharedSettingsService
        get() = sharedApp.settingsService

    private val sharedLocalizationService: SharedLocalizationService
        get() = sharedApp.localizationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This app draws behind the system bars, so we want to handle fitting system windows
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val isDarkTheme: Boolean = isDarkTheme()

            LaunchedEffect(isDarkTheme) {
                sharedApp.onThemeChanged(isDarkTheme = isDarkTheme)
            }

            val navController: NavHostController = rememberNavController()

            val appRouter: AppRouter = remember(navController) {
                AppRouter(startRoute = AppRoute.SPLASH, navController = navController)
            }

            val sharedStrings: SharedStrings = getSharedStrings()

            AppTheme(isDarkTheme = isDarkTheme) {

                ShowStartUpDialogs(sharedStrings = sharedStrings)

                MainNavigationGraph(
                    sharedApp = sharedApp,
                    appRouter = appRouter,
                    sharedStrings = sharedStrings
                )
            }
        }

        sharedApp.onViewCreated(view = this)
    }

    @Composable
    private fun ShowStartUpDialogs(sharedStrings: SharedStrings) {
        // Track permission states
        var notificationPermissionChecked by remember { mutableStateOf(false) }
        var exactAlarmPermissionChecked by remember { mutableStateOf(false) }

        // Step 1: Request notification permission
        RequestNotificationPermissionDialog(
            sharedStrings = sharedStrings,
            onGranted = {
                notificationPermissionChecked = true
                AzanPeriodicJobScheduler.schedule(this)
            },
            onDenied = {
                notificationPermissionChecked = true
                AzanPeriodicJobScheduler.schedule(this)
            }
        )

        // Step 2: Request exact alarm permission (after notification permission)
        if (notificationPermissionChecked && !exactAlarmPermissionChecked) {
            RequestExactAlarmPermissionDialog(
                sharedStrings = sharedStrings,
                onGranted = {
                    exactAlarmPermissionChecked = true
                },
                onInProgress = {
                    exactAlarmPermissionChecked = true
                },
                onDenied = {
                    exactAlarmPermissionChecked = true
                }
            )
        }
    }

    @SuppressLint("FlowOperatorInvokedInComposition")
    @Composable
    private fun getSharedStrings(): SharedStrings {
        val sharedStrings: State<SharedStrings> = sharedSettingsService
            .eventsFlow
            .filterIsInstance<SharedLanguageChangedEvent>()
            .map { sharedLocalizationService.strings }
            .collectAsState(initial = sharedLocalizationService.strings)

        return sharedStrings.value
    }

    @SuppressLint("FlowOperatorInvokedInComposition")
    @Composable
    private fun isDarkTheme(): Boolean {
        val appTheme: State<SharedAppTheme> = sharedSettingsService
            .eventsFlow
            .filterIsInstance<SharedThemeChangedEvent>()
            .map { sharedSettingsService.appThemeModel.appTheme }
            .collectAsState(initial = sharedSettingsService.appThemeModel.appTheme)

        val isDarkTheme = when (appTheme.value) {
            SharedAppTheme.DEFAULT -> isSystemInDarkTheme()
            SharedAppTheme.LIGHT -> false
            SharedAppTheme.DARK -> true
        }

        return isDarkTheme
    }
}
