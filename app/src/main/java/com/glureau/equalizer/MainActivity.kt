package com.glureau.equalizer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.glureau.equalizer.audio.AudioPlayer
import com.glureau.equalizer.audio.VisualizerComputer
import com.glureau.equalizer.audio.VisualizerData
import com.glureau.equalizer.ui.BarEqualizer
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
                    audioPlayer.play(assets, "bensound-hey.mp3", visualizerData)
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
    Column {
        Button(onClick = {
            setPlaying(!isPlaying)
        }) {
            Text(if (isPlaying) "stop" else "play")
        }
        BarEqualizer(
            Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color(0x40000000)),
            visualizerData.value
        )
    }
}