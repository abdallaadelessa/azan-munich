package com.alifwyaa.azanmunich.data.internal

import com.alifwyaa.azanmunich.data.internal.model.SharedAzanDataModel

/**
 * @author Created by Abdullah Essa on 20.05.21.
 */
class SharedAzanDataParser {
    /**
     * @return [SharedAzanDataModel] from map
     */
    fun parse(map: Map<String, Any>?): SharedAzanDataModel? {
        if (map == null) return null

        val id = map[SharedAzanDataModel.Firebase.ID] as? String ?: return null
        val dayOfMonth = map[SharedAzanDataModel.Firebase.DAY_OF_MONTH] as? Long ?: return null
        val monthOfYear =
            map[SharedAzanDataModel.Firebase.MONTH_OF_YEAR] as? Long ?: return null
        val year = map[SharedAzanDataModel.Firebase.YEAR] as? Long ?: return null
        val time = map[SharedAzanDataModel.Firebase.TIME] as? String ?: return null
        val azan = map[SharedAzanDataModel.Firebase.AZAN] as? Long ?: return null

        return SharedAzanDataModel(
            id = id,
            day = dayOfMonth.toInt(),
            month = monthOfYear.toInt(),
            year = year.toInt(),
            time = time,
            azan = azan.toInt()
        )
    }
}
