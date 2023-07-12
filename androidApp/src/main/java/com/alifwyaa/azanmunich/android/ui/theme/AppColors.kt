package com.alifwyaa.azanmunich.android.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * App colors
 */
fun appColors(darkColors: Boolean): AppColors = if (darkColors) AppDarkColors else AppLightColors

/**
 * App colors interface
 */
interface AppColors {
    val background: Color
    val surface: Color
    val surfaceBorder: Color
    val primary: Color
    val highlight: Color
    val primaryVariant: Color
    val onPrimary: Color
}

private object AppLightColors : AppColors {
    override val background = Color(0xffFFE6D5)
    override val surface = Color(0xffFFF1E6)
    override val surfaceBorder = Color.Black
    override val primaryVariant = Color(0xffFFAB72)
    override val primary = Color(0xffFFAB72)
    override val highlight = Color(0xffFFC8A3)
    override val onPrimary: Color = Color.Black
}

private object AppDarkColors : AppColors {
    override val background = Color(0xff000000)
    override val surface = Color(0xff202020)
    override val surfaceBorder = Color.Transparent
    override val primaryVariant = Color(0xff939292)
    override val primary = Color(0xff202020)
    override val highlight = Color(0xff939292)
    override val onPrimary: Color = Color.White
}
