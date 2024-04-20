@file:Suppress("NOTHING_TO_INLINE")

package net.spartanb312.everett.utils.math.vector

import net.spartanb312.everett.utils.math.sq
import kotlin.math.hypot
import kotlin.math.sqrt

inline fun distance(x1: Double, y1: Double, x2: Double, y2: Double): Double =
    hypot(x2 - x1, y2 - y1)

inline fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Double =
    hypot((x2 - x1).toDouble(), (y2 - y1).toDouble())

inline fun distance(x1: Int, y1: Int, x2: Int, y2: Int): Double =
    hypot((x2 - x1).toDouble(), (y2 - y1).toDouble())

inline fun distanceSq(x1: Double, y1: Double, x2: Double, y2: Double): Double =
    (x2 - x1).sq + (y2 - y1).sq

inline fun distanceSq(x1: Float, y1: Float, x2: Float, y2: Float): Float =
    (x2 - x1).sq + (y2 - y1).sq

inline fun distanceSq(x1: Int, y1: Int, x2: Int, y2: Int): Int =
    (x2 - x1).sq + (y2 - y1).sq

inline fun distance(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double): Double =
    sqrt(distanceSq(x1, y1, z1, x2, y2, z2))

inline fun distance(x1: Float, y1: Float, z1: Float, x2: Float, y2: Float, z2: Float): Double =//
    sqrt(distanceSq(x1, y1, z1, x2, y2, z2).toDouble())

inline fun distance(x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int): Double =
    sqrt(distanceSq(x1, y1, z1, x2, y2, z2).toDouble())

inline fun distanceSq(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double): Double =
    (x2 - x1).sq + (y2 - y1).sq + (z2 - z1).sq

inline fun distanceSq(x1: Float, y1: Float, z1: Float, x2: Float, y2: Float, z2: Float): Float =
    (x2 - x1).sq + (y2 - y1).sq + (z2 - z1).sq

inline fun distanceSq(x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int): Int =
    (x2 - x1).sq + (y2 - y1).sq + (z2 - z1).sq