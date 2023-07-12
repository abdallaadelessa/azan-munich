package com.alifwyaa.azanmunich.android.ui.screens.settings

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.alifwyaa.azanmunich.android.extensions.appVersion
import com.alifwyaa.azanmunich.android.ui.screens.common.BaseState
import com.alifwyaa.azanmunich.android.ui.screens.common.BaseViewModel
import com.alifwyaa.azanmunich.android.ui.screens.settings.SettingsViewModel.State
import com.alifwyaa.azanmunich.domain.SharedStrings
import com.alifwyaa.azanmunich.domain.model.settings.ShardAppSoundSettingsModel
import com.alifwyaa.azanmunich.domain.model.settings.SharedAppBaseSettingsModel
import com.alifwyaa.azanmunich.domain.model.settings.SharedAppLanguageSettingsModel
import com.alifwyaa.azanmunich.domain.model.settings.SharedAppThemeSettingsModel
import com.alifwyaa.azanmunich.domain.internal.platform.Screen
import com.alifwyaa.azanmunich.domain.services.SharedLocalizationService
import com.alifwyaa.azanmunich.domain.services.SharedSettingsService
import com.alifwyaa.azanmunich.domain.internal.platform.SharedTrackingService
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * @author Created by Abdullah Essa on 01.08.21.
 */
