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
fun DoubleSidedCircularPathEqualizer(
    modifier: Modifier,
    data: VisualizerData,
    segmentCount: Int,
    fillBrush: Brush,
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    Row(modifier.onSizeChanged { size = it }) {
        val viewportWidth = size.width.toFloat()
        val viewportHeight = size.height.toFloat()

        val resampled = data.resample(segmentCount)
        val pathData =
            computeDoubleSidedPoints(
                resampled,
                viewportWidth,
                viewportHeight,
                segmentCount,
                continuous = true
            )
                .map {
                    val height by animateFloatAsState(targetValue = it.y())
                    Point(it.x() to height)
                }
                .circularProj(viewportWidth, viewportHeight)
                .map<Point, PathNode> { p ->
                    PathNode.LineTo(p.x(), p.y())
                }
        val firstPoint = pathData.firstOrNull() as? PathNode.LineTo
        val firstPath = PathNode.MoveTo(firstPoint?.x ?: 0f, firstPoint?.y ?: 0f)
        val finalPathData = pathData.toMutableList()
        finalPathData.add(0, firstPath)

        val vectorPainter = rememberVectorPainter(
            defaultWidth = viewportWidth.dp,
            defaultHeight = viewportHeight.dp,
            viewportWidth = viewportWidth,
            viewportHeight = viewportHeight,
        ) { _, _ ->
            Path(
                fill = fillBrush,
                pathData = finalPathData
            )
        }
        Image(
            painter = vectorPainter,
            contentDescription = null
        )
    }
}