package com.alifwyaa.azanmunich.domain.model.settings

/**
 * @author Created by Abdullah Essa on 18.06.21.
 */
data class SharedAppLanguageSettingsModel(
    override val displayName: String,
    val appLocale: SharedAppLocale
) : SharedAppBaseSettingsModel

/**
 * Shared locale enum
 */
enum class SharedAppLocale(val code: String) {
    ENGLISH("en"), GERMAN("de")
}
