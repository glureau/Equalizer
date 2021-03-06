package com.glureau.equalizer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.glureau.equalizer.audio.AudioPlayer
import com.glureau.equalizer.audio.VisualizerComputer
import com.glureau.equalizer.audio.VisualizerData
import com.glureau.equalizer.ui.*
import com.glureau.equalizer.ui.ext.repeat
import com.glureau.equalizer.ui.theme.EqualizerTheme

class MainActivity : ComponentActivity() {
    private val audioPlayer = AudioPlayer()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        VisualizerComputer.setupPermissions(this)
        setContent {
            val visualizerData = remember { mutableStateOf(VisualizerData()) }
            val (isPlaying, setPlaying) = remember { mutableStateOf(false) }
            EqualizerTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Content(isPlaying, setPlaying, visualizerData)
                }
                if (isPlaying) {
                    audioPlayer.play(assets, "instru.mp3", visualizerData)
                } else {
                    audioPlayer.stop()
                }
            }
        }
    }
}

@Composable
fun Content(
    isPlaying: Boolean,
    setPlaying: (Boolean) -> Unit,
    visualizerData: MutableState<VisualizerData>
) {
    LazyColumn {
        item {
            Button(
                onClick = {
                    setPlaying(!isPlaying)
                },
                modifier = Modifier.padding(2.dp),
            ) {
                Text(if (isPlaying) "stop" else "play")
            }
        }

        val someColors =
            listOf(Color.Blue, Color.Green, Color.Yellow, Color.Magenta, Color.Red, Color.Cyan)
        val displayAllItems = false
        val selectItemIndex = 0

        if (displayAllItems || (selectItemIndex == 0))
            item {
                FancyTubularStackedBarEqualizer(
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .padding(vertical = 4.dp),
                    data = visualizerData.value,
                    barCount = 48,
                    maxStackCount = 16,
                )
            }

        if (displayAllItems || (selectItemIndex == 1))
            item {
                CircularStackedBarEqualizer(
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(Color(0xff111111)),
                    data = visualizerData.value,
                    barCount = 48,
                    maxStackCount = 16
                )
            }

        if (displayAllItems || (selectItemIndex == 2))
            item {
                StackedBarEqualizer(
                    Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(vertical = 4.dp)
                        .background(Color(0x50000000)),
                    data = visualizerData.value,
                    barCount = 64
                )
            }

        if (displayAllItems || (selectItemIndex == 3))
            item {
                FullBarEqualizer(
                    Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(vertical = 4.dp)
                        .background(Color(0x50000000)),
                    barModifier = { i, m -> m.background(someColors[i % someColors.size]) },
                    data = visualizerData.value,
                    barCount = 64
                )
            }

        if (displayAllItems || (selectItemIndex == 4))
            item {
                OneSidedPathEqualizer(
                    Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(vertical = 4.dp)
                        .background(Color(0x60000000)),
                    data = visualizerData.value,
                    segmentCount = 32,
                    fillBrush = Brush.linearGradient(
                        start = Offset.Zero,
                        end = Offset.Infinite,
                        colors = listOf(
                            Color.Red,
                            Color.Yellow,
                            Color.Green,
                            Color.Cyan,
                            Color.Blue,
                            Color.Magenta,
                        ).repeat(3)
                    )
                )
            }

        if (displayAllItems || (selectItemIndex == 5))
            item {
                DoubleSidedPathEqualizer(
                    Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(vertical = 4.dp)
                        .background(Color(0x70000000)),
                    data = visualizerData.value,
                    segmentCount = 128,
                    fillBrush = Brush.linearGradient(
                        start = Offset.Zero,
                        end = Offset(0f, Float.POSITIVE_INFINITY),
                        colors = listOf(Color.White, Color.Red, Color.White)
                    )
                )
            }

        if (displayAllItems || (selectItemIndex == 6))
            item {
                DoubleSidedCircularPathEqualizer(
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .padding(vertical = 4.dp)
                        .background(Color(0xE0000000)),
                    data = visualizerData.value,
                    segmentCount = 128,
                    fillBrush = Brush.radialGradient(
                        listOf(
                            Color.Red,
                            Color.Red,
                            Color.Yellow,
                            Color.Green
                        )
                    )
                )
            }
    }
}