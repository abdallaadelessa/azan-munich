package com.alifwyaa.azanmunich.domain.internal.platform

import com.alifwyaa.azanmunich.domain.services.SharedAppScope
import com.alifwyaa.azanmunich.domain.model.SharedPlatformInfo
import com.alifwyaa.azanmunich.domain.services.SharedLogService

/**
 * Platform-specific in-app review prompts.
 *
 * Integrates with platform-specific in-app review APIs (e.g., Google Play In-App Review,
 * iOS StoreKit ReviewController) to request user ratings without leaving the app.
 *
 * @author Created by Abdullah Essa on 24.02.22.
 */
expect class SharedInAppReviewService(
    platformInfo: SharedPlatformInfo,
    appScope: SharedAppScope,
    logService: SharedLogService,
) {

    /**
     * Shows the in-app review dialog.
     *
     * Triggers the platform-specific review prompt. The system may choose not to
     * display the prompt based on platform quotas and policies.
     *
     * @param view Platform-specific view object (Activity on Android, UIViewController on iOS)
     */
    fun showInAppReview(view: Any)
}
