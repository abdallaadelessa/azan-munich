package com.alifwyaa.azanmunich.domain.internal.platform

import com.alifwyaa.azanmunich.domain.services.SharedAppScope
import com.alifwyaa.azanmunich.domain.model.SharedPlatformInfo
import com.alifwyaa.azanmunich.domain.services.SharedLogService
import platform.StoreKit.SKStoreReviewController

/**
 * @author Created by Abdullah Essa on 24.02.22.
 */
actual class SharedInAppReviewService actual constructor(
    platformInfo: SharedPlatformInfo,
    appScope: SharedAppScope,
    logService: SharedLogService,
) {
    /**
     * Show in app review
     */
    actual fun showInAppReview(view: Any) {
        SKStoreReviewController.requestReview()
    }
}
