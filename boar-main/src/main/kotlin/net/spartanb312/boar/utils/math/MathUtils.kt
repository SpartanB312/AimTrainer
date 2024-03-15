package net.spartanb312.boar.utils.math

import kotlin.math.*

@Suppress("NOTHING_TO_INLINE")
object MathUtils {

    @JvmStatic
    inline fun ceilToPOT(valueIn: Int): Int {
        var i = valueIn
        i--
        i = i or (i shr 1)
        i = i or (i shr 2)
        i = i or (i shr 4)
        i = i or (i shr 8)
        i = i or (i shr 16)
        i++
        return i
    }

    @JvmStatic
    inline fun round(value: Float, places: Int): Float {
        val scale = 10.0f.pow(places)
        return round(value * scale) / scale
    }

    @JvmStatic
    inline fun round(value: Double, places: Int): Double {
        val scale = 10.0.pow(places)
        return round(value * scale) / scale
    }

    @JvmStatic
    inline fun decimalPlaces(value: Double) = value.toString().split('.').getOrElse(1) { "0" }.length

    @JvmStatic
    inline fun decimalPlaces(value: Float) = value.toString().split('.').getOrElse(1) { "0" }.length

    @JvmStatic
    inline fun isNumberEven(i: Int): Boolean {
        return i and 1 == 0
    }

    @JvmStatic
    inline fun reverseNumber(num: Int, min: Int, max: Int): Int {
        return max + min - num
    }

    @JvmStatic
    inline fun convertRange(valueIn: Int, minIn: Int, maxIn: Int, minOut: Int, maxOut: Int): Int {
        return convertRange(
            valueIn.toDouble(),
            minIn.toDouble(),
            maxIn.toDouble(),
            minOut.toDouble(),
            maxOut.toDouble()
        ).toInt()
    }

    @JvmStatic
    inline fun convertRange(valueIn: Float, minIn: Float, maxIn: Float, minOut: Float, maxOut: Float): Float {
        return convertRange(
            valueIn.toDouble(),
            minIn.toDouble(),
            maxIn.toDouble(),
            minOut.toDouble(),
            maxOut.toDouble()
        ).toFloat()
    }

    @JvmStatic
    inline fun convertRange(valueIn: Double, minIn: Double, maxIn: Double, minOut: Double, maxOut: Double): Double {
        val rangeIn = maxIn - minIn
        val rangeOut = maxOut - minOut
        val convertedIn = (valueIn - minIn) * (rangeOut / rangeIn) + minOut
        val actualMin = min(minOut, maxOut)
        val actualMax = max(minOut, maxOut)
        return min(max(convertedIn, actualMin), actualMax)
    }

    @JvmStatic
    inline fun lerp(from: Double, to: Double, delta: Double): Double {
        return from + (to - from) * delta
    }

    @JvmStatic
    inline fun lerp(from: Float, to: Float, delta: Float): Float {
        return from + (to - from) * delta
    }

    @JvmStatic
    inline fun calcSegments(segmentsIn: Int, radius: Double, range: Float): Int {
        if (segmentsIn != -0) return segmentsIn
        val segments = radius * 0.5 * PI * (range / 360.0)
        return max(segments.roundToInt(), 16)
    }

    @JvmStatic
    inline fun diagonalFOVToVerticalFOV(fovd: Double, aspect: Double): Double {
        val f = 1000.0
        val l = 2 * f * tan(fovd / 360.0 * PI)
        val h = sqrt(l.pow(2) / (1 + aspect.pow(2)))
        return 2 * (atan2(h / 2.0, f) / PI * 180.0)
    }

    @JvmStatic
    inline fun diagonalFOVToHorizontalFOV(fovd: Double, aspect: Double): Double {
        val f = 1000.0
        val l = 2 * f * tan(fovd / 360.0 * PI)
        val h = sqrt(l.pow(2) / (1 + aspect.pow(2)))
        val w = h * aspect
        return 2 * (atan2(w / 2.0, f) / PI * 180.0)
    }

    @JvmStatic
    inline fun horizontalFOVToVerticalFOV(fovx: Double, aspect: Double): Double {
        val f = 1000.0
        val w = 2 * f * tan(fovx / 360.0 * PI)
        val h = w / aspect
        return 2 * (atan2(h / 2.0, f) / PI * 180.0)
    }

    @JvmStatic
    inline fun horizontalFOVToDiagonalFOV(fovx: Double, aspect: Double): Double {
        val f = 1000.0
        val w = 2 * f * tan(fovx / 360.0 * PI)
        val h = w / aspect
        val l = sqrt(w * w + h * h)
        return 2 * (atan2(l / 2.0, f) / PI * 180.0)
    }

    @JvmStatic
    inline fun verticalFOVToDiagonalFOV(fovv: Double, aspect: Double): Double {
        val f = 1000.0
        val h = 2 * f * tan(fovv / 360.0 * PI)
        val w = h * aspect
        val l = sqrt(w * w + h * h)
        return 2 * (atan2(l / 2.0, f) / PI * 180.0)
    }

    @JvmStatic
    inline fun verticalFOVToHorizontalFOV(fovv: Double, aspect: Double): Double {
        val f = 1000.0
        val h = 2 * f * tan(fovv / 360.0 * PI)
        val w = h * aspect
        return 2 * (atan2(w / 2.0, f) / PI * 180.0)
    }

    @JvmStatic
    inline fun Float.d2vFOV(aspect: Double): Float {
        return diagonalFOVToVerticalFOV(this.toDouble(), aspect).toFloat()
    }

    @JvmStatic
    inline fun Float.d2hFOV(aspect: Double): Float {
        return diagonalFOVToHorizontalFOV(this.toDouble(), aspect).toFloat()
    }

    @JvmStatic
    inline fun Float.h2vFOV(aspect: Double): Float {
        return horizontalFOVToVerticalFOV(this.toDouble(), aspect).toFloat()
    }

    @JvmStatic
    inline fun Float.h2dFOV(aspect: Double): Float {
        return horizontalFOVToDiagonalFOV(this.toDouble(), aspect).toFloat()
    }

    @JvmStatic
    inline fun Float.v2dFOV(aspect: Double): Float {
        return verticalFOVToDiagonalFOV(this.toDouble(), aspect).toFloat()
    }

    @JvmStatic
    inline fun Float.v2fFOV(aspect: Double): Float {
        return verticalFOVToHorizontalFOV(this.toDouble(), aspect).toFloat()
    }

}