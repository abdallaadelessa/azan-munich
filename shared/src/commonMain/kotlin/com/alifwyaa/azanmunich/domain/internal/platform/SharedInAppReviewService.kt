package com.alifwyaa.azanmunich.domain.internal.platform

import com.alifwyaa.azanmunich.domain.services.SharedAppScope
import com.alifwyaa.azanmunich.domain.model.SharedPlatformInfo
import com.alifwyaa.azanmunich.domain.services.SharedLogService

/**
 * @author Created by Abdullah Essa on 24.02.22.
 */
expect class SharedInAppReviewService(
    platformInfo: SharedPlatformInfo,
    appScope: SharedAppScope,
    logService: SharedLogService,
) {

    /**
     * Show in app review
     */
    fun showInAppReview(view: Any)
}
