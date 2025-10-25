package com.alifwyaa.azanmunich.domain.services

import com.alifwyaa.azanmunich.domain.internal.tracking.SharedCrashReportingService
import com.alifwyaa.azanmunich.domain.model.SharedPlatformInfo
import kotlin.coroutines.cancellation.CancellationException
import kotlin.reflect.KClass

/**
 * Provides logging and error reporting capabilities.
 *
 * Handles debug messages, error logging, and crash reporting. Logging is only
 * active in debug mode. Errors can optionally be reported to crash reporting service.
 *
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
     * Logs a debug message (iOS variant).
     *
     * Only active in debug mode.
     *
     * @param message The message to log
     */
    fun m(message: String) {
        if (isDebugMode) {
            println(message)
        }
    }

    /**
     * Logs a debug message using a lambda (Shared & Android variant).
     *
     * Only active in debug mode. The lambda is only evaluated in debug mode.
     *
     * @param block Lambda that produces the message
     */
    fun m(block: () -> String) {
        if (isDebugMode) {
            println(block())
        }
    }

    /**
     * Logs a multi-line debug message (Shared & Android variant).
     *
     * Only active in debug mode. Useful for logging structured data.
     *
     * @param block Lambda with StringBuilder receiver to build the message
     */
    inline fun p(block: StringBuilder.() -> Unit) {
        if (isDebugMode) {
            println(StringBuilder().apply(block).toString())
        }
    }

    //endregion

    //region Error

    /**
     * Logs an error from a message (iOS variant).
     *
     * @param message The error message
     * @param report Whether to report the error to crash reporting service
     */
    fun e(
        message: String,
        report: Boolean = false,
    ) {
        e(throwable = Exception(message), report = report)
    }

    /**
     * Logs an error with optional crash reporting (Shared & Android variant).
     *
     * Ignores CancellationException. Reports to crash service in production if requested.
     * Prints stack trace in debug mode.
     *
     * @param throwable The error to log
     * @param report Whether to report the error to crash reporting service
     * @param block Optional lambda to add context to the error message
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
