@file:Suppress("LongMethod", "UndocumentedPublicFunction")

package com.alifwyaa.azanmunich.android.ui.screens.splash

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alifwyaa.azanmunich.android.R
import com.alifwyaa.azanmunich.extensions.sharedApp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Preview("SplashScreen")
@Preview("SplashScreen (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewSplashScreen() {
    SplashContent("Azan Munich")
}

/**
 * SplashScreen
 */
@Composable
fun SplashScreen(navigateToHome: () -> Unit) {
    SplashContent(
        title = LocalContext.current.sharedApp.localizationService.strings.appName
    )
    LaunchedEffect(true) {
        launch(Dispatchers.Main) {
            delay(2000)
            navigateToHome()
        }
    }
}

@Composable
fun SplashContent(title: String) {
    val systemUiController = rememberSystemUiController()

    val useDarkIcons = MaterialTheme.colors.isLight

    val backgroundColor = MaterialTheme.colors.background

    SideEffect {
        // Update all of the system bar colors to be transparent, and use
        // dark icons if we're in light theme
        systemUiController.setSystemBarsColor(color = backgroundColor, darkIcons = useDarkIcons)
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        val size = 250

        Image(
            painter = painterResource(id = R.drawable.ic_azan_munich),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .width(size.dp)
                .height((size / 1.2).dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.body1,
            fontSize = 32.sp,
        )
    }
}
