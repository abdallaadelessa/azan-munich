package com.alifwyaa.azanmunich.domain.internal.platform

import com.alifwyaa.azanmunich.domain.model.SharedPlatformInfo

/**
 * @author Created by Abdullah Essa on 01.10.21.
 */
expect class SharedTrackingService actual constructor(
    platformInfo: SharedPlatformInfo
) {
    /**
     *  Init
     */
    internal fun init()

    /**
     *  Track screen
     */
    fun trackScreen(screen: Screen)

    /**
     *  Track event
     */
    fun trackEvent(name: String, vararg params: Pair<String, String>)
}

/**
 * App Screen Names
 */
enum class Screen(val trackingName: String) {
    HOME("Home Screen"),
    SETTINGS("Settings Screen"),
}
