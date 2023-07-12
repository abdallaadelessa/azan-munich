package com.alifwyaa.azanmunich.domain.services

import com.alifwyaa.azanmunich.data.internal.model.SharedAzanType
import com.alifwyaa.azanmunich.domain.internal.platform.SharedDispatchers
import com.alifwyaa.azanmunich.domain.internal.platform.SharedTrackingService
import com.alifwyaa.azanmunich.domain.model.events.SharedLanguageChangedEvent
import com.alifwyaa.azanmunich.domain.model.events.SharedNotificationsChangedEvent
import com.alifwyaa.azanmunich.domain.model.events.SharedSettingsEvent
import com.alifwyaa.azanmunich.domain.model.events.SharedThemeChangedEvent
import com.alifwyaa.azanmunich.domain.model.settings.ShardAppSoundSettingsModel
import com.alifwyaa.azanmunich.domain.model.settings.SharedAppLanguageSettingsModel
import com.alifwyaa.azanmunich.domain.model.settings.SharedAppLocale
import com.alifwyaa.azanmunich.domain.model.settings.SharedAppLocale.ENGLISH
import com.alifwyaa.azanmunich.domain.model.settings.SharedAppLocale.GERMAN
import com.alifwyaa.azanmunich.domain.model.settings.SharedAppSound
import com.alifwyaa.azanmunich.domain.model.settings.SharedAppTheme
import com.alifwyaa.azanmunich.domain.model.settings.SharedAppThemeSettingsModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

/**
 * @author Created by Abdullah Essa on 18.06.21.
 */
