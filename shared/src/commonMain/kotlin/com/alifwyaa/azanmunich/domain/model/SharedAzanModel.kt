package com.alifwyaa.azanmunich.domain.model

import com.alifwyaa.azanmunich.data.internal.model.SharedAzanType

/**
 * @author Created by Abdullah Essa on 24.05.21.
 * The model used in the domain layer
 */
data class SharedAzanModel(
    val id: String,
    val azanType: SharedAzanType,
    val displayName: String,
    val displayTime: String,
    val dateModel: SharedDateModel,
    val timeModel: SharedTimeModel,
)
