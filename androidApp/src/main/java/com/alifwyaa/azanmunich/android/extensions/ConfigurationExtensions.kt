package com.alifwyaa.azanmunich.android.extensions

import android.content.res.Configuration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.alifwyaa.azanmunich.android.extensions.DeviceType.PHONE
import com.alifwyaa.azanmunich.android.extensions.DeviceType.TABLET_10_INCH
import com.alifwyaa.azanmunich.android.extensions.DeviceType.TABLET_7_INCH

/**
 * @author Created by Abdullah Essa on 02.10.21.
 */

/**
 * @return DeviceType based on screen width
 */
@Suppress("MagicNumber")
val Configuration.deviceType: DeviceType
    get() {
        val screenWidthDp = screenWidthDp
        return when {
            screenWidthDp >= 720 -> TABLET_10_INCH
            screenWidthDp >= 600 -> TABLET_7_INCH
            else -> PHONE
        }
    }

/**
 * Screen horizontal margin
 */
@Suppress("MagicNumber")
val Configuration.screenHorizontalMargin: Dp
    get() = when (deviceType) {
        TABLET_10_INCH -> 150.dp
        TABLET_7_INCH -> 100.dp
        else -> 24.dp
    }

/**
 * Screen horizontal margin
 */
@Suppress("MagicNumber")
val Configuration.dialogHorizontalMargin: Dp
    get() = when (deviceType) {
        PHONE -> 0.dp
        else -> screenHorizontalMargin
    }

/**
 * Device type
 */
enum class DeviceType {
    TABLET_10_INCH,
    TABLET_7_INCH,
    PHONE,
}
