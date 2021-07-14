package com.glureau.equalizer.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.glureau.equalizer.audio.VisualizerData


@Composable
fun BarEqualizer(
    modifier: Modifier,
    visualizationData: VisualizerData,
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    Row(modifier
        .onSizeChanged { size = it }
        .background(Color(0x40000000))
    ) {
        val widthDp = LocalDensity.current.run { size.width.toDp() }
        val heightDp = LocalDensity.current.run { size.height.toDp() }
        val padding = 1.dp
        val barWidthDp = (widthDp / visualizationData.resolution)// - padding

        visualizationData.bytes.forEachIndexed { index, data ->
            val height by animateDpAsState(targetValue = heightDp * data / 128f)
            //val height by animateDpAsState(targetValue = heightDp * (data.toFloat() / 256))
            //val height by animateDpAsState(targetValue = heightDp * (1 - data.toFloat() / 256))
            Box(
                Modifier
                    .width(barWidthDp)
                    .height(height)
                    .padding(start = if (index == 0) 0.dp else padding)
                    .background(Color(0x40A0490F))
                    .align(Alignment.Bottom)
            )
        }
    }
}

/*
@Preview
@Composable
fun EqualizerPreview() {
    val data = IntArray(64) { ((sin(it.toDouble())+1) * 128).toInt() }
    Equalizer(
        Modifier
            .fillMaxSize()
            .background(Color.Magenta),
        VisualizerData(data),
        IntSize(600, 200)
    )
}*/