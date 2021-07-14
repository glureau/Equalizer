package com.glureau.equalizer.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.graphics.vector.PathNode
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.glureau.equalizer.audio.VisualizerData


@Composable
fun DoubleSidedPathEqualizer(
    modifier: Modifier,
    data: VisualizerData,
    segmentCount: Int,
    fillBrush: Brush,
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    Row(modifier.onSizeChanged { size = it }) {
        val viewportWidth = size.width.toFloat()
        val viewportHeight = size.height.toFloat()

        val barWidth = viewportWidth / (segmentCount - 2)

        val top = mutableListOf<PathNode>()
        val bottom = mutableListOf<PathNode>()

        val resampled = data.resample(segmentCount)
        resampled.forEachIndexed { index, d ->
            val isTop = index % 2 == 0
            if (isTop) {
                val targetValue = viewportHeight * (0.5f - (d / 256f))
                val height by animateFloatAsState(targetValue = targetValue)
                top.add(PathNode.LineTo(barWidth * (index + 0), height))
            } else {
                val targetValue = viewportHeight * (0.5f + (d / 256f))
                val height by animateFloatAsState(targetValue = targetValue)
                bottom.add(PathNode.LineTo(barWidth * (index - 1), height))
            }
        }

        val vectorPainter = rememberVectorPainter(
            defaultWidth = viewportWidth.dp,
            defaultHeight = viewportHeight.dp,
            viewportWidth = viewportWidth,
            viewportHeight = viewportHeight,
        ) { vw, vh ->
            Path(
                fill = fillBrush,
                pathData = top + bottom.reversed()
            )
        }
        Image(
            painter = vectorPainter,
            contentDescription = null
        )
    }
}