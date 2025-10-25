package com.alifwyaa.azanmunich.domain.internal.platform

import com.alifwyaa.azanmunich.domain.model.SharedPlatformInfo

/**
 * Platform-specific analytics and event tracking.
 *
 * Integrates with platform-specific analytics services (e.g., Firebase Analytics).
 * Tracks user navigation and custom events for analytics purposes.
 *
 * @author Created by Abdullah Essa on 01.10.21.
 */
expect class SharedTrackingService(
    platformInfo: SharedPlatformInfo
) {
    /**
     * Initializes the analytics tracking service.
     *
     * Called during app startup to configure analytics.
     */
    internal fun init()

    /**
     * Tracks a screen view event.
     *
     * @param screen The screen being viewed
     */
    fun trackScreen(screen: Screen)

    /**
     * Tracks a custom analytics event.
     *
     * @param name The event name
     * @param params Optional key-value pairs for event parameters
     */
    fun trackEvent(name: String, vararg params: Pair<String, String>)
}

/**
 * App screen identifiers for analytics tracking.
 */
enum class Screen(val trackingName: String) {
    HOME("Home Screen"),
    SETTINGS("Settings Screen"),
}
