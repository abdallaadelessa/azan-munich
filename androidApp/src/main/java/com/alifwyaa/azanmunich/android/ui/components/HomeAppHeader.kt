package com.alifwyaa.azanmunich.android.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Suppress("UndocumentedPublicFunction")
@Preview("HomeAppHeader")
@Composable
fun PreviewHomeAppHeader() {
    val appColors = MaterialTheme.colors
    HomeAppHeader(
        title = "Azan App",
        menuItemTitle = "Azan App",
        openSettings = {},
        statusBarColor = appColors.primaryVariant,
        backgroundColor = appColors.primary,
        contentColor = appColors.onPrimary
    )
}

/**
 * HomeAppHeader
 */
@Composable
fun HomeAppHeader(
    modifier: Modifier = Modifier,
    title: String,
    menuItemTitle: String,
    openSettings: () -> Unit,
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
            title = { TitleView(title) },
            actions = { OptionMenuView(menuItemTitle, openSettings) },
            modifier = modifier
        )
    }
}

@Composable
private fun TitleView(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = title,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun OptionMenuView(
    menuItemTitle: String,
    openSettings: () -> Unit
) {
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        IconButton(
            onClick = openSettings
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = menuItemTitle,
                tint = MaterialTheme.colors.onPrimary
            )
        }
    }
}
