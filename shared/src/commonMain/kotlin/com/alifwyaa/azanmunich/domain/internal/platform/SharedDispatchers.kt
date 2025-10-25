package com.alifwyaa.azanmunich.domain.internal.platform

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Platform-specific coroutine dispatchers.
 *
 * Provides access to platform-appropriate dispatchers for main thread
 * and background operations. Expect/actual pattern allows platform-specific
 * implementations while maintaining a common interface.
 *
 * @author Created by Abdullah Essa on 04.06.21.
 */
expect object SharedDispatchers {
    /**
     * Dispatcher for main thread operations (UI updates, etc.).
     */
    val Main: CoroutineDispatcher

    /**
     * Dispatcher for CPU-intensive background work.
     */
    val Default: CoroutineDispatcher
}
