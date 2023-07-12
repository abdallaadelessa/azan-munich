package com.alifwyaa.azanmunich.android.ui.screens.common

import androidx.compose.runtime.Stable

/**
 *
 */
@Stable
sealed class DataPlaceHolder<out T : Any> {

    /**
     *
     */
    @Stable
    object Loading : DataPlaceHolder<Nothing>()

    /**
     *
     */
    @Stable
    data class Success<T : Any>(val data: T) : DataPlaceHolder<T>()

    /**
     *
     */
    @Stable
    data class Error(
        val message: String,
        val actionText: String,
    ) : DataPlaceHolder<Nothing>()

    /**
     *
     */
    fun getOrNull(): T? = (this as? Success<T>)?.data

    /**
     * Map the success data to another model
     */
    inline fun <R : Any> mapSuccess(block: (T) -> R): DataPlaceHolder<R> = when (this) {
        is DataPlaceHolder.Loading -> this
        is DataPlaceHolder.Error -> this
        is DataPlaceHolder.Success -> DataPlaceHolder.Success(block(data))
    }
}
