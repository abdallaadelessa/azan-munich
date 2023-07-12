@file:Suppress("OptionalUnit", "LongMethod","UndocumentedPublicFunction")

package com.alifwyaa.azanmunich.android.ui.screens.home


import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alifwyaa.azanmunich.android.R
import com.alifwyaa.azanmunich.android.extensions.screenHorizontalMargin
import com.alifwyaa.azanmunich.android.extensions.toUnit
import com.alifwyaa.azanmunich.android.ui.components.ShimmerView
import com.alifwyaa.azanmunich.android.ui.screens.common.DataPlaceHolder
import com.alifwyaa.azanmunich.android.ui.screens.home.HomeViewModel.State
import com.alifwyaa.azanmunich.android.ui.screens.home.HomeViewModel.State.Azan
import com.alifwyaa.azanmunich.android.ui.screens.home.HomeViewModel.State.AzanDate
import com.alifwyaa.azanmunich.android.ui.screens.home.HomeViewModel.State.NextAzan
import com.alifwyaa.azanmunich.android.ui.theme.appColors
import com.alifwyaa.azanmunich.android.ui.theme.isDarkTheme
import com.alifwyaa.azanmunich.data.internal.model.SharedAzanType
import com.alifwyaa.azanmunich.domain.model.SharedAzanModel
import com.alifwyaa.azanmunich.domain.model.SharedDateModel
import com.alifwyaa.azanmunich.domain.model.SharedTimeModel

//region Screen

/**
 * HomeScreen
 */
@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val state by viewModel.stateFlow.collectAsState(viewModel.initialState)
    HomeContent(
        state = state,
        goToDayBefore = viewModel::goToDayBefore,
        goToDayAfter = viewModel::goToDayAfter,
        goToNextAzanDay = viewModel::goToNextAzanDay,
        retry = viewModel::reloadAzanList
    )
}

@Composable
fun HomeContent(
    state: State,
    goToDayBefore: () -> Unit = {},
    goToDayAfter: () -> Unit = {},
    goToNextAzanDay: () -> Unit = {},
    retry: () -> Unit = {},
) {
    val scrollableListState = rememberLazyListState()

    LazyColumn(
        state = scrollableListState,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(
            horizontal = LocalConfiguration.current.screenHorizontalMargin,
            vertical = 16.dp
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        item {
            CreatePageHeader(
                nextAzan = state.nextAzan,
                goToNextAzanDay = goToNextAzanDay
            )
        }

        item {
            CreateDateNavigator(
                azanDate = state.azanDate,
                goToDayBefore = goToDayBefore,
                goToDayAfter = goToDayAfter
            )
        }

        item {
            CreatePageItems(
                scrollableListState = scrollableListState,
                azanList = state.azanList,
                retry = retry
            )
        }
    }.toUnit()
}

@Composable
private fun CreatePageHeader(
    nextAzan: DataPlaceHolder<NextAzan>,
    goToNextAzanDay: () -> Unit
): Unit =
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { goToNextAzanDay() }
    ) {
        when (nextAzan) {
            is DataPlaceHolder.Error,
            is DataPlaceHolder.Loading -> {
                val height: Dp = 10.dp
                val margins: Dp = 11.dp
                val bigMargins: Dp = 19.dp // 38 dp
                val shape = RoundedCornerShape(16.dp)
                ShimmerView(
                    modifier = Modifier
                        .clip(shape)
                        .height(height)
                        .width(90.dp),
                    boxModifier = Modifier.padding(vertical = margins)
                )
                ShimmerView(
                    modifier = Modifier
                        .clip(shape)
                        .height(height)
                        .width(200.dp),
                    boxModifier = Modifier.padding(vertical = bigMargins)
                )
                ShimmerView(
                    modifier = Modifier
                        .clip(shape)
                        .height(height)
                        .width(90.dp),
                    boxModifier = Modifier.padding(vertical = margins)
                )
            }
            is DataPlaceHolder.Success -> {
                nextAzan.data.apply {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_azan_munich),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            colorFilter = ColorFilter.tint(MaterialTheme.colors.onPrimary),
                            modifier = Modifier.height(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.subtitle1,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.align(Alignment.Bottom)
                        )
                    }
                    AutoSizeText(
                        text = text,
                        textStyle = TextStyle(fontSize = 36.sp),
                        modifier = Modifier.height(48.dp)
                    )
                    AutoSizeText(
                        text = periodText,
                        textStyle = TextStyle(fontSize = 18.sp),
                        modifier = Modifier.height(32.dp)
                    )
                }
            }
        }
    }

