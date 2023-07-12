package com.alifwyaa.azanmunich.android.ui.screens.home

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.alifwyaa.azanmunich.android.ui.screens.common.BaseState
import com.alifwyaa.azanmunich.android.ui.screens.common.BaseViewModel
import com.alifwyaa.azanmunich.android.ui.screens.common.DataPlaceHolder
import com.alifwyaa.azanmunich.domain.SharedStrings
import com.alifwyaa.azanmunich.domain.internal.platform.Screen
import com.alifwyaa.azanmunich.domain.internal.platform.SharedTrackingService
import com.alifwyaa.azanmunich.domain.model.SharedAzanModel
import com.alifwyaa.azanmunich.domain.model.SharedDateModel
import com.alifwyaa.azanmunich.domain.model.SharedResult
import com.alifwyaa.azanmunich.domain.model.SharedTimeUntil
import com.alifwyaa.azanmunich.domain.services.SharedAzanService
import com.alifwyaa.azanmunich.domain.services.SharedDateTimeService
import com.alifwyaa.azanmunich.domain.services.SharedLocalizationService
import com.alifwyaa.azanmunich.domain.services.SharedSettingsService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * HomeViewModel
 */
class HomeViewModel(
    private val azanService: SharedAzanService,
    private val dateTimeService: SharedDateTimeService,
    private val localizationService: SharedLocalizationService,
    private val settingsService: SharedSettingsService,
    trackingService: SharedTrackingService,
) : BaseViewModel<HomeViewModel.State>() {

    override val initialState: State by lazy {
        State(
            azanDate = State.AzanDate(
                date = dateTimeService.getToday(),
                westernFormattedDate = dateTimeService
                    .getWesternFormattedDate(dateTimeService.getToday()),
                islamicFormattedDate = dateTimeService
                    .getIslamicFormattedDate(dateTimeService.getToday())
            ),
        )
    }

    private val strings: SharedStrings
        get() = localizationService.strings

    private var lastFetchedNextAzan: SharedAzanModel? = null

    private var fetchNextAzanJob: Job? = null

    init {
        trackingService.trackScreen(Screen.HOME)
        scheduleNextAzanFetch()
        loadAzanList(dateTimeService.getToday())
        listenToSettingsChanges()
    }

    //region Public Methods

    /**
     * Retry using the last date
     */
    fun reloadAzanList() {
        loadAzanList(state.azanDate.date)
    }

    /**
     * Go to previous day
     */
    fun goToDayBefore() {
        val dayBefore = dateTimeService.getDayBefore(state.azanDate.date)
        loadAzanList(dayBefore)
    }

    /**
     * Go to next day
     */
    fun goToDayAfter() {
        val dayAfter = dateTimeService.getDayAfter(state.azanDate.date)
        loadAzanList(dayAfter)
    }

    /**
     * Go to next azan day
     */
    fun goToNextAzanDay() {
        val nextAzan = state.nextAzan

        if (nextAzan !is DataPlaceHolder.Success
            || nextAzan.data.dateModel == state.azanDate.date
        ) return

        loadAzanList(nextAzan.data.dateModel)
    }

    //endregion

    //region Helpers

    private fun loadAzanList(date: SharedDateModel) {
        computeAndUpdateState {
            it.copy(
                azanDate = State.AzanDate(
                    date = date,
                    westernFormattedDate = dateTimeService.getWesternFormattedDate(date),
                    islamicFormattedDate = dateTimeService.getIslamicFormattedDate(date),
                ),
                azanList = DataPlaceHolder.Loading
            )
        }

        viewModelScope.launch(Dispatchers.Main) {
            delay(300)
            val result = azanService.getAzanList(date)
                .mapSuccess { list ->
                    list.map { sharedAzanModel ->
                        State.Azan(
                            model = sharedAzanModel,
                            isNextAzan = sharedAzanModel.id == state.nextAzan.getOrNull()?.id,
                            isNotificationEnabled = settingsService.isAzanEnabled(sharedAzanModel.azanType)
                        )
                    }
                }
            when (result) {
                is SharedResult.Success -> {
                    computeAndUpdateState {
                        it.copy(azanList = DataPlaceHolder.Success(result.data))
                    }
                }
                is SharedResult.Error -> {
                    computeAndUpdateState {
                        it.copy(
                            azanList = DataPlaceHolder.Error(
                                message = result.message,
                                actionText = strings.retry
                            )
                        )
                    }
                }
            }
        }
    }

    private fun scheduleNextAzanFetch() {
        fetchNextAzanJob?.cancel()
        fetchNextAzanJob = viewModelScope.launch(Dispatchers.Default) {
            while (true) {
                ensureActive()

                val lastFetchedNextAzan = lastFetchedNextAzan

                @Suppress("ReplaceSafeCallChainWithRun")
                val sharedTimeUntil: SharedTimeUntil? = lastFetchedNextAzan
                    ?.let { dateTimeService.getTimeUntil(it.dateModel, it.timeModel) }
                    ?.takeIf { it.isInFuture }

                if (sharedTimeUntil != null) {
                    updateNextAzanState(
                        azanModel = lastFetchedNextAzan,
                        sharedTimeUntil = sharedTimeUntil
                    )
                    continue
                }

                when (val result = azanService.getNextAzan()) {
                    is SharedResult.Success -> {
                        val azanModel = result.data
                        updateNextAzanState(
                            azanModel = azanModel,
                            sharedTimeUntil = dateTimeService.getTimeUntil(
                                dateModel = azanModel.dateModel,
                                timeModel = azanModel.timeModel
                            )
                        )
                    }
                    is SharedResult.Error ->
                        withContext(Dispatchers.Main) {
                            computeAndUpdateState {
                                it.copy(
                                    nextAzan = DataPlaceHolder.Error(
                                        message = result.message,
                                        actionText = strings.retry
                                    )
                                )
                            }
                        }
                }

                delay(500)
            }
        }
    }

    private suspend fun updateNextAzanState(
        azanModel: SharedAzanModel,
        sharedTimeUntil: SharedTimeUntil
    ) {
        withContext(Dispatchers.Main) {
            val nextAzanNameAndTime = azanModel.displayName + " " + azanModel.displayTime
            val timeUntilNextAzan = localizationService.getTimeUntilNextAzan(sharedTimeUntil)

            lastFetchedNextAzan = azanModel

            computeAndUpdateState {

                val updatedAzanList: DataPlaceHolder<List<State.Azan>> =
                    it.azanList.mapSuccess { azanList ->
                        azanList.map { azan ->
                            azan.copy(isNextAzan = azan.model.id == azanModel.id)
                        }
                    }

                it.copy(
                    azanList = updatedAzanList,
                    nextAzan = DataPlaceHolder.Success(
                        State.NextAzan(
                            id = azanModel.id,
                            title = strings.nextPrayer,
                            text = nextAzanNameAndTime,
                            periodText = timeUntilNextAzan,
                            dateModel = azanModel.dateModel,
                        )
                    )
                )
            }
        }
    }

    private fun listenToSettingsChanges() {
        viewModelScope.launch(Dispatchers.Main) {
            settingsService.eventsFlow
                .collect {
                    reloadAzanList()
                }
        }
    }

    //endregion

    //region State

    /**
     * State
     */
    @Stable
    data class State(
        val azanDate: AzanDate = AzanDate(
            date = SharedDateModel(day = 0, month = 0, year = 0),
            westernFormattedDate = "",
            islamicFormattedDate = ""
        ),
        val nextAzan: DataPlaceHolder<NextAzan> = DataPlaceHolder.Loading,
        val azanList: DataPlaceHolder<List<Azan>> = DataPlaceHolder.Loading,
    ) : BaseState {

        /**
         *
         */
        @Stable
        data class AzanDate(
            val date: SharedDateModel,
            val westernFormattedDate: String,
            val islamicFormattedDate: String
        )

        /**
         *
         */
        @Stable
        data class Azan(
            val model: SharedAzanModel,
            val isNextAzan: Boolean,
            val isNotificationEnabled: Boolean,
        )

        /**
         *
         */
        @Stable
        data class NextAzan(
            val id: String,
            val title: String,
            val text: String,
            val periodText: String,
            val dateModel: SharedDateModel,
        )
    }

    //endregion

}


