package net.spartanb312.boar.utils.math

import kotlin.math.max
import kotlin.math.min

object ConvergeUtil {

    fun Int.converge(target: Int, speed: Float): Int {
        if (this == target) return this
        var speed0 = speed
        val larger = target > this
        if (speed0 < 0f) speed0 = 0f
        else if (speed0 > 1f) speed0 = 1f
        val dif = max(this, target) - min(this, target)
        var factor = dif * speed0
        if (factor < 0.1f) factor = 0.1f
        return if (larger) min(this + factor.toInt(), target)
        else max(this - factor.toInt(), target)
    }

    fun Float.converge(target: Float, speed: Float): Float {
        if (this == target) return this
        var speed0 = speed
        val larger = target > this
        if (speed0 < 0f) speed0 = 0f
        else if (speed0 > 1f) speed0 = 1f
        val dif = max(this, target) - min(this, target)
        var factor = dif * speed0
        if (factor < 0.1f) factor = 0.1f
        return if (larger) min(this + factor, target)
        else max(this - factor, target)
    }

    fun Double.converge(target: Double, speed: Double): Double {
        if (this == target) return this
        var speed0 = speed
        val larger = target > this
        if (speed0 < 0.0) speed0 = 0.0
        else if (speed0 > 1.0) speed0 = 1.0
        val dif = max(this, target) - min(this, target)
        var factor = dif * speed0
        if (factor < 0.1) factor = 0.1
        return if (larger) min(this + factor, target)
        else max(this - factor, target)
    }

    fun Float.linearTo(target: Float, progression: Float): Float {
        val p = progression.coerceIn(0f, 100f)
        val offset = target - this
        return this + offset * progression / 100f
    }

}