package com.alifwyaa.azanmunich.domain.services

import com.alifwyaa.azanmunich.domain.internal.platform.SharedInAppReviewService

/**
 * @author Created by Abdullah Essa on 24.02.22.
 */
class SharedUserService(
    preferenceService: SharedPreferenceService,
    private val logService: SharedLogService,
    private val inAppReviewService: SharedInAppReviewService
) {

    //region Properties

    private var sessionCount: Int by preferenceService.int(PREF_KEY_SESSION_COUNT, 0)

    /**
     * Show review starting from the second session and then every 5 sessions
     */
    @Suppress("MagicNumber")
    private val shouldShowReview: Boolean
        get() = sessionCount == 2 || sessionCount % 5 == 0

    //endregion

    //region Public methods

    /**
     * Called when the first view is created in android and ios
     */
    fun onViewCreated(view: Any) {
        createNewSession()
        logService.m { "SharedUserService-> shouldShowReview: $shouldShowReview" }
        if (shouldShowReview) {
            inAppReviewService.showInAppReview(view)
        }
    }

    //endregion

    //region Private methods

    /**
     * Increment session
     */
    private fun createNewSession() {
        sessionCount += 1
        logService.m { "SharedUserService-> SessionCount: $sessionCount" }
    }

    //endregion

    companion object {
        private const val PREF_KEY_SESSION_COUNT = "PrefKeySessionCount"
    }
}
