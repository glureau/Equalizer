package com.glureau.equalizer.audio

class VisualizerData(
    val bytes: IntArray = IntArray(0),
    val resolution: Int = 1,
    val signalForce: Int = 0,
) {
    // Can add some filtering here, for bass or something...
}