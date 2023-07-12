package com.alifwyaa.azanmunich.domain.services

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

/**
 * @author Created by Abdullah Essa on 20.06.21.
 */
class SharedAppScope : CoroutineScope by MainScope()
