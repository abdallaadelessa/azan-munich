package com.alifwyaa.azanmunich.android.extensions

import android.content.Context
import com.alifwyaa.azanmunich.domain.SharedApp
import com.alifwyaa.azanmunich.domain.model.SharedPlatformInfo

/**
 * @author Created by Abdullah Essa on 01.10.21.
 */

/**
 * @return the application context
 */
val SharedApp.appContext: Context
    get() {
        return (platformInfo as SharedPlatformInfo.Android).appContext as Context
    }
