package net.spartanb312.everett.utils.misc

import kotlin.random.Random

val Float.asRange: ClosedFloatingPointRange<Float> get() = this..this

fun ClosedFloatingPointRange<Float>.random(): Float {
    if (start == endInclusive) return start
    return Random.nextDouble(start.toDouble(), endInclusive.toDouble()).toFloat()
}

fun ClosedFloatingPointRange<Double>.random(): Double {
    if (start == endInclusive) return start
    return Random.nextDouble(start, endInclusive)
}