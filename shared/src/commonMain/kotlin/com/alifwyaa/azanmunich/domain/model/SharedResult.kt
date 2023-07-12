package com.alifwyaa.azanmunich.domain.model

/**
 * @author Created by Abdullah Essa on 07.07.21.
 */
sealed class SharedResult<out T : Any> {

    /**
     * Success
     */
    data class Success<out T : Any>(val data: T) : SharedResult<T>()

    /**
     * Error
     */
    data class Error(
        val id: Int,
        val message: String,
        val cause: String? = null
    ) : SharedResult<Nothing>()

    /**
     * Returns `true` if this instance represents a successful outcome.
     * In this case [isError] returns `false`.
     */
    val isSuccess: Boolean get() = this is Success

    /**
     * Returns `true` if this instance represents a failed outcome.
     * In this case [isSuccess] returns `false`.
     */
    val isError: Boolean get() = this is Error

    /**
     * Returns the encapsulated value if this instance represents [Success] or `null`
     * if it is [Error].
     *
     */
    fun getOrNull(): T? = when (this) {
        is Error -> null
        is Success -> this.data
    }

    /**
     * Returns the encapsulated [Throwable] exception if this instance represents [Error] or `null`
     * if it is [Success].
     */
    fun errorOrNull(): Error? = when (this) {
        is Error -> this
        else -> null
    }

    /**
     * Callback for success
     */
    inline fun onSuccess(onSuccessBlock: (T) -> Unit) =
        apply { getOrNull()?.also(onSuccessBlock) }

    /**
     * Callback for Error
     */
    inline fun onError(onErrorBlock: (Error) -> Unit) =
        apply { errorOrNull()?.also(onErrorBlock) }

    /**
     * Map the success data to another model
     */
    inline fun <R : Any> mapSuccess(block: (T) -> R): SharedResult<R> = when (this) {
        is Success -> Success(block(data))
        is Error -> this
    }
}
