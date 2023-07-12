package com.alifwyaa.azanmunich.domain.internal.platform

import android.app.Activity
import com.alifwyaa.azanmunich.domain.services.SharedAppScope
import com.alifwyaa.azanmunich.domain.model.SharedPlatformInfo
import com.alifwyaa.azanmunich.domain.services.SharedLogService
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

/**
 * @author Created by Abdullah Essa on 24.02.22.
 */
actual class SharedInAppReviewService actual constructor(
    platformInfo: SharedPlatformInfo,
    private val appScope: SharedAppScope,
    private val logService: SharedLogService,
) {
    /**
     * Show in app review
     */
    actual fun showInAppReview(view: Any) {
        val actWeakRef: WeakReference<Activity> = WeakReference(view as? Activity)
        appScope.launch(SharedDispatchers.Main) {
            try {
                val activity = actWeakRef.get() ?: return@launch
                logService.m { "NativeInAppReview Started" }
                val manager = ReviewManagerFactory.create(activity)
                val requestReview: ReviewInfo = manager.requestReview()
                manager.launchReview(activity, requestReview)
                logService.m { "NativeInAppReview Finished" }
            } catch (throwable: Throwable) {
                logService.m { "NativeInAppReview Error" }
                logService.e(throwable = throwable, report = true)
            }
        }
    }
}
