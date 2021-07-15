package com.glureau.equalizer.ui

import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@JvmInline
value class Point(private val p: Pair<Float, Float>) {
    fun x() = p.first
    fun y() = p.second
}

fun computeDoubleSidedPoints(
    resampled: IntArray,
    viewportWidth: Float,
    viewportHeight: Float,
    segmentCount: Int,
    continuous: Boolean = false,
): List<Point> {
    if (resampled.isEmpty()) return emptyList()
    val barWidth = viewportWidth / (segmentCount - if (continuous) 0 else 2)
    val top = mutableListOf<Point>()
    val bottom = mutableListOf<Point>()
    resampled.forEachIndexed { index, d ->
        val isTop = index % 2 == 0
        if (isTop) {
            val targetValue = viewportHeight * (0.5f - (d / 256f))
            top.add(Point(barWidth * (index + 0) to targetValue))
        } else {
            val targetValue = viewportHeight * (0.5f + (d / 256f))
            bottom.add(Point(barWidth * (index - 1) to targetValue))
        }
    }
    return if (!continuous) {
        top + bottom.reversed()
    } else {
        // Added 2 points to make it nicer for circular projection
        top + top.first() + bottom.first() + bottom.reversed()
    }
}

fun List<Point>.circularProj(viewportWidth: Float, viewportHeight: Float) =
    circularProjection(this, viewportWidth, viewportHeight)

/**
 * Take points in a rectangle and project them on a disk,
 * use x as the angle, and y as the distance from center.
 */
fun circularProjection(
    points: List<Point>,
    viewportWidth: Float,
    viewportHeight: Float,
    // 0f = no inner radius, 1f = everything is applied on the external circle
    innerRadiusRatio: Float = 0.4f,
): List<Point> {
    val circleRadius = min(viewportWidth, viewportHeight) / 2
    val center = Point(viewportWidth / 2 to viewportHeight / 2)
    val innerRadius = innerRadiusRatio * circleRadius
    val outerRadius = (1 - innerRadiusRatio) * circleRadius
    return points.mapIndexed { i, p ->
        val angle = Math.PI.toFloat() * 2 * (p.x() / viewportWidth)
        val radiusRatio = p.y() / viewportHeight
        val newRadius = innerRadius + outerRadius * radiusRatio
        val x = newRadius * cos(angle) + center.x()
        val y = newRadius * sin(angle) + center.y()
        /*Log.e(
            "LOG",
            "viewportWidth=$viewportWidth viewportHeight=$viewportHeight i=$i  -- $x $y"
        )*/
        Point(x to y)
    }
}
