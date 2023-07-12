package com.alifwyaa.azanmunich.android.ui.screens.common

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * @author Created by Abdullah Essa on 01.08.21.
 */
abstract class BaseViewModel<T : BaseState> : ViewModel() {

    /**
     *
     */
    abstract val initialState: T

    private val mutableStateFlow: MutableStateFlow<T> by lazy { MutableStateFlow(initialState) }

    protected val state: T
        get() = mutableStateFlow.value

    /**
     *
     */
    val stateFlow: Flow<T>
        get() = mutableStateFlow


    protected fun computeAndUpdateState(updateStateBlock: (T) -> T = { it }): T {
        val oldState = state
        val newState = updateStateBlock(oldState)
        // skip if the state is the same
        if (oldState == newState) return newState
        mutableStateFlow.tryEmit(newState)
        return newState
    }
}

/**
 *
 */
interface BaseState
