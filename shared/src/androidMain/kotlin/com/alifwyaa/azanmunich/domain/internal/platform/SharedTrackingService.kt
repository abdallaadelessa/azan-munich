package com.alifwyaa.azanmunich.domain.internal.platform

import android.os.Bundle
import com.alifwyaa.azanmunich.domain.model.SharedPlatformInfo
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.FirebaseAnalytics.Event.SCREEN_VIEW
import com.google.firebase.analytics.FirebaseAnalytics.Param.SCREEN_NAME
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

/**
 * @author Created by Abdullah Essa on 01.10.21.
 */
actual class SharedTrackingService actual constructor(
    private val platformInfo: SharedPlatformInfo,
) {

    private val isRunningUnitTests: Boolean =
        (platformInfo as SharedPlatformInfo.Android).isRunningUnitTests

    private var firebaseAnalytics: FirebaseAnalytics? = null

    /**
     *  Init
     */
    internal actual fun init() {
        if (isRunningUnitTests) return

        firebaseAnalytics = Firebase.analytics.apply {
            setAnalyticsCollectionEnabled(platformInfo.isRelease)
        }
    }

    /**
     *  Track screen
     */
    actual fun trackScreen(screen: Screen) {
        if (isRunningUnitTests) return

        trackEvent(
            name = SCREEN_VIEW,
            SCREEN_NAME to screen.trackingName
        )
    }

    /**
     *  Track Event
     */
    actual fun trackEvent(name: String, vararg params: Pair<String, String>) {
        if (isRunningUnitTests) return

        val bundle = Bundle().apply { params.forEach { putString(it.first, it.second) } }
        firebaseAnalytics?.logEvent(name, bundle)
    }
}
