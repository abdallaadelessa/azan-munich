package com.alifwyaa.azanmunich.domain.internal.platform

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Delay
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Runnable
import platform.darwin.DISPATCH_QUEUE_PRIORITY_DEFAULT
import platform.darwin.dispatch_after
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_global_queue
import platform.darwin.dispatch_get_main_queue
import kotlin.coroutines.CoroutineContext

/**
 * @author Created by Abdullah Essa on 04.06.21.
 */
@OptIn(InternalCoroutinesApi::class)
actual object SharedDispatchers {
    actual val Main: CoroutineDispatcher = object : CoroutineDispatcher(), Delay {
        private val queue get() = dispatch_get_main_queue()

        override fun dispatch(context: CoroutineContext, block: Runnable) {
            dispatch_async(queue, block::run)
        }

        override fun scheduleResumeAfterDelay(
            timeMillis: Long,
            continuation: CancellableContinuation<Unit>
        ) {

            dispatch_after(
                @Suppress("MagicNumber")
                timeMillis.times(1000).toULong(),
                queue
            ) {
                continuation.resume(Unit) {}
            }
        }
    }

    actual val Default: CoroutineDispatcher = object : CoroutineDispatcher(), Delay {
        private val queue
            get() = dispatch_get_global_queue(
                DISPATCH_QUEUE_PRIORITY_DEFAULT.toLong(),
                0.toULong()
            )

        override fun dispatch(context: CoroutineContext, block: Runnable) {
            dispatch_async(queue, block::run)
        }

        override fun scheduleResumeAfterDelay(
            timeMillis: Long,
            continuation: CancellableContinuation<Unit>
        ) {

            dispatch_after(
                @Suppress("MagicNumber")
                timeMillis.times(1000).toULong(),
                queue
            ) {
                continuation.resume(Unit) {}
            }
        }
    }

}


