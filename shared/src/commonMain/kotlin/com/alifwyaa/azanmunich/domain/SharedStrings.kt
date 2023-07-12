package com.alifwyaa.azanmunich.domain

/**
 * @author Created by Abdullah Essa on 11.06.21.
 */
@Suppress("VariableNaming")
interface SharedStrings {
    val appName: String get() = "Azan Munich"

    val yesterday: String
    val today: String
    val tomorrow: String

    val settings: String

    val generalSettings: String
    val notificationSettings: String
    val infoSettings: String

    val appVersion: String
    val about: String

    val appTheme: String get() = "App Theme"
    val appThemeDefault: String get() = "System Default"
    val appThemeLight: String
    val appThemeDark: String

    val appLanguage: String
    val english: String get() = "English"
    val german: String get() = "Deutsch"

    val appSound: String
    val appSoundDefault: String get() = "System Default"
    val appSound1: String get() = "Islam Sobhy"

    val aboutDescription: String

    val errorUnknown: String
    val errorNetworkConnection: String
    val errorNoResult: String

    val retry: String

    val ok: String
    val cancel: String

    val nextPrayerWidgetAppName: String get() = "Next Prayer Widget"
    val nextPrayerWidgetAppDesc: String get() = "show the next prayer time"

    val dayWidgetTitle: String get() = "Nächste Gebete"
    val dayWidgetAppName: String get() = "Day Widget"
    val dayWidgetAppDesc: String get() = "show prayer times of the day"

    val `in`: String
    val at: String
    val hour: String
    val hours: String
    val minute: String
    val minutes: String
    val second: String
    val seconds: String
    val fajr: String
    val sunrise: String
    val dhuhr: String
    val asr: String
    val maghrib: String
    val isha: String
    val nextPrayer: String
}

/**
 * Implementation of [SharedStrings] in english
 */
@Suppress("ObjectPropertyNaming")
internal object SharedStringsEnglish : SharedStrings {

    override val yesterday: String = "Yesterday"
    override val today: String = "Today"
    override val tomorrow: String = "Tomorrow"

    override val settings: String = "Settings"
    override val generalSettings: String = "General Settings"
    override val notificationSettings: String = "Notification Settings"
    override val infoSettings: String = "About"

    override val appThemeLight: String = "Light"
    override val appThemeDark: String = "Dark"

    override val appLanguage: String = "App Language"
    override val appSound: String = "Notification tone"

    override val appVersion: String = "App Version"

    override val about: String = "Calculation Method"
    override val aboutDescription: String =
        "Islamisches zentrum muenchen"

    override val errorUnknown: String = "Unknown Error"
    override val errorNetworkConnection: String = "No internet connection"
    override val errorNoResult: String = "No Content"

    override val retry: String = "Retry"

    override val ok: String = "OK"
    override val cancel: String = "CANCEL"

    override val `in`: String = "in"
    override val at: String = "at"
    override val hour: String = "hour"
    override val hours: String = "hours"
    override val minute: String = "min"
    override val minutes: String = "mins"
    override val second: String = "sec"
    override val seconds: String = "secs"
    override val fajr: String = "Fajr"
    override val sunrise: String = "Sunrise"
    override val dhuhr: String = "Dhuhr"
    override val asr: String = "Asr"
    override val maghrib: String = "Maghrib"
    override val isha: String = "Isha"
    override val nextPrayer: String = "Next Prayer"
}

/**
 * Implementation of [SharedStrings] in german
 */
@Suppress("ObjectPropertyNaming")
internal object SharedStringsGerman : SharedStrings {

    override val yesterday: String = "Gestern"
    override val today: String = "Heute"
    override val tomorrow: String = "Morgen"

    override val settings: String = "Einstellungen"
    override val generalSettings: String = "Allgemeine Einstellungen"
    override val notificationSettings: String = "Benachrichtigungs Einstellungen"
    override val infoSettings: String = "Über"

    override val appThemeLight: String = "Light"
    override val appThemeDark: String = "Dark"

    override val appLanguage: String = "App Sprache"
    override val appSound: String = "Benachrichtigungston"

    override val appVersion: String = "App Version"

    override val about: String = "Rechenmethode"
    override val aboutDescription: String =
        "Islamisches zentrum muenchen"

    override val errorUnknown: String = "Unbekannte Fehler"
    override val errorNetworkConnection: String = "Keine Internetverbindung"
    override val errorNoResult: String = "Kein Ergebnis"

    override val retry: String = "Wiederholen"

    override val ok: String = "ok"
    override val cancel: String = "abbrechen"

    override val `in`: String = "im"
    override val at: String = "um"
    override val hour: String = "std"
    override val hours: String = "std"
    override val minute: String = "min"
    override val minutes: String = "min"
    override val second: String = "sek"
    override val seconds: String = "sek"
    override val fajr: String = "Fajr"
    override val sunrise: String = "Sonnenaufgang"
    override val dhuhr: String = "Dhuhr"
    override val asr: String = "Asr"
    override val maghrib: String = "Maghrib"
    override val isha: String = "Isha"
    override val nextPrayer: String = "Nächstes Gebet"
}
