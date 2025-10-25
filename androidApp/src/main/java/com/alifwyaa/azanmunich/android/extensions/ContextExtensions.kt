package com.alifwyaa.azanmunich.android.extensions

import android.content.Context
import android.content.pm.PackageInfo
import com.alifwyaa.azanmunich.BaseAzanApp
import com.alifwyaa.azanmunich.domain.SharedApp

/**
 * @author Created by Abdullah Essa on 01.10.21.
 */

/**
 * @return App version
 */
val Context.appVersion: String
    get() {
        val packageInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
        return "${packageInfo.versionName}.${packageInfo.versionCode}"
    }

/**
 * get the shared app from context
 */
val Context.sharedApp: SharedApp
    get() = (applicationContext as BaseAzanApp).sharedApp