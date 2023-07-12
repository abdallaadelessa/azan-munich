package com.alifwyaa.azanmunich.domain.internal.platform

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * @author Created by Abdullah Essa on 04.06.21.
 */
actual object SharedDispatchers {
    actual val Main: CoroutineDispatcher = Dispatchers.Main
    actual val Default: CoroutineDispatcher = Dispatchers.Default
}


