package com.glureau.equalizer.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.graphics.vector.PathNode
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.glureau.equalizer.audio.VisualizerData
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
    Row(modifier.onSizeChanged { size = it }) {
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
            val speed = 1.2f
            val magic = sqrt(2f)// Cause ratio is 1:1, diag is sqrt(2)
            val move = magic - 1f
            val moveRatio by rememberInfiniteTransition().animateFloat(
                initialValue = 0f,
                targetValue = move,
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
            val dist = 0.2f
            val full = 0.41f
            val startRadius1 = moveRatio + (magic - 2 * move - full)
            val endRadius1 = moveRatio + (magic - 2 * move - dist)
            val startRadius2 = moveRatio + (magic - move - full)
            val endRadius2 = moveRatio + (magic - move - dist)
            val startRadius3 = moveRatio + (magic - full)
            val endRadius3 = moveRatio + (magic - dist)

            val circle1 = stackedBar.circularProj(
                viewportWidth = viewportWidth,
                viewportHeight = viewportHeight,
                innerRadiusRatio = startRadius1.pow(speed),
                outerRadiusRatio = endRadius1.pow(speed),
                stretchPow = stretchPow,
                angleOffset = rotation,
            ).stackToNodes()
            val circle2 = stackedBar.circularProj(
                viewportWidth = viewportWidth,
                viewportHeight = viewportHeight,
                innerRadiusRatio = startRadius2.pow(speed),
                outerRadiusRatio = endRadius2.pow(speed),
                stretchPow = stretchPow,
                angleOffset = rotation,
            ).stackToNodes()
            val circle3 = stackedBar.circularProj(
                viewportWidth = viewportWidth,
                viewportHeight = viewportHeight,
                innerRadiusRatio = startRadius3.pow(speed),
                outerRadiusRatio = endRadius3.pow(speed),
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
                    fill = Brush.radialGradient(
                        listOf(
                            Color(0xffb3e5fc),
                            Color(0xffffffff),
                            Color(0xff9575cd),
                        ),
                        radius = Float.POSITIVE_INFINITY,
                    ),
                    fillAlpha = (moveRatio * 3).coerceAtMost(1f),
                    pathData = circle1
                )

                Path(
                    fill = Brush.radialGradient(
                        listOf(
                            Color(0xffb3e5fc),
                            Color(0xffffffff),
                            Color(0xff9575cd),
                        ),
                        radius = Float.POSITIVE_INFINITY,
                    ),
                    pathData = circle2
                )

                Path(
                    fill = Brush.radialGradient(
                        listOf(
                            Color(0xffb3e5fc),
                            Color(0xffffffff),
                            Color(0xff9575cd),
                        ),
                        radius = Float.POSITIVE_INFINITY,
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
                            Color(0x00000000),
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