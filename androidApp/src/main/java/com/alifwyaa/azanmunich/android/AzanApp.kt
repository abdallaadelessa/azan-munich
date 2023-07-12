package com.alifwyaa.azanmunich.android

import com.alifwyaa.azanmunich.BaseAzanApp
import com.alifwyaa.azanmunich.domain.SharedApp
import com.alifwyaa.azanmunich.domain.model.SharedPlatformInfo.Android


/**
 * @author Created by Abdullah Essa on 04.06.21.
 */
class AzanApp : BaseAzanApp() {

    override lateinit var sharedApp: SharedApp

    override fun onCreate() {
        super.onCreate()
        sharedApp = createSharedApp()
    }

    override fun getPlatformInfo() = Android(
        isDebug = BuildConfig.DEBUG,
        isRunningUnitTests = false,
        appContext = this@AzanApp
    )
}
