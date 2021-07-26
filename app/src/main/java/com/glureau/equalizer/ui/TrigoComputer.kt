package com.glureau.equalizer.ui

import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

@JvmInline
value class Point(private val p: Pair<Float, Float>) {
    fun x() = p.first
    fun y() = p.second

    operator fun plus(other: Point) = Point(x() + other.x() to y() + other.y())
    operator fun minus(other: Point) = Point(x() - other.x() to y() - other.y())
    operator fun times(factor: Float) = Point(x() * factor to y() * factor)
    operator fun div(factor: Float) = times(1f / factor)

    override fun toString() = "[${x()}:${y()}]"
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
    return top + bottom.reversed()//if (!continuous) {
    //top + bottom.reversed()
    /*} else {
        // Added 2 points to make it nicer for circular projection
        top + top.first() + bottom.first() + bottom.reversed()
    }*/
}

fun computeStackedBarPoints(
    resampled: IntArray,
    viewportWidth: Float,
    viewportHeight: Float,
    barCount: Int,
    maxStackCount: Int,
    padding: Float,
): List<Point> {
    val barWidth = (viewportWidth / barCount) - padding
    val stackHeightWithPadding = viewportHeight / maxStackCount
    val stackHeight = stackHeightWithPadding - padding

    val nodes = mutableListOf<Point>()
    resampled.forEachIndexed { index, d ->
        //val barHeight by animateFloatAsState(targetValue = viewportHeight * (1 - (d / 128f)))
        val stackCount = (maxStackCount * (d / 128f)).roundToInt()
        for (stackIndex in 0..stackCount) {
            nodes += Point(
                barWidth * index + padding * index to
                        viewportHeight - stackIndex * stackHeight - padding * stackIndex
            )
            nodes += Point(
                barWidth * (index + 1) + padding * index to
                        viewportHeight - stackIndex * stackHeight - padding * stackIndex
            )
            nodes += Point(
                barWidth * (index + 1) + padding * index to
                        viewportHeight - (stackIndex + 1) * stackHeight - padding * stackIndex
            )
            nodes += Point(
                barWidth * index + padding * index to
                        viewportHeight - (stackIndex + 1) * stackHeight - padding * stackIndex
            )
        }
    }
    return nodes
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
    //val angleOffset = 1f - (1f / points.size)
    return points.mapIndexed { i, p ->
        val angle = Math.PI.toFloat() * 2 * (p.x() / viewportWidth) //* angleOffset
        val radiusRatio = 1 - (p.y() / viewportHeight)
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