@Composable
private fun CreateDateNavigator(
    azanDate: AzanDate,
    goToDayBefore: () -> Unit,
    goToDayAfter: () -> Unit
): Unit = AzanRowCard(innerHorizontalPadding = 8.dp, verticalPadding = 8.dp) {
    IconButton(
        onClick = goToDayBefore
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_arrow_left),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onPrimary),
            modifier = Modifier
                .width(35.dp)
                .height(35.dp)
        )
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.weight(1.0f, true)
    ) {
        AutoSizeText(
            text = azanDate.westernFormattedDate,
            textStyle = TextStyle(fontSize = 24.sp)
        )
        AutoSizeText(
            text = azanDate.islamicFormattedDate,
            textStyle = TextStyle(fontSize = 16.sp)
        )
    }
    IconButton(
        onClick = goToDayAfter
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onPrimary),
            modifier = Modifier
                .width(35.dp)
                .height(35.dp)
        )
    }
}.toUnit()

@Composable
private fun CreatePageItems(
    scrollableListState: LazyListState,
    azanList: DataPlaceHolder<List<Azan>>,
    retry: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        when (azanList) {
            is DataPlaceHolder.Error -> {
                CreatePageLoadingItems()
                ShowError(
                    errorMessage = azanList.message,
                    retryText = azanList.actionText,
                    retry = retry
                )
            }
            is DataPlaceHolder.Loading -> {
                CreatePageLoadingItems()
            }
            is DataPlaceHolder.Success -> {
                CreatePageSuccessItems(azanList)
            }
        }.toUnit()
    }

    if (azanList is DataPlaceHolder.Error) {
        LaunchedEffect(true) {
            scrollableListState.animateScrollToItem(scrollableListState.layoutInfo.totalItemsCount - 1)
        }
    }
}

@Composable
private fun CreatePageLoadingItems() {
    val width: Dp = 70.dp
    val height: Dp = 20.dp // 40 dp
    val margins = 10.dp
    val shape = RoundedCornerShape(16.dp)
    repeat(6) {
        AzanRowCard(
            innerHorizontalPadding = 16.dp,
            verticalPadding = 4.dp,
        ) {
            ShimmerView(
                modifier = Modifier
                    .height(height)
                    .width(height)
                    .clip(shape),
                boxModifier = Modifier.padding(
                    start = 8.dp,
                    end = 16.dp,
                    top = margins,
                    bottom = margins,
                )
            )
            ShimmerView(
                modifier = Modifier
                    .clip(shape)
                    .height(height)
                    .width(width)
            )
            Spacer(modifier = Modifier.weight(1.0f, true))
            ShimmerView(
                modifier = Modifier
                    .clip(shape)
                    .height(height)
                    .width(width)
            )
        }
    }
}

@Composable
private fun CreatePageSuccessItems(
    azanList: DataPlaceHolder.Success<List<Azan>>
) {
    azanList.data.forEach { item: Azan ->
        AzanRowCard(
            innerHorizontalPadding = 16.dp,
            verticalPadding = 4.dp,
            isSelected = item.isNextAzan
        ) {
            Box(
                Modifier.padding(
                    start = 3.dp,
                    end = 11.dp,
                    top = 2.dp,
                    bottom = 2.dp
                )
            ) {
                val painter = painterResource(
                    id = if (item.isNotificationEnabled) {
                        R.drawable.ic_notification_on
                    } else {
                        R.drawable.ic_notification_off
                    }
                )
                Image(
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onPrimary),
                    modifier = Modifier
                        .width(36.dp)
                        .height(36.dp)
                )
            }
            Text(
                text = item.model.displayName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1.0f, true),
                fontWeight = FontWeight.Normal,
                fontSize = 22.sp
            )
            Text(
                text = item.model.displayTime,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Normal,
                fontSize = 22.sp
            )
        }
    }
}

