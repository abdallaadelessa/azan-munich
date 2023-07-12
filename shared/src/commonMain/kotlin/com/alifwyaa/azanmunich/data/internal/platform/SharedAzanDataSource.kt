package com.alifwyaa.azanmunich.data.internal.platform

import com.alifwyaa.azanmunich.data.internal.SharedAzanDataParser
import com.alifwyaa.azanmunich.data.internal.model.SharedAzanDataModel

/**
 * @author Created by Abdullah Essa on 16.05.21.
 */
expect class SharedAzanDataSource(parser: SharedAzanDataParser) {
    /**
     * Init the data source
     */
    fun init()

    /**
     * @return the list of azan times for the given day
     */
    suspend fun get(
        dayOfMonth: Int,
        monthOfYear: Int,
        year: Int
    ): List<SharedAzanDataModel>

    /**
     * Firestore 'in' filters support a maximum of 10 elements in the value array.
     * @return the list of azan times for the given [ids]
     */
    suspend fun get(ids: List<String>): List<SharedAzanDataModel>
}
