package com.alifwyaa.azanmunich.android.ui


import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
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
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable


/**
 * Destinations used in the ([MainActivityContent]).
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
@ExperimentalAnimationApi
@Composable
fun MainNavigationGraph(
    sharedApp: SharedApp,
    appRouter: AppRouter,
    sharedStrings: SharedStrings
) {
    val animationDurationInMillis = 400

    AnimatedNavHost(
        navController = appRouter.navController,
        startDestination = appRouter.startRoute.route,
        modifier = Modifier
            .navigationBarsPadding()
            .statusBarsPadding()
    ) {
        composable(
            route = SPLASH.route,
            exitTransition = {
                slideOutHorizontally(animationSpec = tween(animationDurationInMillis))
            },
        ) {
            CreateSplashScreen(appRouter)
        }
        composable(
            route = HOME.route,
            enterTransition = {
                when (initialState.destination.route) {
                    SPLASH.route -> slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(animationDurationInMillis)
                    )
                    else -> slideInHorizontally(animationSpec = tween(animationDurationInMillis))
                }
            },
            exitTransition = {
                slideOutHorizontally(
                    animationSpec = tween(
                        animationDurationInMillis
                    )
                )
            },
        ) {
            CreateHomeScreen(sharedApp, sharedStrings, appRouter)
        }
        composable(
            route = SETTINGS.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(animationDurationInMillis)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it / 2 },
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
    Scaffold {
        SplashScreen(navigateToHome = appRouter.navigateToHome)
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

    Scaffold(topBar = {
        HomeAppHeader(
            title = sharedStrings.appName,
            menuItemTitle = sharedStrings.settings,
            openSettings = appRouter.navigateToSettings
        )
    }) {
        HomeScreen(viewModel = homeViewModel)
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

    Scaffold(topBar = {
        SettingsAppHeader(
            title = sharedStrings.settings,
            previousScreenTitle = sharedStrings.appName,
            onBack = appRouter.upPress
        )
    }) {
        SettingsScreen(settingsViewModel)
    }
}

private fun createViewModelFactory(createBlock: () -> ViewModel) =
    object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return createBlock() as T
        }
    }
