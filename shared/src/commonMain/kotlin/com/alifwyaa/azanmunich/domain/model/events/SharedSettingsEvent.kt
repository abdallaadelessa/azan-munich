package com.alifwyaa.azanmunich.domain.model.events

/**
 * @author Created by Abdullah Essa on 19.06.21.
 */
sealed class SharedSettingsEvent

/**
 * On Notification settings changed event
 */
object SharedNotificationsChangedEvent : SharedSettingsEvent()

/**
 * On App Language settings changed event
 */
object SharedLanguageChangedEvent : SharedSettingsEvent()


/**
 * On App Theme settings changed event
 */
object SharedThemeChangedEvent : SharedSettingsEvent()
