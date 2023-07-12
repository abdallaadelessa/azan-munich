package com.alifwyaa.azanmunich.data.internal.platform

import cocoapods.FirebaseFirestore.FIRDocumentSnapshot
import cocoapods.FirebaseFirestore.FIRFirestore
import cocoapods.FirebaseFirestore.FIRQuerySnapshot
import com.alifwyaa.azanmunich.data.internal.SharedAzanDataParser
import com.alifwyaa.azanmunich.data.internal.model.SharedAzanDataModel
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import platform.Foundation.NSError

/**
 * @author Created by Abdullah Essa on 16.05.21.
 */
actual class SharedAzanDataSource actual constructor(
    private val parser: SharedAzanDataParser
) {

    /**
     * Init the data source
     */
    actual fun init() {
        FIRFirestore.enableLogging(false)
    }

    /**
     * @return the list of azan times for the given day
     */
    actual suspend fun get(
        dayOfMonth: Int,
        monthOfYear: Int,
        year: Int
    ): List<SharedAzanDataModel> = suspendCoroutine { continuation ->
        FIRFirestore.firestore()
            .collectionWithPath(SharedAzanDataModel.Firebase.COLLECTION_NAME)
            .queryWhereField(
                field = SharedAzanDataModel.Firebase.DAY_OF_MONTH,
                isEqualTo = dayOfMonth
            )
            .queryWhereField(
                field = SharedAzanDataModel.Firebase.MONTH_OF_YEAR,
                isEqualTo = monthOfYear
            )
            .queryWhereField(field = SharedAzanDataModel.Firebase.YEAR, isEqualTo = year)
            .queryOrderedByField(SharedAzanDataModel.Firebase.AZAN)
            .getDocumentsWithCompletion { snapShot: FIRQuerySnapshot?, error: NSError? ->
                when {
                    error != null -> continuation.resumeWithException(
                        exception = RuntimeException(
                            "code:${error.code}, " +
                                    "domain:${error.domain}, " +
                                    "localizedDescription: ${error.localizedDescription}"
                        )
                    )
                    else -> continuation.resume(
                        value = snapShot
                            ?.documents
                            ?.mapNotNull { (it as FIRDocumentSnapshot).data() as? Map<String, String> }
                            ?.mapNotNull { parser.parse(it) }
                            ?: emptyList()
                    )
                }
            }
    }

    /**
     * @return the list of azan times for the given [ids]
     */
    actual suspend fun get(ids: List<String>): List<SharedAzanDataModel> =
        suspendCoroutine { continuation ->
            FIRFirestore.firestore()
                .collectionWithPath(SharedAzanDataModel.Firebase.COLLECTION_NAME)
                .queryWhereField(
                    field = SharedAzanDataModel.Firebase.ID,
                    `in` = ids
                )
                .getDocumentsWithCompletion { snapShot: FIRQuerySnapshot?, error: NSError? ->
                    when {
                        error != null -> continuation.resumeWithException(
                            exception = RuntimeException(
                                "code:${error.code}, " +
                                        "domain:${error.domain}, " +
                                        "localizedDescription: ${error.localizedDescription}"
                            )
                        )
                        else -> continuation.resume(
                            value = snapShot
                                ?.documents
                                ?.mapNotNull { (it as FIRDocumentSnapshot).data() as? Map<String, String> }
                                ?.mapNotNull { parser.parse(it) }
                                ?: emptyList()
                        )
                    }
                }
        }

}
