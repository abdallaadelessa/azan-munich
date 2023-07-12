package com.alifwyaa.azanmunich.domain.model

/**
 * @author Created by Abdullah Essa on 16.08.21.
 */
@Suppress("UnnecessaryAbstractClass")
abstract class SharedPlatformInfo(
    val isDebug: Boolean,
    val isRunningUnitTests: Boolean,
) {
    /**
     * Is release build
     */
    val isRelease: Boolean = isDebug.not()

    /**
     * Android Info
     */
    class Android(
        isDebug: Boolean,
        isRunningUnitTests: Boolean,
        val appContext: Any,
    ) : SharedPlatformInfo(isDebug = isDebug, isRunningUnitTests = isRunningUnitTests)

    /**
     * IOS Info
     */
    class Ios(
        isDebug: Boolean,
    ) : SharedPlatformInfo(isDebug = isDebug, isRunningUnitTests = false) {

        var nativeDelegate: NativeDelegate? = null

        /**
         * Implemented by the ios app project
         */
        interface NativeDelegate {
            /**
             * Refresh all app widgets
             */
            fun refreshWidgets()
        }
    }
}