@SuppressLint("StaticFieldLeak")
class SettingsViewModel(
    private val appContext: Context,
    private val settingsService: SharedSettingsService,
    private val localizationService: SharedLocalizationService,
    trackingService: SharedTrackingService,
) : BaseViewModel<State>() {

    private val strings: SharedStrings
        get() = localizationService.strings

    override val initialState: State by lazy(LazyThreadSafetyMode.NONE) {
        createState()
    }

    init {
        trackingService.trackScreen(Screen.SETTINGS)
        listenToSettingsChanges()
    }

    //region Public Methods

    /**
     *
     */
    fun <T : SharedAppBaseSettingsModel> showDialogPicker(
        pickerModel: State.PickerModel<T>,
        onItemSelected: (Int) -> Unit,
        onDismiss: () -> Unit,
    ) {
        computeAndUpdateState {
            it.copy(
                dialog = State.DialogPickerState.Shown(
                    State.DialogPickerState.Shown.Request(
                        title = pickerModel.title,
                        positiveBtn = pickerModel.positiveBtn,
                        negativeBtn = pickerModel.negativeBtn,
                        selectedIndex = pickerModel.allValues.indexOf(pickerModel.selectedValue),
                        items = pickerModel.allValues.map { value -> value.displayName },
                        onItemSelected = onItemSelected,
                        onDismiss = onDismiss,
                    )
                )
            )
        }
    }

    /**
     *
     */
    fun hideDialogPicker() {
        computeAndUpdateState { it.copy(dialog = State.DialogPickerState.Hidden) }
    }

    /**
     *
     */
    fun setAppTheme(value: SharedAppThemeSettingsModel) {
        settingsService.appThemeModel = value
        computeAndUpdateState { oldState ->
            oldState.copy(themeSettings = oldState.themeSettings.copy(selectedValue = value))
        }
    }

    /**
     *
     */
    fun setAppLanguage(value: SharedAppLanguageSettingsModel) {
        settingsService.appLanguageModel = value
        computeAndUpdateState { oldState ->
            oldState.copy(languageSettings = oldState.languageSettings.copy(selectedValue = value))
        }
    }

    /**
     *
     */
    fun setAppSound(value: ShardAppSoundSettingsModel) {
        settingsService.appSoundModel = value
        computeAndUpdateState { oldState ->
            oldState.copy(soundSettings = oldState.soundSettings.copy(selectedValue = value))
        }
    }

    /**
     *
     */
    fun setFajrEnabled(value: Boolean) {
        settingsService.isFajrEnabled = value
        computeAndUpdateState { oldState ->
            oldState.copy(fajrSettings = oldState.fajrSettings.copy(value = value))
        }
    }

    /**
     *
     */
    fun setSunriseEnabled(value: Boolean) {
        settingsService.isSunriseEnabled = value
        computeAndUpdateState { oldState ->
            oldState.copy(sunriseSettings = oldState.sunriseSettings.copy(value = value))
        }
    }

    /**
     *
     */
    fun setDhuhrEnabled(value: Boolean) {
        settingsService.isDhuhrEnabled = value
        computeAndUpdateState { oldState ->
            oldState.copy(dhuhrSettings = oldState.dhuhrSettings.copy(value = value))
        }
    }

    /**
     *
     */
    fun setAsrEnabled(value: Boolean) {
        settingsService.isAsrEnabled = value
        computeAndUpdateState { oldState ->
            oldState.copy(asrSettings = oldState.asrSettings.copy(value = value))
        }
    }

    /**
     *
     */
    fun setMaghribEnabled(value: Boolean) {
        settingsService.isMaghribEnabled = value
        computeAndUpdateState { oldState ->
            oldState.copy(maghribSettings = oldState.maghribSettings.copy(value = value))
        }
    }

    /**
     *
     */
    fun setIshaEnabled(value: Boolean) {
        settingsService.isIshaEnabled = value
        computeAndUpdateState { oldState ->
            oldState.copy(ishaSettings = oldState.ishaSettings.copy(value = value))
        }
    }

    //endregion

    //region Helpers

    private fun listenToSettingsChanges() {
        viewModelScope.launch {
            settingsService
                .eventsFlow
                .collect {
                    computeAndUpdateState { createState() }
                }
        }
    }

    //endregion

    //region State

    private fun createState() = State(
        generalSectionTitle = strings.generalSettings,
        notificationsSectionTitle = strings.notificationSettings,
        infoSectionTitle = strings.infoSettings,
        themeSettings = State.PickerModel(
            title = strings.appTheme,
            positiveBtn = strings.ok,
            negativeBtn = strings.cancel,
            selectedValue = settingsService.appThemeModel,
            allValues = settingsService.appThemeList,
        ),
        languageSettings = State.PickerModel(
            title = strings.appLanguage,
            positiveBtn = strings.ok,
            negativeBtn = strings.cancel,
            selectedValue = settingsService.appLanguageModel,
            allValues = settingsService.appLanguageList,
        ),
        soundSettings = State.PickerModel(
            title = strings.appSound,
            positiveBtn = strings.ok,
            negativeBtn = strings.cancel,
            selectedValue = settingsService.appSoundModel,
            allValues = settingsService.appSoundList,
        ),
        fajrSettings = State.SwitchModel(
            title = strings.fajr,
            value = settingsService.isFajrEnabled
        ),
        sunriseSettings = State.SwitchModel(
            title = strings.sunrise,
            value = settingsService.isSunriseEnabled
        ),
        dhuhrSettings = State.SwitchModel(
            title = strings.dhuhr,
            value = settingsService.isDhuhrEnabled
        ),
        asrSettings = State.SwitchModel(
            title = strings.asr,
            value = settingsService.isAsrEnabled
        ),
        maghribSettings = State.SwitchModel(
            title = strings.maghrib,
            value = settingsService.isMaghribEnabled
        ),
        ishaSettings = State.SwitchModel(
            title = strings.isha,
            value = settingsService.isIshaEnabled
        ),
        versionInfo = State.InfoModel(
            strings.appVersion,
            appContext.appVersion,
        ),
        aboutInfo = State.InfoModel(
            strings.about,
            strings.aboutDescription,
        ),
        dialog = State.DialogPickerState.Hidden
    )

    /**
     *
     */
    data class State(
        val generalSectionTitle: String,
        val notificationsSectionTitle: String,
        val infoSectionTitle: String,
        val themeSettings: PickerModel<SharedAppThemeSettingsModel>,
        val languageSettings: PickerModel<SharedAppLanguageSettingsModel>,
        val soundSettings: PickerModel<ShardAppSoundSettingsModel>,
        val fajrSettings: SwitchModel,
        val sunriseSettings: SwitchModel,
        val dhuhrSettings: SwitchModel,
        val asrSettings: SwitchModel,
        val maghribSettings: SwitchModel,
        val ishaSettings: SwitchModel,
        val versionInfo: InfoModel,
        val aboutInfo: InfoModel,
        val dialog: DialogPickerState,
    ) : BaseState {

        /**
         *
         */
        data class PickerModel<T : SharedAppBaseSettingsModel>(
            val title: String,
            val positiveBtn: String,
            val negativeBtn: String,
            val selectedValue: T,
            val allValues: List<T>,
        )


        /**
         *
         */
        data class InfoModel(
            val title: String,
            val value: String,
        )

        /**
         *
         */
        data class SwitchModel(
            val title: String,
            val value: Boolean,
        )

        /**
         *
         */
        sealed class DialogPickerState {
            /**
             *
             */
            object Hidden : DialogPickerState()

            /**
             *
             */
            data class Shown(val request: Request) : DialogPickerState() {
                /**
                 *
                 */
                data class Request(
                    val title: String,
                    val positiveBtn: String,
                    val negativeBtn: String,
                    val selectedIndex: Int,
                    val items: List<String>,
                    val onItemSelected: (Int) -> Unit,
                    val onDismiss: () -> Unit,
                )
            }
        }
    }

    //endregion

}
