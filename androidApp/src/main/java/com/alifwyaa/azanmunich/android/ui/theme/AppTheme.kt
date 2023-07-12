@file:Suppress("UndocumentedPublicFunction")

package com.alifwyaa.azanmunich.android.ui.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.alifwyaa.azanmunich.domain.model.settings.SharedAppTheme
import com.alifwyaa.azanmunich.extensions.sharedApp


private val LightThemeColors = lightColors(
    background = appColors(darkColors = false).background,

    surface = appColors(darkColors = false).surface,

    primaryVariant = appColors(darkColors = false).primaryVariant,
    primary = appColors(darkColors = false).primary,
    onPrimary = appColors(darkColors = false).onPrimary,

    secondaryVariant = appColors(darkColors = false).primaryVariant,
    secondary = appColors(darkColors = false).primary,
    onSecondary = appColors(darkColors = false).onPrimary,
)

@SuppressLint("ConflictingOnColor")
private val DarkThemeColors = darkColors(
    background = appColors(darkColors = true).background,

    surface = appColors(darkColors = true).surface,

    primaryVariant = appColors(darkColors = true).primaryVariant,
    primary = appColors(darkColors = true).primary,
    onPrimary = appColors(darkColors = true).onPrimary,

    secondaryVariant = appColors(darkColors = true).highlight,
    secondary = appColors(darkColors = true).highlight,
    onSecondary = appColors(darkColors = true).onPrimary
)

@Composable
fun isDarkTheme(): Boolean =
    when (LocalContext.current.sharedApp.settingsService.appThemeModel.appTheme) {
        SharedAppTheme.DEFAULT -> isSystemInDarkTheme()
        SharedAppTheme.LIGHT -> false
        SharedAppTheme.DARK -> true
    }

/**
 * App theme
 */
@Composable
fun AppTheme(
    isDarkTheme: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (isDarkTheme) DarkThemeColors else LightThemeColors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
