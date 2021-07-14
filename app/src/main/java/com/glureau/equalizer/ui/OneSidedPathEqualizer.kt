package com.glureau.equalizer.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.graphics.vector.PathNode
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.glureau.equalizer.audio.VisualizerData


@Composable
fun OneSidedPathEqualizer(
    modifier: Modifier,
    data: VisualizerData,
    segmentCount: Int,
    fillBrush: Brush,
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    Row(modifier.onSizeChanged { size = it }) {
        val viewportWidth = size.width.toFloat()
        val viewportHeight = size.height.toFloat()

        val barWidth = viewportWidth / (segmentCount - 1)

        val nodes = mutableListOf<PathNode>()
        nodes.add(PathNode.MoveTo(0f, viewportHeight))

        data.resample(segmentCount).forEachIndexed { index, d ->
            val height by animateFloatAsState(targetValue = viewportHeight * (1 - (d / 128f)))
            nodes.add(PathNode.LineTo(barWidth * (index + 0), height))
        }
        nodes.add(PathNode.LineTo(viewportWidth, viewportHeight))

        val vectorPainter = rememberVectorPainter(
            defaultWidth = viewportWidth.dp,
            defaultHeight = viewportHeight.dp,
            viewportWidth = viewportWidth,
            viewportHeight = viewportHeight,
        ) { vw, vh ->
            Path(
                fill = fillBrush,
                pathData = nodes
            )
        }
        Image(
            painter = vectorPainter,
            contentDescription = null
        )
    }
}