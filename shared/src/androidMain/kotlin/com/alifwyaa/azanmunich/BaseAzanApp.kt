package com.alifwyaa.azanmunich

import android.app.Application
import com.alifwyaa.azanmunich.domain.SharedApp
import com.alifwyaa.azanmunich.domain.model.SharedPlatformInfo

/**
 * @author Created by Abdullah Essa on 05.11.21.
 */
abstract class BaseAzanApp : Application() {

    /**
     * Shared app
     */
    abstract val sharedApp: SharedApp

    /**
     * Create the shared app instance
     */
    protected fun createSharedApp(): SharedApp = SharedApp(getPlatformInfo()).apply { onCreate() }

    /**
     * @return the platform info
     */
    abstract fun getPlatformInfo(): SharedPlatformInfo
}
