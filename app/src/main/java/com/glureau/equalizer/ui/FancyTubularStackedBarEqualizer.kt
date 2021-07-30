package com.glureau.equalizer.ui

import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.glureau.equalizer.audio.VisualizerData
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt


@Composable
fun FancyTubularStackedBarEqualizer(
    modifier: Modifier,
    data: VisualizerData,
    barCount: Int,
    maxStackCount: Int = 32
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    Row(
        modifier
            .clip(CircleShape)
            .border(4.dp, Color.Gray, CircleShape)
            .onSizeChanged { size = it }) {
        val viewportWidth = size.width.toFloat()
        val viewportHeight = size.height.toFloat()
        if (viewportWidth > 0 && viewportHeight > 0) {
            val horizontalPadding = LocalDensity.current.run { 2.dp.toPx() }
            val verticalPadding = LocalDensity.current.run { 4.dp.toPx() }

            val stackedBar = computeStackedBarPoints(
                resampled = data.resample(barCount),
                viewportWidth = viewportWidth,
                viewportHeight = viewportHeight,
                barCount = barCount,
                maxStackCount = maxStackCount,
                horizontalPadding = horizontalPadding,
                verticalPadding = verticalPadding,
            )

            val stretchPow = 1.8f // tunnel effect
            val moveRatio by rememberInfiniteTransition().animateFloat(
                initialValue = 0f,
                targetValue = 1f / 3f, // Because loop is computed for 3 paths
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
            val rotation by rememberInfiniteTransition().animateFloat(
                initialValue = 0f,
                targetValue = 2f * Math.PI.toFloat(),
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 40000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )

            val longestRadiusFactor = sqrt(2f)// Cause ratio is 1:1, diag is sqrt(2)
            val startFactor1 = 0
            val startFactor2 = 1f / 3f
            val startFactor3 = 2f / 3f
            val surfaceRatio = startFactor2 / 3f // < 1/3f
            val endFactor1 = startFactor1 + surfaceRatio
            val endFactor2 = startFactor2 + surfaceRatio
            val endFactor3 = startFactor3 + surfaceRatio
            val speedPowFactor = 5f

            fun Float.curveSpaceAndTime() = 0.15f + 0.85f * pow(speedPowFactor)

            val startRadius1 = (moveRatio + startFactor1).curveSpaceAndTime() * longestRadiusFactor
            val endRadius1 = (moveRatio + endFactor1).curveSpaceAndTime() * longestRadiusFactor
            val endRadius1Background = (moveRatio + endFactor1 + 0.1f).curveSpaceAndTime() * longestRadiusFactor
            val startRadius2 = (moveRatio + startFactor2).curveSpaceAndTime() * longestRadiusFactor
            val endRadius2 = (moveRatio + endFactor2).curveSpaceAndTime() * longestRadiusFactor
            val endRadius2Background = (moveRatio + endFactor2 + 0.1f).curveSpaceAndTime() * longestRadiusFactor
            val startRadius3 = (moveRatio + startFactor3).curveSpaceAndTime() * longestRadiusFactor
            val endRadius3 = (moveRatio + endFactor3).curveSpaceAndTime() * longestRadiusFactor
            val endRadius3Background = (moveRatio + endFactor3 + 0.1f).curveSpaceAndTime() * longestRadiusFactor

            Log.e(
                "GREG",
                "startRadius1=$startRadius1 endRadius1=$endRadius1, startRadius2=$startRadius2 endRadius2=$endRadius2, startRadius3=$startRadius3 endRadius3=$endRadius3"
            )

            val circle1 = stackedBar.circularProj(
                viewportWidth = viewportWidth,
                viewportHeight = viewportHeight,
                innerRadiusRatio = startRadius1,
                outerRadiusRatio = endRadius1,
                stretchPow = stretchPow,
                angleOffset = rotation,
            ).stackToNodes()
            val circle2 = stackedBar.circularProj(
                viewportWidth = viewportWidth,
                viewportHeight = viewportHeight,
                innerRadiusRatio = startRadius2,
                outerRadiusRatio = endRadius2,
                stretchPow = stretchPow,
                angleOffset = rotation,
            ).stackToNodes()
            val circle3 = stackedBar.circularProj(
                viewportWidth = viewportWidth,
                viewportHeight = viewportHeight,
                innerRadiusRatio = startRadius3,
                outerRadiusRatio = endRadius3,
                stretchPow = stretchPow,
                angleOffset = rotation,
            ).stackToNodes()
            val circle1Background = stackedBar.circularProj(
                viewportWidth = viewportWidth,
                viewportHeight = viewportHeight,
                innerRadiusRatio = startRadius1,
                outerRadiusRatio = endRadius1Background,
                stretchPow = stretchPow,
                angleOffset = rotation,
            ).stackToNodes()
            val circle2Background = stackedBar.circularProj(
                viewportWidth = viewportWidth,
                viewportHeight = viewportHeight,
                innerRadiusRatio = startRadius2,
                outerRadiusRatio = endRadius2Background,
                stretchPow = stretchPow,
                angleOffset = rotation,
            ).stackToNodes()
            val circle3Background = stackedBar.circularProj(
                viewportWidth = viewportWidth,
                viewportHeight = viewportHeight,
                innerRadiusRatio = startRadius3,
                outerRadiusRatio = endRadius3Background,
                stretchPow = stretchPow,
                angleOffset = rotation,
            ).stackToNodes()


            val vectorPainter = rememberVectorPainter(
                defaultWidth = viewportWidth.dp,
                defaultHeight = viewportHeight.dp,
                viewportWidth = viewportWidth,
                viewportHeight = viewportHeight,
            ) { vw, vh ->
                Path(
                    fill = Brush.radialGradient(listOf(Color(0xff9575cd), Color(0x409575cd))),
                    pathData = circle1Background
                )

                Path(
                    fill = Brush.radialGradient(
                        startRadius1 to Color(0xffb3e5fc),
                        startRadius1 + (endRadius1 - startRadius1) / 2f to Color(0xffffffff),
                        endRadius1 to Color(0xff9575cd),
                    ),
                    fillAlpha = (moveRatio * 3).coerceAtMost(1f),
                    pathData = circle1
                )

                Path(
                    fill = Brush.radialGradient(listOf(Color(0xff9575cd), Color(0x409575cd))),
                    pathData = circle2Background
                )


                Path(
                    fill = Brush.radialGradient(
                        startRadius2 to Color(0xffb3e5fc),
                        startRadius2 + (endRadius2 - startRadius2) / 2f to Color(0xffffffff),
                        endRadius2 to Color(0xff9575cd),
                    ),
                    pathData = circle2
                )

                Path(
                    fill = Brush.radialGradient(listOf(Color(0xff9575cd), Color(0x409575cd))),
                    pathData = circle3Background
                )

                Path(
                    fill = Brush.radialGradient(
                        startRadius3 to Color(0xffb3e5fc),
                        startRadius3 + (endRadius3 - startRadius3) / 2f to Color(0xffffffff),
                        endRadius3 to Color(0xff9575cd),
                        radius = max(viewportWidth / 2f, viewportHeight / 2f)
                    ),
                    pathData = circle3
                )
            }
            Image(
                painter = vectorPainter,
                contentDescription = null,
                modifier = Modifier.background(
                    Brush.radialGradient(
                        listOf(
                            Color(0xffffffff),
                            Color(0xff9575cd),
                            Color(0xff000000),
                        ),
                        radius = min(viewportWidth, viewportHeight) / 5f
                    )
                )
            )
        }
    }
}

/*
@Preview
@Composable
fun EqualizerPreview() {
    val data = IntArray(32) { ((sin(it.toDouble())+1) * 128).toInt() }
    BarEqualizer(
        Modifier
            .fillMaxSize()
            .background(Color.Magenta),
        VisualizerData(data)
    )
}*/