package com.alifwyaa.azanmunich.android.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.alifwyaa.azanmunich.android.extensions.appContext
import com.alifwyaa.azanmunich.android.ui.AppRoute.HOME
import com.alifwyaa.azanmunich.android.ui.AppRoute.SETTINGS
import com.alifwyaa.azanmunich.android.ui.AppRoute.SPLASH
import com.alifwyaa.azanmunich.android.ui.screens.home.HomeScreen
import com.alifwyaa.azanmunich.android.ui.screens.home.HomeViewModel
import com.alifwyaa.azanmunich.android.ui.screens.settings.SettingsScreen
import com.alifwyaa.azanmunich.android.ui.screens.settings.SettingsViewModel
import com.alifwyaa.azanmunich.android.ui.screens.splash.SplashScreen
import com.alifwyaa.azanmunich.domain.SharedApp
import com.alifwyaa.azanmunich.domain.SharedStrings


/**
 * Destinations used in the app navigation.
 */
enum class AppRoute(val route: String) {
    SPLASH("/splash"),
    HOME("/home"),
    SETTINGS("/settings"),
}

/**
 * Models the navigation actions in the app.
 */
class AppRouter(
    val startRoute: AppRoute,
    val navController: NavHostController
) {
    val upPress: () -> Unit = {
        navController.navigateUp()
    }

    val navigateToHome: () -> Unit = {
        navController.popBackStack()
        navController.navigate(HOME.route)
    }

    val navigateToSettings: () -> Unit = {
        navController.navigate(SETTINGS.route)
    }
}

/**
 * Main app graph
 */
@Composable
fun MainNavigationGraph(
    sharedApp: SharedApp,
    appRouter: AppRouter,
    sharedStrings: SharedStrings
) {
    val animationDurationInMillis = 400

    NavHost(
        navController = appRouter.navController,
        startDestination = appRouter.startRoute.route,
    ) {
        composable(
            route = SPLASH.route,
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(animationDurationInMillis)
                )
            },
        ) {
            CreateSplashScreen(appRouter)
        }
        composable(
            route = HOME.route,
            enterTransition = {
                when (initialState.destination.route) {
                    SPLASH.route -> slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = tween(animationDurationInMillis)
                    )

                    else -> slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = tween(animationDurationInMillis)
                    )
                }
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(animationDurationInMillis)
                )
            },
        ) {
            CreateHomeScreen(sharedApp, sharedStrings, appRouter)
        }
        composable(
            route = SETTINGS.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(animationDurationInMillis)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(animationDurationInMillis)
                )
            },
        ) {
            CreateSettingsScreen(sharedApp, sharedStrings, appRouter)
        }
    }
}

@Composable
private fun CreateSplashScreen(appRouter: AppRouter) {
    Scaffold { paddingValues ->
        SplashScreen(
            navigateToHome = appRouter.navigateToHome,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun CreateHomeScreen(
    sharedApp: SharedApp,
    sharedStrings: SharedStrings,
    appRouter: AppRouter
) {
    val homeViewModel: HomeViewModel = viewModel(
        factory = createViewModelFactory {
            HomeViewModel(
                azanService = sharedApp.azanService,
                dateTimeService = sharedApp.dateTimeService,
                localizationService = sharedApp.localizationService,
                settingsService = sharedApp.settingsService,
                trackingService = sharedApp.trackingService,
            )
        }
    )

    Scaffold(
        topBar = {
            HomeAppHeader(
                title = sharedStrings.appName,
                menuItemTitle = sharedStrings.settings,
                openSettings = appRouter.navigateToSettings
            )
        },
        modifier = Modifier
            .background(MaterialTheme.colors.primary)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) { paddingValues ->
        HomeScreen(
            viewModel = homeViewModel,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun CreateSettingsScreen(
    sharedApp: SharedApp,
    sharedStrings: SharedStrings,
    appRouter: AppRouter
) {
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = createViewModelFactory {
            SettingsViewModel(
                appContext = sharedApp.appContext,
                settingsService = sharedApp.settingsService,
                localizationService = sharedApp.localizationService,
                trackingService = sharedApp.trackingService,
            )
        }
    )

    Scaffold(
        topBar = {
            SettingsAppHeader(
                title = sharedStrings.settings,
                previousScreenTitle = sharedStrings.appName,
                onBack = appRouter.upPress
            )
        },
        modifier = Modifier
            .background(MaterialTheme.colors.primary)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) { paddingValues ->
        SettingsScreen(
            viewModel = settingsViewModel,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

private fun createViewModelFactory(createBlock: () -> ViewModel) =
    object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return createBlock() as T
        }
    }
