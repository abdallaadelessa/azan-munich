package com.alifwyaa.azanmunich.domain.internal.platform

import kotlinx.coroutines.CoroutineDispatcher

/**
 * @author Created by Abdullah Essa on 04.06.21.
 */
expect object SharedDispatchers {
    val Main: CoroutineDispatcher
    val Default: CoroutineDispatcher
}