class SharedSettingsService(
    private val appScope: SharedAppScope,
    private val logService: () -> SharedLogService,
    private val trackingService: SharedTrackingService,
    private val preferenceService: () -> SharedPreferenceService,
    private val localizationService: () -> SharedLocalizationService,
) {

    private val prefs get() = preferenceService()

    //region App Language

    /**
     * Shared Locale setter and getter
     */
    var sharedAppLocale: SharedAppLocale
        set(value) {
            prefs.putString(PREFS_KEY_APP_LANGUAGE, value.code)
            notifyLanguageSettingsChanged()
        }
        get() = prefs.getStringOrNull(PREFS_KEY_APP_LANGUAGE)
            ?.let { code -> SharedAppLocale.values().firstOrNull { it.code == code } }
            ?: GERMAN


    /**
     * Selected language setter and getter
     */
    var appLanguageModel: SharedAppLanguageSettingsModel
        set(value) {
            sharedAppLocale = SharedAppLocale.values().first { it == value.appLocale }
        }
        get() = appLanguageList.first { it.appLocale == sharedAppLocale }

    /**
     * All supported languages
     */
    @Suppress("MemberVisibilityCanBePrivate")
    val appLanguageList: List<SharedAppLanguageSettingsModel>
        get() = SharedAppLocale.values().map {
            SharedAppLanguageSettingsModel(
                displayName = when (it) {
                    ENGLISH -> localizationService().strings.english
                    GERMAN -> localizationService().strings.german
                },
                appLocale = it
            )
        }

    //endregion

    //region App Theme

    /**
     * Selected app theme setter and getter
     */
    var appThemeModel: SharedAppThemeSettingsModel
        set(value) {
            prefs.putString(PREFS_KEY_APP_THEME, value.appTheme.toString())
            notifyThemeSettingsChanged()
        }
        get() {
            val stringValueFromPrefs: String? = prefs.getStringOrNull(PREFS_KEY_APP_THEME)
            val appTheme: SharedAppTheme = SharedAppTheme.values()
                .firstOrNull { it.toString().equals(stringValueFromPrefs, ignoreCase = true) }
                ?: SharedAppTheme.DEFAULT
            return appThemeList.first { it.appTheme == appTheme }
        }

    /**
     * All supported app themes
     */
    @Suppress("MemberVisibilityCanBePrivate")
    val appThemeList: List<SharedAppThemeSettingsModel>
        get() = SharedAppTheme.values().map {
            SharedAppThemeSettingsModel(
                displayName = when (it) {
                    SharedAppTheme.DEFAULT -> localizationService().strings.appThemeDefault
                    SharedAppTheme.LIGHT -> localizationService().strings.appThemeLight
                    SharedAppTheme.DARK -> localizationService().strings.appThemeDark
                },
                appTheme = it
            )
        }

    //endregion

    //region Notification Settings

    /**
     * Selected app sound setter and getter
     */
    var appSoundModel: ShardAppSoundSettingsModel
        set(value) {
            prefs.putString(PREFS_KEY_APP_SOUND, value.appSound.toString())
            notifyNotificationSettingsChanged()
        }
        get() {
            val stringValueFromPrefs: String? = prefs.getStringOrNull(PREFS_KEY_APP_SOUND)
            val appSound: SharedAppSound = SharedAppSound.values()
                .firstOrNull { it.toString().equals(stringValueFromPrefs, ignoreCase = true) }
                ?: SharedAppSound.SOUND1
            return appSoundList.first { it.appSound == appSound }
        }

    /**
     * All supported app sounds
     */
    @Suppress("MemberVisibilityCanBePrivate")
    val appSoundList: List<ShardAppSoundSettingsModel>
        get() = SharedAppSound.values().map {
            ShardAppSoundSettingsModel(
                displayName = when (it) {
                    SharedAppSound.DEFAULT -> localizationService().strings.appSoundDefault
                    SharedAppSound.SOUND1 -> localizationService().strings.appSound1
                },
                appSound = it
            )
        }

    /**
     *  Notification settings setter and getter
     */
    var isFajrEnabled: Boolean
        set(value) {
            prefs.putBoolean(PREFS_KEY_IS_FAJR_ENABLED, value)
            notifyNotificationSettingsChanged()
        }
        get() = prefs.getBoolean(PREFS_KEY_IS_FAJR_ENABLED, true)

    /**
     *  Notification settings setter and getter
     */
    var isSunriseEnabled: Boolean
        set(value) {
            prefs.putBoolean(PREFS_KEY_IS_SUNRISE_ENABLED, value)
            notifyNotificationSettingsChanged()
        }
        get() = prefs.getBoolean(PREFS_KEY_IS_SUNRISE_ENABLED, false)

    /**
     *  Notification settings setter and getter
     */
    var isDhuhrEnabled: Boolean
        set(value) {
            prefs.putBoolean(PREFS_KEY_IS_DHUHR_ENABLED, value)
            notifyNotificationSettingsChanged()
        }
        get() = prefs.getBoolean(PREFS_KEY_IS_DHUHR_ENABLED, true)

    /**
     *  Notification settings setter and getter
     */
    var isAsrEnabled: Boolean
        set(value) {
            prefs.putBoolean(PREFS_KEY_IS_ASR_ENABLED, value)
            notifyNotificationSettingsChanged()
        }
        get() = prefs.getBoolean(PREFS_KEY_IS_ASR_ENABLED, true)

    /**
     *  Notification settings setter and getter
     */
    var isMaghribEnabled: Boolean
        set(value) {
            prefs.putBoolean(PREFS_KEY_IS_MAGHRIB_ENABLED, value)
            notifyNotificationSettingsChanged()
        }
        get() = prefs.getBoolean(PREFS_KEY_IS_MAGHRIB_ENABLED, true)

    /**
     *  Notification settings setter and getter
     */
    var isIshaEnabled: Boolean
        set(value) {
            prefs.putBoolean(PREFS_KEY_IS_ISHA_ENABLED, value)
            notifyNotificationSettingsChanged()
        }
        get() = prefs.getBoolean(PREFS_KEY_IS_ISHA_ENABLED, true)

    /**
     *  @return true if the given [azanType] is enabled in the settings
     */
    fun isAzanEnabled(azanType: SharedAzanType): Boolean =
        when (azanType) {
            SharedAzanType.FAJR -> isFajrEnabled
            SharedAzanType.SUNRISE -> isSunriseEnabled
            SharedAzanType.DHUHR -> isDhuhrEnabled
            SharedAzanType.ASR -> isAsrEnabled
            SharedAzanType.MAGHRIB -> isMaghribEnabled
            SharedAzanType.ISHA -> isIshaEnabled
        }

    /**
     *  set enabled for the given [azanType]
     */
    fun setAzanEnabled(azanType: SharedAzanType, isEnabled: Boolean) {
        when (azanType) {
            SharedAzanType.FAJR -> isFajrEnabled = isEnabled
            SharedAzanType.SUNRISE -> isSunriseEnabled = isEnabled
            SharedAzanType.DHUHR -> isDhuhrEnabled = isEnabled
            SharedAzanType.ASR -> isAsrEnabled = isEnabled
            SharedAzanType.MAGHRIB -> isMaghribEnabled = isEnabled
            SharedAzanType.ISHA -> isIshaEnabled = isEnabled
        }
    }

    //endregion

    //region Events

    private val eventsMutableFlow = MutableSharedFlow<SharedSettingsEvent>()

    /**
     * Events flow to listen for any changes in the settings service
     */
    val eventsFlow: Flow<SharedSettingsEvent> get() = eventsMutableFlow

    private fun notifyNotificationSettingsChanged() {
        trackingService.trackEvent(
            "SettingsChanged",
            "appSound" to appSoundModel.appSound.toString(),
            "isFajrEnabled" to isFajrEnabled.toString(),
            "isSunriseEnabled" to isSunriseEnabled.toString(),
            "isDhuhrEnabled" to isDhuhrEnabled.toString(),
            "isAsrEnabled" to isAsrEnabled.toString(),
            "isMaghribEnabled" to isMaghribEnabled.toString(),
            "isIshaEnabled" to isIshaEnabled.toString(),
        )
        appScope.launch(SharedDispatchers.Main) {
            eventsMutableFlow.emit(SharedNotificationsChangedEvent)
            logService().m { "notifyNotificationSettingsChanged" }
        }
    }

    private fun notifyLanguageSettingsChanged() {
        trackingService.trackEvent(
            "SettingsChanged",
            "Language" to appLanguageModel.appLocale.toString()
        )
        appScope.launch(SharedDispatchers.Main) {
            eventsMutableFlow.emit(SharedLanguageChangedEvent)
            logService().m { "notifyLanguageSettingsChanged" }
        }
    }

    private fun notifyThemeSettingsChanged() {
        trackingService.trackEvent(
            "SettingsChanged",
            "Theme" to appThemeModel.appTheme.toString()
        )
        appScope.launch(SharedDispatchers.Main) {
            eventsMutableFlow.emit(SharedThemeChangedEvent)
            logService().m { "SharedThemeChangedEvent" }
        }
    }

    //endregion

    companion object {
        private const val PREFS_KEY_APP_LANGUAGE = "PREFS_KEY_APP_LANGUAGE"
        private const val PREFS_KEY_APP_THEME = "PREFS_KEY_APP_THEME"
        private const val PREFS_KEY_APP_SOUND = "PREFS_KEY_APP_SOUND"
        private const val PREFS_KEY_IS_FAJR_ENABLED = "PREFS_KEY_IS_FAJR_ENABLED"
        private const val PREFS_KEY_IS_SUNRISE_ENABLED = "PREFS_KEY_IS_SUNRISE_ENABLED"
        private const val PREFS_KEY_IS_DHUHR_ENABLED = "PREFS_KEY_IS_DHUHR_ENABLED"
        private const val PREFS_KEY_IS_ASR_ENABLED = "PREFS_KEY_IS_ASR_ENABLED"
        private const val PREFS_KEY_IS_MAGHRIB_ENABLED = "PREFS_KEY_IS_MAGHRIB_ENABLED"
        private const val PREFS_KEY_IS_ISHA_ENABLED = "PREFS_KEY_IS_ISHA_ENABLED"
    }
}
