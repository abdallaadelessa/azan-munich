@file:Suppress("LongMethod","UndocumentedPublicFunction")
package com.alifwyaa.azanmunich.android.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.alifwyaa.azanmunich.android.extensions.dialogHorizontalMargin
import com.alifwyaa.azanmunich.android.extensions.screenHorizontalMargin
import com.alifwyaa.azanmunich.android.ui.screens.settings.SettingsViewModel.State
import com.alifwyaa.azanmunich.android.ui.screens.settings.SettingsViewModel.State.PickerModel
import com.alifwyaa.azanmunich.android.ui.screens.settings.SettingsViewModel.State.SwitchModel
import com.alifwyaa.azanmunich.domain.model.settings.ShardAppSoundSettingsModel
import com.alifwyaa.azanmunich.domain.model.settings.SharedAppBaseSettingsModel
import com.alifwyaa.azanmunich.domain.model.settings.SharedAppLanguageSettingsModel
import com.alifwyaa.azanmunich.domain.model.settings.SharedAppThemeSettingsModel

//region Screen

/**
 * SettingsScreen
 */
@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val state by viewModel.stateFlow.collectAsState(viewModel.initialState)

    DialogPicker(pickerState = state.dialog)

    SettingsContent(
        state = state,
        onThemeValueClicked = {
            showDialogPicker(viewModel, state.themeSettings) { v -> viewModel.setAppTheme(v) }
        },
        onLanguageValueClicked = {
            showDialogPicker(viewModel, state.languageSettings) { v -> viewModel.setAppLanguage(v) }
        },
        onSoundValueClicked = {
            showDialogPicker(viewModel, state.soundSettings) { v -> viewModel.setAppSound(v) }
        },
        onFajrEnabledChanged = { viewModel.setFajrEnabled(it) },
        onSunriseEnabledChanged = { viewModel.setSunriseEnabled(it) },
        onDhuhrEnabledChanged = { viewModel.setDhuhrEnabled(it) },
        onAsrEnabledChanged = { viewModel.setAsrEnabled(it) },
        onMaghribEnabledChanged = { viewModel.setMaghribEnabled(it) },
        onIshaEnabledChanged = { viewModel.setIshaEnabled(it) },
    )
}

@Suppress("LongParameterList")
@Composable
fun SettingsContent(
    state: State,
    onThemeValueClicked: () -> Unit = {},
    onLanguageValueClicked: () -> Unit = {},
    onSoundValueClicked: () -> Unit = {},
    onFajrEnabledChanged: (Boolean) -> Unit = {},
    onSunriseEnabledChanged: (Boolean) -> Unit = {},
    onDhuhrEnabledChanged: (Boolean) -> Unit = {},
    onAsrEnabledChanged: (Boolean) -> Unit = {},
    onMaghribEnabledChanged: (Boolean) -> Unit = {},
    onIshaEnabledChanged: (Boolean) -> Unit = {},
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(
            horizontal = LocalConfiguration.current.screenHorizontalMargin,
            vertical = 16.dp
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        item {
            CreateGeneralSection(
                title = state.generalSectionTitle,
                themeSettings = state.themeSettings,
                languageSettings = state.languageSettings,
                onThemeValueClicked = onThemeValueClicked,
                onLanguageValueClicked = onLanguageValueClicked
            )
        }
        item {
            CreateNotificationsSection(
                title = state.notificationsSectionTitle,
                soundSettings = state.soundSettings,
                fajrSettings = state.fajrSettings,
                sunriseSettings = state.sunriseSettings,
                dhuhrSettings = state.dhuhrSettings,
                asrSettings = state.asrSettings,
                maghribSettings = state.maghribSettings,
                ishaSettings = state.ishaSettings,
                onSoundValueClicked = onSoundValueClicked,
                onFajrEnabledChanged = onFajrEnabledChanged,
                onSunriseEnabledChanged = onSunriseEnabledChanged,
                onDhuhrEnabledChanged = onDhuhrEnabledChanged,
                onAsrEnabledChanged = onAsrEnabledChanged,
                onMaghribEnabledChanged = onMaghribEnabledChanged,
                onIshaEnabledChanged = onIshaEnabledChanged,
            )
        }
        item {
            CreateInfoSection(
                title = state.infoSectionTitle,
                versionInfo = state.versionInfo,
                aboutInfo = state.aboutInfo,
            )
        }
    }
}

//region Sections

@Composable
private fun CreateGeneralSection(
    title: String,
    themeSettings: PickerModel<SharedAppThemeSettingsModel>,
    languageSettings: PickerModel<SharedAppLanguageSettingsModel>,
    onThemeValueClicked: () -> Unit,
    onLanguageValueClicked: () -> Unit,
) {

    SectionItem(
        title = title,
        content = {
            PickerItem(
                title = themeSettings.title,
                value = themeSettings.selectedValue.displayName,
                clickAction = onThemeValueClicked
            )

            SectionDivider()

            PickerItem(
                title = languageSettings.title,
                value = languageSettings.selectedValue.displayName,
                clickAction = onLanguageValueClicked
            )
        })
}

