package com.alifwyaa.azanmunich.android

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import com.alifwyaa.azanmunich.android.ui.AppRoute
import com.alifwyaa.azanmunich.android.ui.AppRouter
import com.alifwyaa.azanmunich.android.ui.MainNavigationGraph
import com.alifwyaa.azanmunich.android.ui.theme.AppTheme
import com.alifwyaa.azanmunich.domain.SharedStrings
import com.alifwyaa.azanmunich.domain.model.events.SharedLanguageChangedEvent
import com.alifwyaa.azanmunich.domain.model.events.SharedThemeChangedEvent
import com.alifwyaa.azanmunich.domain.model.settings.SharedAppTheme
import com.alifwyaa.azanmunich.domain.services.SharedLocalizationService
import com.alifwyaa.azanmunich.domain.services.SharedSettingsService
import com.alifwyaa.azanmunich.extensions.sharedApp
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
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

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AzanPeriodicJobScheduler.schedule(context = this)

        // This app draws behind the system bars, so we want to handle fitting system windows
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val isDarkTheme: Boolean = isDarkTheme()

            LaunchedEffect(isDarkTheme) {
                sharedApp.onThemeChanged(isDarkTheme = isDarkTheme)
            }

            val navController: NavHostController = rememberAnimatedNavController()

            val appRouter: AppRouter = remember(navController) {
                AppRouter(startRoute = AppRoute.SPLASH, navController = navController)
            }

            val sharedStrings: SharedStrings = getSharedStrings()

            AppTheme(isDarkTheme = isDarkTheme) {
                ProvideWindowInsets {
                    MainNavigationGraph(
                        sharedApp= sharedApp,
                        appRouter = appRouter,
                        sharedStrings = sharedStrings
                    )
                }
            }
        }

        sharedApp.onViewCreated(view = this)
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
