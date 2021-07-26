package com.glureau.equalizer.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.glureau.equalizer.audio.VisualizerData
import com.glureau.equalizer.ui.ext.getHeightDp
import com.glureau.equalizer.ui.ext.getWidthDp


@Composable
fun FullBarEqualizer(
    modifier: Modifier,
    barModifier: (Int, Modifier) -> Modifier,
    data: VisualizerData,
    barCount: Int
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    Row(modifier.onSizeChanged { size = it }) {
        val widthDp = size.getWidthDp()
        val heightDp = size.getHeightDp()
        val padding = 1.dp
        val barWidthDp = widthDp / (barCount +2)

        data.resample(barCount).forEachIndexed { index, d ->
            val height by animateDpAsState(
                targetValue = heightDp * d / 128f
                /*
                Compute accurate duration => way too dynamic, better to keep a default spring for smoothness
                ,animationSpec = tween(
                    durationMillis = data.durationSinceLastData.toInt(),
                    easing = LinearEasing
                )*/
            )
            Box(
                barModifier(
                    index,
                    Modifier
                        .width(barWidthDp)
                        .height(height)
                        //.padding(start = if (index == 0) 0.dp else padding)
                        .align(Alignment.Bottom)
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