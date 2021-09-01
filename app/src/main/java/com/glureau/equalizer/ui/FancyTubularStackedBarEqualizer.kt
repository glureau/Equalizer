package com.glureau.equalizer.ui

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
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

// Main inspiration: https://www.shutterstock.com/fr/video/clip-771901-sound-graphic-equalizer
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
            val rotation by rememberInfiniteTransition().animateFloat(
                initialValue = 0f,
                targetValue = 2f * Math.PI.toFloat(),
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 40000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
            val ringCount = 6

            val moveRatio by rememberInfiniteTransition().animateFloat(
                initialValue = 0f,
                targetValue = 1f / ringCount,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 5000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )

            val rings = (0..ringCount).map { index ->
                buildRing(
                    stackedBar,
                    index, ringCount,
                    moveRatio, rotation,
                    speedPowFactor = 10f, surfaceFactor = 0.5f, stretchPow = stretchPow,
                    secondSurfaceFactor = 1.4f,
                    viewportWidth = viewportWidth,
                    viewportHeight = viewportHeight,
                )
            }

            val vectorPainter = rememberVectorPainter(
                defaultWidth = viewportWidth.dp,
                defaultHeight = viewportHeight.dp,
                viewportWidth = viewportWidth,
                viewportHeight = viewportHeight,
            ) { vw, vh ->
                rings.forEach { it() }
            }
            Image(
                painter = vectorPainter,
                contentDescription = null,
                modifier = Modifier.background(
                    Brush.radialGradient(
                        listOf(
                            Color(0xffffffff),
                            //Color(0xff9575cd),
                            Color(0xff3C37CA),
                            Color(0xff000000),
                        ),
                        radius = min(viewportWidth, viewportHeight) / 7f
                    )
                )
            )
        }
    }
}

fun Float.curveSpaceAndTime(speedPowFactor: Float) = 0.15f + 0.85f * pow(speedPowFactor)

@Composable
fun buildRing(
    stackedBar: List<Point>,
    ringIndex: Int, ringCount: Int,
    moveRatio: Float, rotation: Float,
    speedPowFactor: Float, surfaceFactor: Float, stretchPow: Float, secondSurfaceFactor: Float,
    viewportWidth: Float, viewportHeight: Float
): @Composable () -> Unit {
    val longestRadiusFactor = sqrt(2f)// Cause ratio is 1:1, diag is sqrt(2)
    val startFactor = ringIndex.toFloat() / ringCount
    val surfaceRatio = surfaceFactor / ringCount

    val startRadius =
        (moveRatio + startFactor).curveSpaceAndTime(speedPowFactor) * longestRadiusFactor
    val endRadius =
        (moveRatio + startFactor + surfaceRatio).curveSpaceAndTime(speedPowFactor) * longestRadiusFactor
    val endRadiusBackground =
        (moveRatio + startFactor + surfaceRatio * secondSurfaceFactor).curveSpaceAndTime(speedPowFactor) * longestRadiusFactor

    val circle = stackedBar.circularProj(
        viewportWidth = viewportWidth,
        viewportHeight = viewportHeight,
        innerRadiusRatio = startRadius,
        outerRadiusRatio = endRadius,
        stretchPow = stretchPow,
        angleOffset = rotation,
    ).stackToNodes()

    val circleBackground = stackedBar.circularProj(
        viewportWidth = viewportWidth,
        viewportHeight = viewportHeight,
        innerRadiusRatio = startRadius,
        outerRadiusRatio = endRadiusBackground,
        stretchPow = stretchPow,
        angleOffset = rotation,
    ).stackToNodes()
    return {
        Path(
            fill = Brush.radialGradient(
                endRadius to Color(0x709575cd),
                endRadiusBackground to Color(0x109575cd),
            ),
            pathData = circleBackground
        )

        Path(
            fill = Brush.radialGradient(
                startRadius to Color(0xffb3e5fc),
                startRadius + (endRadius - startRadius) / 2f to Color(0xffffffff),
                endRadius to Color(0xff9575cd),
            ),
            fillAlpha = if (ringIndex == 0) (moveRatio * 3).coerceAtMost(1f) else 1f,
            pathData = circle
        )
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