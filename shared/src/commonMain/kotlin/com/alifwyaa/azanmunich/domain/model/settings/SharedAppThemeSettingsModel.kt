package com.alifwyaa.azanmunich.domain.model.settings

/**
 * @author Created by Abdullah Essa on 26.06.21.
 */
data class SharedAppThemeSettingsModel(
    override val displayName: String,
    val appTheme: SharedAppTheme
) : SharedAppBaseSettingsModel

/**
 * Shared app theme
 */
enum class SharedAppTheme {
    DEFAULT, LIGHT, DARK;
}

