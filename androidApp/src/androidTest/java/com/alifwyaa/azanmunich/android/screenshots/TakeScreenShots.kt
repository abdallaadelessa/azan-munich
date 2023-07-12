package com.alifwyaa.azanmunich.android.screenshots

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alifwyaa.azanmunich.android.screenshots.factory.HomeStateFactory
import com.alifwyaa.azanmunich.android.screenshots.factory.SettingsStateFactory
import com.alifwyaa.azanmunich.android.ui.HomeAppHeader
import com.alifwyaa.azanmunich.android.ui.SettingsAppHeader
import com.alifwyaa.azanmunich.android.ui.screens.home.HomeContent
import com.alifwyaa.azanmunich.android.ui.screens.settings.SettingsContent
import com.alifwyaa.azanmunich.android.ui.screens.splash.SplashContent
import com.alifwyaa.azanmunich.android.ui.screens.splash.SplashScreen
import com.alifwyaa.azanmunich.android.ui.theme.AppTheme
import com.alifwyaa.azanmunich.domain.model.settings.SharedAppTheme
import com.alifwyaa.azanmunich.extensions.sharedApp
import com.google.accompanist.insets.ProvideWindowInsets
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import tools.fastlane.screengrab.Screengrab
import tools.fastlane.screengrab.locale.LocaleTestRule


/**
 * @author Created by Abdullah Essa on 04.11.21.
 */
@RunWith(AndroidJUnit4::class)
class TakeScreenShots {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Rule
    @JvmField
    val localeTestRule = LocaleTestRule()


    @Composable
    private fun getSharedApp() = LocalContext.current.sharedApp

    @Composable
    private fun getStrings() = getSharedApp().localizationService.strings

    //region Tests

    @Test
    fun takeSplashScreenShotLight() {
        composeTestRule.setContent {
            AppTest(isDarkTheme = false) {
                Scaffold {
                    SplashContent(getStrings().appName)
                }
            }
        }
        Screengrab.screenshot("splash_screenshot_light")
    }

    @Test
    fun takeSplashScreenShotDark() {
        composeTestRule.setContent {
            AppTest(isDarkTheme = true) {
                Scaffold {
                    SplashContent(getStrings().appName)
                }
            }
        }
        Screengrab.screenshot("splash_screenshot_dark")
    }

    @Test
    fun takeHomeScreenShotLight() {
        composeTestRule.setContent {
            AppTest(isDarkTheme = false) {
                Scaffold(topBar = {
                    HomeAppHeader(
                        title = getStrings().appName,
                        menuItemTitle = "settings",
                        openSettings = {}
                    )
                }) {
                    HomeContent(HomeStateFactory.getState(getSharedApp()))
                }
            }
        }
        Screengrab.screenshot("home_screenshot_light")
    }

    @Test
    fun takeHomeScreenShotDark() {
        composeTestRule.setContent {
            AppTest(isDarkTheme = true) {
                Scaffold(topBar = {
                    HomeAppHeader(
                        title = getStrings().appName,
                        menuItemTitle = "settings",
                        openSettings = {}
                    )
                }) {
                    HomeContent(HomeStateFactory.getState(getSharedApp()))
                }
            }
        }
        Screengrab.screenshot("home_screenshot_dark")
    }

    @Test
    fun takeSettingsScreenShotLight() {
        composeTestRule.setContent {
            AppTest(isDarkTheme = false) {
                Scaffold(topBar = {
                    SettingsAppHeader(
                        title = getStrings().settings,
                        previousScreenTitle = "",
                        onBack = {}
                    )
                }) {
                    SettingsContent(SettingsStateFactory.getState(sharedApp = getSharedApp()))
                }
            }
        }
        Screengrab.screenshot("settings_screenshot_light")
    }

    @Test
    fun takeSettingsScreenShotDark() {
        composeTestRule.setContent {
            AppTest(isDarkTheme = true) {
                Scaffold(topBar = {
                    SettingsAppHeader(
                        title = getStrings().settings,
                        previousScreenTitle = "",
                        onBack = {}
                    )
                }) {
                    SettingsContent(SettingsStateFactory.getState(sharedApp = getSharedApp()))
                }
            }
        }
        Screengrab.screenshot("settings_screenshot_dark")
    }

    //endregion

    //region Helpers

    @Composable
    private fun AppTest(isDarkTheme: Boolean, block: @Composable () -> Unit) {

        val targetAppTheme: SharedAppTheme = if (isDarkTheme) SharedAppTheme.DARK else SharedAppTheme.LIGHT

        getSharedApp().settingsService.appThemeModel =
            getSharedApp().settingsService.appThemeList.first { it.appTheme == targetAppTheme }

        getSharedApp().settingsService.appLanguageModel =
            getSharedApp().settingsService.appLanguageList.first { it.appLocale == AppConfig.locale }

        AppTheme(isDarkTheme = isDarkTheme) {
            ProvideWindowInsets {
                block()
            }
        }
    }

    //endregion

}