@Composable
private fun ShowError(
    errorMessage: String,
    retryText: String,
    retry: () -> Unit
) {
    Snackbar(
        action = { Button(onClick = retry) { Text(retryText) } },
        modifier = Modifier.padding(8.dp)
    ) { Text(text = errorMessage) }
}

//endregion

//region Helpers

@Composable
fun AutoSizeText(
    text: String,
    textStyle: TextStyle,
    modifier: Modifier = Modifier
) {
    var scaledTextStyle by remember { mutableStateOf(textStyle) }
    var readyToDraw by remember { mutableStateOf(false) }

    Text(
        text,
        modifier.drawWithContent {
            if (readyToDraw) {
                drawContent()
            }
        },
        textAlign = TextAlign.Center,
        style = scaledTextStyle,
        softWrap = false,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.didOverflowWidth) {
                scaledTextStyle =
                    scaledTextStyle.copy(fontSize = scaledTextStyle.fontSize * 0.9)
            } else {
                readyToDraw = true
            }
        }
    )
}

@Composable
private fun AzanRowCard(
    innerHorizontalPadding: Dp,
    verticalPadding: Dp,
    isSelected: Boolean = false,
    content: @Composable() (RowScope.() -> Unit)
): Unit =
    Card(
        shape = RoundedCornerShape(50.dp),
        border = BorderStroke(1.dp, appColors(isDarkTheme()).surfaceBorder),
        backgroundColor = if (isSelected) {
            appColors(isDarkTheme()).highlight
        } else {
            MaterialTheme.colors.surface
        },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = verticalPadding, horizontal = innerHorizontalPadding),
        ) {
            this.content()
        }

    }.toUnit()

//endregion

@Suppress("UndocumentedPublicFunction", "NamedArguments", "LongMethod")
@Preview("HomeScreen")
@Preview("HomeScreen (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewHomeScreen() {
    val azanList = DataPlaceHolder.Success(
        listOf(
            SharedAzanModel(
                "",
                SharedAzanType.FAJR,
                "Name",
                "Time",
                SharedDateModel(1, 2, 2021),
                SharedTimeModel(1, 1),
            ),
            SharedAzanModel(
                "",
                SharedAzanType.SUNRISE,
                "Name",
                "Time",
                SharedDateModel(1, 2, 2021),
                SharedTimeModel(1, 1),
            ),
            SharedAzanModel(
                "",
                SharedAzanType.DHUHR,
                "Name",
                "Time",
                SharedDateModel(1, 2, 2021),
                SharedTimeModel(1, 1),
            ),
            SharedAzanModel(
                "",
                SharedAzanType.ASR,
                "Name",
                "Time",
                SharedDateModel(1, 2, 2021),
                SharedTimeModel(1, 1),
            ),
            SharedAzanModel(
                "",
                SharedAzanType.MAGHRIB,
                "Name",
                "Time",
                SharedDateModel(1, 2, 2021),
                SharedTimeModel(1, 1),
            ),
            SharedAzanModel(
                "",
                SharedAzanType.ISHA,
                "Name",
                "Time",
                SharedDateModel(1, 2, 2021),
                SharedTimeModel(1, 1),
            ),

            ).map {
            Azan(model = it, isNextAzan = false, isNotificationEnabled = false)
        }
    )
    HomeContent(
        State(
            nextAzan = DataPlaceHolder.Success(
                NextAzan(
                    id = "",
                    title = "Next Prayer",
                    text = "Fajr: 03:30",
                    periodText = "1 hours, 2 minutes and 3 seconds",
                    dateModel = SharedDateModel(1, 1, 1),
                )
            ),
            azanDate = AzanDate(
                SharedDateModel(1, 1, 2021),
                "Fr., 30 Juli 2021",
                "20 Dhu I-Hiddscha 1442",
            ),
            azanList = azanList,
        )
    )
}
