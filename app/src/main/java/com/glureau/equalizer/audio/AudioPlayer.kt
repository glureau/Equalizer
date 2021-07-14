package com.glureau.equalizer.audio

import android.content.res.AssetManager
import android.media.MediaPlayer
import androidx.compose.runtime.MutableState

class AudioPlayer {

    private var player: MediaPlayer? = null

    private val audioComputer = VisualizerComputer()

    fun play(assets: AssetManager, fileName: String, visualizerData: MutableState<VisualizerData>) {
        val afd = assets.openFd(fileName)
        player = MediaPlayer().apply {
            setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            prepare()
            start()
        }
        audioComputer.
        start(audioSessionId = player!!.audioSessionId, onData = { data ->
            visualizerData.value = data
        })
    }

    fun stop() {
        audioComputer.stop()
        player?.stop()
        player?.release()
        player = null
    }
}