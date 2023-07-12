package com.alifwyaa.azanmunich.extensions

import android.content.Context
import com.alifwyaa.azanmunich.BaseAzanApp
import com.alifwyaa.azanmunich.domain.SharedApp

/**
 * @author Created by Abdullah Essa on 25.02.22.
 */

/**
 * get the shared app from context
 */
val Context.sharedApp: SharedApp
    get() = (applicationContext as BaseAzanApp).sharedApp
