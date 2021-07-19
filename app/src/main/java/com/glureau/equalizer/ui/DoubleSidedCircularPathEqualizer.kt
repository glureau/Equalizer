package com.glureau.equalizer.ui

import android.util.Log
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
        if (resampled.isNotEmpty()) {
            val circularProj = computeDoubleSidedPoints(
                resampled,
                viewportWidth,
                viewportHeight,
                segmentCount,
                continuous = true // false if cubic, true if lineto
            ).map {
                val height by animateFloatAsState(targetValue = it.y())
                Point(it.x() to height)
            }
                .circularProj(viewportWidth, viewportHeight)

            /*val pathData = circularProj
            .map<Point, PathNode> { p ->
                PathNode.LineTo(p.x(), p.y())
            }*/
            val pathData = mutableListOf<PathNode>()
            val count = circularProj.size
            val halfCount = count / 2
            val offset = halfCount - 1
            Log.e("DEV", "TOP=" + circularProj.subList(0, halfCount).joinToString())
            Log.e("DEV", "BOTTOM=" + circularProj.subList(halfCount, count).joinToString())
            pathData += PathNode.MoveTo(circularProj[offset].x(), circularProj[offset].y())
            for (i in 0..halfCount-1) {
                val prevprev = circularProj[(i - 2 + halfCount) % halfCount]
                val prev = circularProj[(i - 1 + halfCount) % halfCount]
                val current = circularProj[i]
                val next = circularProj[(i + 1) % halfCount]
                val tangentPrev = current - prevprev
                val tangentCurrent = prev - next
                val control1 = prev + tangentPrev * 0.2f
                val control2 = current + tangentCurrent * 0.2f
                Log.e("DEV", "$i - prevprev=$prevprev prev=$prev current=$current next=$next")
                pathData += PathNode.CurveTo(
                    control1.x(), control1.y(),
                    control2.x(), control2.y(),
                    current.x(), current.y()
                )
            }

            pathData += PathNode.LineTo(circularProj[count-1].x(), circularProj[count-1].y())
            for (i in 0..halfCount-1) {
                val prevprev = circularProj[((i - 2 + halfCount) % halfCount) + halfCount]
                val prev = circularProj[((i - 1 + halfCount) % halfCount) + halfCount]
                val current = circularProj[i + halfCount]
                val next = circularProj[((i + 1) % halfCount) + halfCount]
                val tangentPrev = current - prevprev
                val tangentCurrent = prev - next
                val control1 = prev + tangentPrev * 0.2f
                val control2 = current + tangentCurrent * 0.2f
                Log.e("DEV", "$i - prevprev=$prevprev prev=$prev current=$current next=$next")
                pathData += PathNode.CurveTo(
                    control1.x(), control1.y(),
                    control2.x(), control2.y(),
                    current.x(), current.y()
                )
            }
            Log.e("DEV", pathData.joinToString("\n"))

            val firstPoint = circularProj.first()
            val firstPath = PathNode.MoveTo(firstPoint.x(), firstPoint.y())
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
                    //strokeLineWidth = 10f,
                    pathData = finalPathData
                )
            }
            Image(
                painter = vectorPainter,
                contentDescription = null
            )
        }
    }
}