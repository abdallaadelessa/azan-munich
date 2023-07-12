package com.alifwyaa.azanmunich.domain.internal.platform

import cocoapods.FirebaseAnalytics.FIRAnalytics
import com.alifwyaa.azanmunich.domain.model.SharedPlatformInfo

/**
 * @author Created by Abdullah Essa on 01.10.21.
 */
actual class SharedTrackingService actual constructor(
    private val platformInfo: SharedPlatformInfo
) {

    private val analytics: FIRAnalytics.Companion
        get() = FIRAnalytics.Companion

    /**
     *  Init
     */
    internal actual fun init() {
        analytics.setAnalyticsCollectionEnabled(platformInfo.isRelease)
    }

    /**
     *  Track screen
     */
    actual fun trackScreen(screen: Screen) {
        trackEvent(
            name = "screen_view",
            "screen_name" to screen.trackingName,
            "screen_class" to screen.toString(),
        )
    }

    /**
     *  Track event
     */
    actual fun trackEvent(
        name: String,
        vararg params: Pair<String, String>
    ) {
        analytics.logEventWithName(name, params.toMap())
    }

}
