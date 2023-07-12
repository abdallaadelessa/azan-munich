package com.alifwyaa.azanmunich.android.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Suppress("UndocumentedPublicFunction")
@Preview("SettingsAppHeader")
@Composable
fun PreviewSettingsAppHeader() {
    val appColors = MaterialTheme.colors
    SettingsAppHeader(
        title = "",
        previousScreenTitle = "",
        onBack = {},
        statusBarColor = appColors.primary,
        backgroundColor = appColors.primary,
        contentColor = appColors.onPrimary
    )
}

/**
 * SettingsAppHeader
 */
@Composable
fun SettingsAppHeader(
    modifier: Modifier = Modifier,
    title: String,
    previousScreenTitle: String,
    onBack: () -> Unit,
    statusBarColor: Color = MaterialTheme.colors.primary,
    backgroundColor: Color = MaterialTheme.colors.primary,
    contentColor: Color = MaterialTheme.colors.onPrimary,
) {

    val systemUiController = rememberSystemUiController()

    val useDarkIcons = MaterialTheme.colors.isLight

    SideEffect {
        // Update all of the system bar colors to be transparent, and use
        // dark icons if we're in light theme
        systemUiController.setSystemBarsColor(color = statusBarColor, darkIcons = useDarkIcons)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        TopAppBar(
            elevation = 2.dp,
            contentColor = contentColor,
            backgroundColor = backgroundColor,
            title = { TitleView(title, previousScreenTitle, onBack) },
            modifier = modifier
        )
    }
}

@Composable
private fun TitleView(
    title: String,
    previousScreenTitle: String,
    onBack: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack) {
            Icon(
                painter = rememberVectorPainter(image = Icons.Filled.ArrowBack),
                contentDescription = previousScreenTitle,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .heightIn(max = 24.dp)
            )
        }
        Text(
            text = title,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
