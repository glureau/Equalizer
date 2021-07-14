package com.glureau.equalizer.audio


import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.media.audiofx.Visualizer
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlin.math.abs
import kotlin.math.min

class VisualizerComputer(private val resolution: Int = 32) {

    companion object {
        fun setupPermissions(activity: Activity) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.RECORD_AUDIO), 42
                )
            }
        }

        private val CAPTURE_SIZE = Visualizer.getCaptureSizeRange()[0]
    }

    private var visualizer: Visualizer? = null

    // Callbacks are called on main thread
    private fun visualizerCallback(onData: (VisualizerData) -> Unit) =
        object : Visualizer.OnDataCaptureListener {
            override fun onFftDataCapture(
                visualizer: Visualizer,
                fft: ByteArray,
                samplingRate: Int
            ) {
                //Timber.e("FFT - samplingRate=$samplingRate, waveform=${fft.joinToString()} thread=" + Thread.currentThread())
                //onData(VisualizerData(bytes = process(fft), resolution = resolution))
            }

            override fun onWaveFormDataCapture(
                visualizer: Visualizer,
                waveform: ByteArray,
                samplingRate: Int
            ) {
                //Timber.e("Wave - samplingRate=$samplingRate, waveform=${waveform.joinToString()} thread=" + Thread.currentThread())
                onData(process(waveform))
            }
        }

    fun start(audioSessionId: Int = 0, onData: (VisualizerData) -> Unit) {
        stop()
        visualizer = Visualizer(audioSessionId).apply {
            enabled = false // All configuration have to be done in a disabled state
            captureSize = CAPTURE_SIZE
            //scalingMode = Visualizer.SCALING_MODE_NORMALIZED
            //measurementMode = Visualizer.MEASUREMENT_MODE_NONE
            setDataCaptureListener(
                visualizerCallback(onData),
                Visualizer.getMaxCaptureRate(),
                true,
                true
            )
            enabled = true // Configuration is done, can enable now...
        }
    }

    fun stop() {
        visualizer?.release()
    }

    /**
     * Capture has to be done
     */
    // Returns a color array (Int) of UI_RESOLUTION size
    private fun process(data: ByteArray): VisualizerData {
        val processed = IntArray(resolution)
        val groupSize = CAPTURE_SIZE / resolution
        for (i in 0 until resolution) {
            processed[i] = data.map { abs(it.toInt()) }
                .subList(i * groupSize, min((i + 1) * groupSize, data.size))
                .average().toInt()
        }
        val average = data.map(Byte::toInt).maxOf { it }
        return VisualizerData(
            bytes = processed,
            resolution = resolution,
            signalForce = average
        )
    }
}