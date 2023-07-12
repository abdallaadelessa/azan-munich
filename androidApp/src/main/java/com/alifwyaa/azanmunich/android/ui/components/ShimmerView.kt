package com.alifwyaa.azanmunich.android.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode

/**
 * @author Created by Abdullah Essa on 08.08.21.
 *
 * Shimmer View
 */
@Composable
fun ShimmerView(
    modifier: Modifier,
    boxModifier: Modifier = Modifier,
    colorShades: Array<Pair<Float, Color>> = arrayOf(
        0f to MaterialTheme.colors.primary.copy(0.6f),
        0.3f to MaterialTheme.colors.primaryVariant.copy(0.6f),
        0.7f to MaterialTheme.colors.background.copy(0.6f),
        1f to MaterialTheme.colors.primary.copy(0.6f),
    )
) {

    /*
    Create InfiniteTransition
    which holds child animation like [Transition]
    animations start running as soon as they enter
    the composition and do not stop unless they are removed
    */
    val transition = rememberInfiniteTransition()
    val translateAnim by transition.animateFloat(
        /*
        Specify animation positions,
        initial Values 0F means it starts from 0 position
        */
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(

            /*
             Tween Animates between values over specified [durationMillis]
            */
            tween(durationMillis = 900, easing = LinearEasing),
            RepeatMode.Restart
        )
    )

    /*
      Create a gradient using the list of colors
      Use Linear Gradient for animating in any direction according to requirement
      start=specifies the position to start with in cartesian like system Offset(10f,10f) means x(10,0) , y(0,10)
      end= Animate the end position to give the shimmer effect using the transition created above
    */
    val brush = Brush.horizontalGradient(
        colorStops = colorShades,
        startX = 0f,
        endX = translateAnim,
        tileMode = TileMode.Clamp
    )

    /*
         Column composable shaped like a rectangle,
         set the [background]'s [brush] with the
         brush receiving from [ShimmerAnimation]
         which will get animated.
         Add few more Composable to test
       */
    Box(modifier = boxModifier) {
        Spacer(modifier = modifier.background(brush = brush))
    }
}
