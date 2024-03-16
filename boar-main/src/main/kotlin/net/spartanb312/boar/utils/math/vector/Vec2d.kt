package net.spartanb312.boar.utils.math.vector

import kotlin.math.acos
import kotlin.math.sqrt

data class Vec2d(val x: Double = 0.0, val y: Double = 0.0) {

    constructor(x: Number, y: Number) : this(x.toDouble(), y.toDouble())

    constructor(vec3d: Vec3d) : this(vec3d.x, vec3d.y)

    constructor(vec2d: Vec2d) : this(vec2d.x, vec2d.y)

    inline val xInt get() = x.toInt()
    inline val yInt get() = y.toInt()
    inline val xLong get() = x.toLong()
    inline val yLong get() = y.toLong()
    inline val xFloat get() = x.toFloat()
    inline val yFloat get() = y.toFloat()
    inline val length get() = sqrt(x * x + y * y)

    // Divide
    operator fun div(vec2d: Vec2d) = div(vec2d.x, vec2d.y)

    operator fun div(divider: Double) = div(divider, divider)

    fun div(x: Double, y: Double) = Vec2d(this.x / x, this.y / y)

    // Multiply
    operator fun times(vec2d: Vec2d) = times(vec2d.x, vec2d.y)

    operator fun times(multiplier: Double) = times(multiplier, multiplier)

    fun times(x: Double, y: Double) = Vec2d(this.x * x, this.y * y)

    // Minus
    operator fun minus(vec2d: Vec2d) = minus(vec2d.x, vec2d.y)

    operator fun minus(sub: Double) = minus(sub, sub)

    fun minus(x: Double, y: Double) = plus(-x, -y)

    // Plus
    operator fun plus(vec2d: Vec2d) = plus(vec2d.x, vec2d.y)

    operator fun plus(add: Double) = plus(add, add)

    fun plus(x: Double, y: Double) = Vec2d(this.x + x, this.y + y)

    infix fun dot(target: Vec2d): Double = x * target.x + y * target.y

    infix fun cross(target: Vec2d): Vec3d = Vec3d(
        0,
        0,
        x * target.y - y * target.x
    )

    fun normalize(): Vec2d {
        val r = length
        if (r == 0.0) return this
        return Vec2d(x / r, y / r)
    }

    fun angle(other: Vec2d): Double {
        return acos((this dot other) / (length * other.length))
    }

    override fun toString(): String {
        return "Vec2d[${this.x}, ${this.y}]"
    }

    override fun equals(other: Any?): Boolean {
        return other is Vec2d && other.x == x && other.y == y
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }

    companion object {
        @JvmField
        val ZERO = Vec2d(0.0, 0.0)
    }

}