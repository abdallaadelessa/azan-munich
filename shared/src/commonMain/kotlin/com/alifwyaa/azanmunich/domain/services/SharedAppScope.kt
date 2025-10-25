package com.alifwyaa.azanmunich.domain.services

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

/**
 * Application-wide coroutine scope for managing long-running operations.
 *
 * Delegates to [MainScope] to ensure operations run on the main dispatcher.
 * Used for application lifecycle-bound coroutines that should survive individual screens.
 *
 * @author Created by Abdullah Essa on 20.06.21.
 */
class SharedAppScope : CoroutineScope by MainScope()
