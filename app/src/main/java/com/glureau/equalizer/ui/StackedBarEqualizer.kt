package com.glureau.equalizer.ui

import android.util.Log
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.glureau.equalizer.audio.VisualizerData
import kotlin.math.roundToInt


@Composable
fun StackedBarEqualizer(
    modifier: Modifier,
    data: VisualizerData,
    barCount: Int,
    maxStackCount: Int = 32
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    Row(modifier.onSizeChanged { size = it }) {
        val viewportWidth = size.width.toFloat()
        val viewportHeight = size.height.toFloat()
        val padding = LocalDensity.current.run { 1.dp.toPx() }

        val barWidth = (viewportWidth / barCount) - padding
        val stackHeightWithPadding = viewportHeight / maxStackCount
        val stackHeight = stackHeightWithPadding - padding

        val nodes = mutableListOf<PathNode>()
        data.resample(barCount).forEachIndexed { index, d ->
            //val barHeight by animateFloatAsState(targetValue = viewportHeight * (1 - (d / 128f)))
            val stackCount = (maxStackCount * (d / 128f)).roundToInt()
            for (stackIndex in 0..stackCount) {
                nodes += PathNode.MoveTo(
                    barWidth * index + padding * index,
                    viewportHeight - stackIndex * stackHeight - padding * stackIndex
                )
                nodes += PathNode.LineTo(
                    barWidth * (index + 1) + padding * index,
                    viewportHeight - stackIndex * stackHeight - padding * stackIndex
                )
                nodes += PathNode.LineTo(
                    barWidth * (index + 1) + padding * index,
                    viewportHeight - (stackIndex + 1) * stackHeight - padding * stackIndex
                )
                nodes += PathNode.LineTo(
                    barWidth * index + padding * index,
                    viewportHeight - (stackIndex + 1) * stackHeight - padding * stackIndex
                )
            }
            if (index == barCount) {
                Log.e("GREG", "c - stackCount=$stackCount")
            }
            if (index == barCount-1) {
                Log.e("GREG", "c-1 - stackCount=$stackCount")
            }
        }


        val vectorPainter = rememberVectorPainter(
            defaultWidth = viewportWidth.dp,
            defaultHeight = viewportHeight.dp,
            viewportWidth = viewportWidth,
            viewportHeight = viewportHeight,
        ) { vw, vh ->
            Path(
                fill = Brush.linearGradient(
                    listOf(Color.Red, Color.Yellow, Color.Green),
                    start = Offset.Zero, end = Offset(0f, Float.POSITIVE_INFINITY)
                ),
                pathData = nodes
            )
        }
        Image(
            painter = vectorPainter,
            contentDescription = null
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