@Suppress("LongParameterList")
@Composable
private fun CreateNotificationsSection(
    title: String,
    soundSettings: PickerModel<ShardAppSoundSettingsModel>,
    fajrSettings: SwitchModel,
    sunriseSettings: SwitchModel,
    dhuhrSettings: SwitchModel,
    asrSettings: SwitchModel,
    maghribSettings: SwitchModel,
    ishaSettings: SwitchModel,
    onSoundValueClicked: () -> Unit,
    onFajrEnabledChanged: (Boolean) -> Unit,
    onSunriseEnabledChanged: (Boolean) -> Unit,
    onDhuhrEnabledChanged: (Boolean) -> Unit,
    onAsrEnabledChanged: (Boolean) -> Unit,
    onMaghribEnabledChanged: (Boolean) -> Unit,
    onIshaEnabledChanged: (Boolean) -> Unit,
) {
    SectionItem(
        title = title,
        content = {
            PickerItem(
                title = soundSettings.title,
                value = soundSettings.selectedValue.displayName,
                clickAction = onSoundValueClicked
            )

            SectionDivider()

            SwitchItem(
                title = fajrSettings.title,
                isChecked = fajrSettings.value,
                onChange = onFajrEnabledChanged
            )

            SectionDivider()

            SwitchItem(
                title = sunriseSettings.title,
                isChecked = sunriseSettings.value,
                onChange = onSunriseEnabledChanged
            )

            SectionDivider()

            SwitchItem(
                title = dhuhrSettings.title,
                isChecked = dhuhrSettings.value,
                onChange = onDhuhrEnabledChanged
            )

            SectionDivider()

            SwitchItem(
                title = asrSettings.title,
                isChecked = asrSettings.value,
                onChange = onAsrEnabledChanged
            )

            SectionDivider()

            SwitchItem(
                title = maghribSettings.title,
                isChecked = maghribSettings.value,
                onChange = onMaghribEnabledChanged
            )

            SectionDivider()

            SwitchItem(
                title = ishaSettings.title,
                isChecked = ishaSettings.value,
                onChange = onIshaEnabledChanged
            )
        }
    )
}

@Composable
fun CreateInfoSection(
    title: String,
    versionInfo: State.InfoModel,
    aboutInfo: State.InfoModel
) {
    SectionItem(
        title = title,
        content = {
            PickerItem(
                title = aboutInfo.title,
                value = aboutInfo.value,
                clickAction = {}
            )

            SectionDivider()

            PickerItem(
                title = versionInfo.title,
                value = versionInfo.value,
                clickAction = {}
            )
        })

}

//endregion

//region Section Items

@Composable
private fun SectionDivider() {
    Divider(
        color = MaterialTheme.colors.background,
        thickness = 1.dp,
    )
}

@Composable
private fun SectionItem(
    title: String,
    content: @Composable() () -> Unit
) {
    Column {
        Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            backgroundColor = MaterialTheme.colors.surface,
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                content()
            }
        }
    }

}

@Composable
private fun PickerItem(
    title: String,
    value: String,
    clickAction: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = clickAction)
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal
        )
        Text(text = value, fontSize = 16.sp, color = MaterialTheme.colors.primaryVariant)
    }
}

@Composable
private fun SwitchItem(
    title: String,
    isChecked: Boolean,
    onChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1.0f, true),
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal
        )
        Switch(checked = isChecked, onCheckedChange = onChange)
    }
}

//endregion

//region DialogPicker

@Composable
private fun DialogPicker(pickerState: State.DialogPickerState) {
    when (pickerState) {
        State.DialogPickerState.Hidden -> {
            // do nothing
        }
        is State.DialogPickerState.Shown -> {
            pickerState.request.apply {
                val radioButtonSelectedIndex = remember { mutableStateOf(selectedIndex) }
                AlertDialog(
                    modifier = Modifier.padding(horizontal = LocalConfiguration.current.dialogHorizontalMargin),
                    onDismissRequest = {},
                    confirmButton = {
                        TextButton(onClick = { onItemSelected(radioButtonSelectedIndex.value) }) {
                            Text(
                                positiveBtn,
                                style = MaterialTheme.typography.body1,
                                color = MaterialTheme.colors.onPrimary,
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { onDismiss() }) {
                            Text(
                                negativeBtn,
                                style = MaterialTheme.typography.body1,
                                color = MaterialTheme.colors.onPrimary,
                            )
                        }
                    },
                    title = { Text(text = title, style = MaterialTheme.typography.h6) },
                    text = { DialogPickerBody(pickerState.request, radioButtonSelectedIndex) },
                    properties = DialogProperties(
                        dismissOnClickOutside = true
                    )
                )
            }
        }
    }
}

@Composable
private fun DialogPickerBody(
    request: State.DialogPickerState.Shown.Request,
    selectedIndexMutableState: MutableState<Int>
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(request.items.size) { currentIndex ->
                val text: String = request.items[currentIndex]
                val selected: Boolean =
                    currentIndex == selectedIndexMutableState.value
                val onClick = { selectedIndexMutableState.value = currentIndex }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onClick)
                ) {
                    RadioButton(
                        selected = selected,
                        onClick = onClick,
                    )
                    Text(
                        text = text,
                        color = MaterialTheme.colors.onPrimary,
                        style = MaterialTheme.typography.body1
                    )
                }
            }
        }
    }
}

//endregion

//endregion

//region Helpers

private fun <T : SharedAppBaseSettingsModel> showDialogPicker(
    viewModel: SettingsViewModel,
    pickerModel: PickerModel<T>,
    action: (T) -> Unit,
) {
    viewModel.showDialogPicker(
        pickerModel = pickerModel,
        onItemSelected = { item ->
            pickerModel.allValues.getOrNull(item)?.also { value ->
                action(value)
                viewModel.hideDialogPicker()
            }
        },
        onDismiss = {
            viewModel.hideDialogPicker()
        },
    )
}

//endregion

@Suppress("UndocumentedPublicFunction", "NamedArguments", "LongMethod")
@Preview("SettingsScreen")
@Composable
fun PreviewSettingsScreen() {
//    SettingsContent(
//        State(
//
//        )
//    )
}
