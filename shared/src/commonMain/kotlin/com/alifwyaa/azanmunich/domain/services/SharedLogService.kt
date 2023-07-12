package com.alifwyaa.azanmunich.domain.services

import com.alifwyaa.azanmunich.domain.internal.platform.SharedCrashReportingService
import com.alifwyaa.azanmunich.domain.model.SharedPlatformInfo
import kotlin.coroutines.cancellation.CancellationException
import kotlin.reflect.KClass

/**
 * @author Created by Abdullah Essa on 11.06.21.
 */
@Suppress("FunctionMinLength")
class SharedLogService(
    platformInfo: SharedPlatformInfo,
    private val crashReportingService: SharedCrashReportingService
) {

    private val ignoredExceptions: List<KClass<out Throwable>> = listOf(
        CancellationException::class,
    )

    /**
     * Is enabled
     */
    val isDebugMode = platformInfo.isDebug


    //region Message

    /**
     * Debug Message
     * IOS
     */
    fun m(message: String) {
        if (isDebugMode) {
            println(message)
        }
    }

    /**
     * Debug Message
     * Shared & Android
     */
    fun m(block: () -> String) {
        if (isDebugMode) {
            println(block())
        }
    }

    /**
     * Debug Paragraph
     * Shared & Android
     */
    inline fun p(block: StringBuilder.() -> Unit) {
        if (isDebugMode) {
            println(StringBuilder().apply(block).toString())
        }
    }

    //endregion

    //region Error

    /**
     * Error
     * IOS
     */
    fun e(
        message: String,
        report: Boolean = false,
    ) {
        e(throwable = Exception(message), report = report)
    }

    /**
     * Error
     * Shared & Android
     */
    fun e(
        throwable: Throwable,
        report: Boolean = false,
        block: StringBuilder.() -> Unit = {}
    ) {
        if (ignoredExceptions.any { ignoredEx -> ignoredEx.isInstance(throwable) }) return

        if (!isDebugMode && report) {
            crashReportingService.report(throwable)
        }

        if (isDebugMode) {
            val message = StringBuilder()
                .apply(block)
                .appendLine(throwable.stackTraceToString())
                .toString()
            println(message)
        }
    }

    //endregion
}
