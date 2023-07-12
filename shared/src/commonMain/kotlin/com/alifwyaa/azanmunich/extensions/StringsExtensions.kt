package com.alifwyaa.azanmunich.extensions

/**
 * @author Created by Abdullah Essa on 06.03.22.
 */

/**
 * @return the string if not empty or null
 */
fun String?.takeIfNotEmpty(): String? = this?.takeIf { it.isNotEmpty() }


/**
 * @return the string if not blank or null
 */
fun String?.takeIfNotBlank(): String? = this?.takeIf { it.isNotBlank() }
