package com.alifwyaa.azanmunich.domain.services

import com.russhwolf.settings.Settings
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * @author Created by Abdullah Essa on 24.02.22.
 */
class SharedPreferenceService(
    private val logService: SharedLogService
) {

    //region Preference

    private val prefs: Settings = Settings()

    //endregion

    //region Accessor & Setters

    /**
     * @return String or [null] by [key]
     */
    fun getStringOrNull(key: String): String? = prefs.getStringOrNull(key)


    /**
     * @return String or [defaultValue] by [key]
     */
    fun getString(key: String, defaultValue: String): String = prefs.getString(key, defaultValue)

    /**
     * @return Int or [defaultValue] by [key]
     */
    fun getInt(key: String, defaultValue: Int): Int = prefs.getInt(key, defaultValue)

    /**
     * @return Boolean or [defaultValue] by [key]
     */
    fun getBoolean(key: String, defaultValue: Boolean): Boolean = prefs.getBoolean(key, defaultValue)


    /**
     * Insert String by [key]
     */
    fun putString(key: String, value: String) {
        prefs.putString(key, value)
    }

    /**
     * Insert Int by [key]
     */
    fun putInt(key: String, value: Int) {
        prefs.putInt(key, value)
    }

    /**
     * Insert Boolean by [key]
     */
    fun putBoolean(key: String, value: Boolean) {
        prefs.putBoolean(key, value)
    }

    //endregion

    //region Delegates

    /**
     * Json delegate
     */
    @OptIn(ExperimentalSerializationApi::class)
    inline fun <reified T : Any> json(
        key: String,
        defaultValue: T,
    ): ReadWriteProperty<Any, T> = object : ReadWriteProperty<Any, T> {

        @Suppress("SwallowedException")
        override fun getValue(
            thisRef: Any,
            property: KProperty<*>
        ): T {
            return try {
                val json: String = getString(key = key, "").takeIf { it.isNotEmpty() } ?: return defaultValue
                Json.decodeFromString(json)
            } catch (e: Exception) {
                defaultValue
            }
        }

        @Suppress("SwallowedException")
        override fun setValue(
            thisRef: Any,
            property: KProperty<*>,
            value: T
        ) {
            try {
                putString(key, Json.encodeToString(value))
            } catch (e: Exception) {
                // do nothing
            }
        }
    }

    /**
     * String Delegate
     */
    fun string(
        key: String,
        defaultValue: String
    ): ReadWriteProperty<Any, String> = object : ReadWriteProperty<Any, String> {
        override fun getValue(
            thisRef: Any,
            property: KProperty<*>
        ): String = try {
            getString(key, defaultValue)
        } catch (e: Exception) {
            logService.e(throwable = e, report = true)
            defaultValue
        }

        override fun setValue(
            thisRef: Any,
            property: KProperty<*>,
            value: String
        ) {
            try {
                putString(key, value)
            } catch (e: Exception) {
                logService.e(throwable = e, report = true)
            }
        }
    }

    /**
     * Int Delegate
     */
    fun int(
        key: String,
        defaultValue: Int,
    ): ReadWriteProperty<Any, Int> = object : ReadWriteProperty<Any, Int> {

        override fun getValue(
            thisRef: Any,
            property: KProperty<*>
        ): Int = try {
            getInt(key, defaultValue)
        } catch (e: Exception) {
            logService.e(throwable = e, report = true)
            defaultValue
        }

        override fun setValue(
            thisRef: Any,
            property: KProperty<*>,
            value: Int
        ) {
            try {
                putInt(key, value)
            } catch (e: Exception) {
                logService.e(throwable = e, report = true)
            }
        }
    }

    //endregion

}
