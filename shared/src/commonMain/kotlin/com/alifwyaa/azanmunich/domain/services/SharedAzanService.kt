package com.alifwyaa.azanmunich.domain.services

import com.alifwyaa.azanmunich.data.internal.model.SharedAzanDataModel
import com.alifwyaa.azanmunich.data.internal.model.SharedAzanType
import com.alifwyaa.azanmunich.data.internal.platform.SharedAzanDataSource
import com.alifwyaa.azanmunich.domain.SharedErrorCodes
import com.alifwyaa.azanmunich.domain.model.SharedAzanModel
import com.alifwyaa.azanmunich.domain.model.SharedDateModel
import com.alifwyaa.azanmunich.domain.model.SharedResult
import com.alifwyaa.azanmunich.domain.model.SharedTimeModel

/**
 * @author Created by Abdullah Essa on 16.05.21.
 */
class SharedAzanService(
    private val dataSource: SharedAzanDataSource,
    private val dateTimeService: SharedDateTimeService,
    private val localizationService: SharedLocalizationService,
    private val logService: SharedLogService,
) {

    //region Public Methods

    /**
     * @return the next azan time
     */
    suspend fun getNextAzan(): SharedResult<SharedAzanModel> = kotlin.runCatching {
        val today = dateTimeService.getToday()
        val tomorrow = dateTimeService.getDayAfter(today)

        val nextAzanToday = dataSource
            .get(
                dayOfMonth = today.day,
                monthOfYear = today.month,
                year = today.year
            ).firstOrNull {
                dateTimeService.isTimeAfter(
                    dateModel = SharedDateModel(day = it.day, month = it.month, year = it.year),
                    timeModel = it.getTimeModel()
                )
            }

        val nextAzanDataModel = nextAzanToday
            ?: dataSource.get(
                dayOfMonth = tomorrow.day,
                monthOfYear = tomorrow.month,
                year = tomorrow.year
            ).firstOrNull()

        nextAzanDataModel?.toDomainModel()

    }.fold(
        {
            when (it) {
                null -> SharedResult.Error(
                    SharedErrorCodes.NO_RESULT,
                    localizationService.strings.errorNoResult
                )
                else -> SharedResult.Success(it)
            }
        },
        {
            logService.e(throwable = it, report = true) { appendLine("getNextAzan Error:") }
            SharedResult.Error(
                id = SharedErrorCodes.UNKNOWN,
                message = localizationService.strings.errorUnknown,
                cause = it.message
            )
        }
    )

    /**
     * @return list of azan times for the given day
     */
    suspend fun getAzanList(dateModel: SharedDateModel): SharedResult<List<SharedAzanModel>> =
        kotlin.runCatching {
            dataSource.get(
                dayOfMonth = dateModel.day,
                monthOfYear = dateModel.month,
                year = dateModel.year
            ).mapNotNull { it.toDomainModel() }
        }.fold(
            {
                when {
                    it.isEmpty() -> SharedResult.Error(
                        SharedErrorCodes.NO_RESULT,
                        localizationService.strings.errorNoResult
                    )
                    else -> SharedResult.Success(it)
                }
            },
            {
                logService.e(throwable = it, report = true) { appendLine("getAzanList Error:") }
                SharedResult.Error(
                    id = SharedErrorCodes.UNKNOWN,
                    message = localizationService.strings.errorUnknown,
                    cause = it.message
                )
            }
        )

    /**
     * @return list of azan times from now including today
     */
    @Suppress("MagicNumber")
    internal suspend fun getAzanList(daysFromNow: Int): List<SharedAzanModel> = dateTimeService
        .getDaysFromNow(daysFromNow)
        .map { it.generateAzanIdsFor() }
        .flatten()
        .chunked(10) // Firestore 'in' filters support a maximum of 10 elements in the value array.
        .mapNotNull { kotlin.runCatching { dataSource.get(it) }.getOrNull() }
        .flatten()
        .mapNotNull { it.toDomainModel() }

    //endregion

    //region Helpers

    private fun SharedAzanDataModel.toDomainModel(): SharedAzanModel? = kotlin.runCatching {
        val azanType = azan.toAzanType() ?: return@runCatching null
        val dayModel = SharedDateModel(day, month, year)
        val timeModel = getTimeModel()
        return@runCatching SharedAzanModel(
            id = id,
            azanType = azanType,
            displayName = localizationService.getAzanDisplayName(azanType),
            displayTime = dateTimeService.getFormattedTime(
                dateModel = dayModel,
                timeModel = timeModel,
                pattern = SharedDateTimeService.TIME_FORMAT
            ),
            dateModel = dayModel,
            timeModel = timeModel,
        )
    }.getOrNull()

    private fun Int.toAzanType() = SharedAzanType.values().getOrNull(this)

    private fun SharedAzanDataModel.getTimeModel(): SharedTimeModel = time.split(":")
        .takeIf { it.size == 2 }
        ?.mapNotNull { it.toIntOrNull() }
        ?.let { timeArray -> SharedTimeModel(timeArray[0], timeArray[1]) }
        ?: SharedTimeModel(0, 0)

    @Suppress("MagicNumber")
    private fun SharedDateModel.generateAzanIdsFor(): List<String> =
        mutableListOf<String>()
            .also { list -> for (index in 0..5) list.add(run { "$day-$month-$year:${index}" }) }


    //endregion
}
