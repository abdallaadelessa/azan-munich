@file:Suppress("UnusedImports")

package com.alifwyaa.azanmunich.data.internal.platform

import com.alifwyaa.azanmunich.data.internal.SharedAzanDataParser
import com.alifwyaa.azanmunich.data.internal.model.SharedAzanDataModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * @author Created by Abdullah Essa on 16.05.21.
 */
@Suppress("ReplaceSafeCallChainWithRun")
actual class SharedAzanDataSource actual constructor(
    private val parser: SharedAzanDataParser
) {

    /**
     * Init the data source
     */
    actual fun init() {
        // Do nothing
    }

    /**
     * @return the list of azan times for the given day
     */
    actual suspend fun get(
        dayOfMonth: Int,
        monthOfYear: Int,
        year: Int
    ): List<SharedAzanDataModel> = withContext(Dispatchers.IO) {
        FirebaseFirestore.getInstance().collection(SharedAzanDataModel.Firebase.COLLECTION_NAME)
            .whereEqualTo(SharedAzanDataModel.Firebase.DAY_OF_MONTH, dayOfMonth)
            .whereEqualTo(SharedAzanDataModel.Firebase.MONTH_OF_YEAR, monthOfYear)
            .whereEqualTo(SharedAzanDataModel.Firebase.YEAR, year)
            .orderBy(SharedAzanDataModel.Firebase.AZAN)
            .get()
            .await()
            ?.documents
            ?.mapNotNull { snapshot -> parser.parse(snapshot.data) }
            ?: emptyList()
    }

    /**
     * @return the list of azan times for the given [ids]
     */
    actual suspend fun get(ids: List<String>): List<SharedAzanDataModel> =
        withContext(Dispatchers.IO) {
            FirebaseFirestore.getInstance()
                .collection(SharedAzanDataModel.Firebase.COLLECTION_NAME)
                .whereIn(SharedAzanDataModel.Firebase.ID, ids)
                .get()
                .await()
                ?.documents
                ?.mapNotNull { snapshot -> parser.parse(snapshot.data) }
                ?: emptyList()
        }
}
