package com.alifwyaa.azanmunich.android.extensions

import android.content.Context
import android.content.pm.PackageInfo

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
