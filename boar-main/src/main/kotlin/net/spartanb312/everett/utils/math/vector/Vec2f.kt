package net.spartanb312.everett.utils.math.vector

import kotlin.math.acos
import kotlin.math.sqrt

@JvmInline
value class Vec2f private constructor(val bits: Long) {

    constructor(x: Number, y: Number) : this(
        (x.toFloat().toRawBits().toLong() shl 32) or (y.toFloat().toRawBits().toLong() and 0xFFFFFFFF)
    )

    constructor(vec2d: Vec2d) : this(vec2d.x.toFloat(), vec2d.y.toFloat())

    constructor() : this(0f, 0f)

    inline val x: Float get() = getX(bits)
    inline val y: Float get() = getY(bits)
    inline val xInt get() = x.toInt()
    inline val yInt get() = y.toInt()
    inline val xLong get() = x.toLong()
    inline val yLong get() = y.toLong()
    inline val xDouble get() = x.toDouble()
    inline val yDouble get() = y.toDouble()
    inline val length get() = sqrt(x * x + y * y)

    // Divide
    operator fun div(vec2f: Vec2f) = div(vec2f.x, vec2f.y)

    operator fun div(divider: Float) = div(divider, divider)

    fun div(x: Float, y: Float) = Vec2f(this.x / x, this.y / y)

    // Multiply
    operator fun times(vec2f: Vec2f) = times(vec2f.x, vec2f.y)

    operator fun times(multiplier: Float) = times(multiplier, multiplier)

    fun times(x: Float, y: Float) = Vec2f(this.x * x, this.y * y)

    // Minus
    operator fun minus(vec2f: Vec2f) = minus(vec2f.x, vec2f.y)

    operator fun minus(value: Float) = minus(value, value)

    fun minus(x: Float, y: Float) = plus(-x, -y)

    // Plus
    operator fun plus(vec2f: Vec2f) = plus(vec2f.x, vec2f.y)

    operator fun plus(value: Float) = plus(value, value)

    fun plus(x: Float, y: Float) = Vec2f(this.x + x, this.y + y)

    operator fun component1() = x

    operator fun component2() = y

    infix fun dot(target: Vec2f): Float = x * target.x + y * target.y

    infix fun cross(target: Vec2f): Vec3f = Vec3f(
        0,
        0,
        x * target.y - y * target.x
    )

    fun normalize(): Vec2f {
        val r = length
        if (r == 0f) return this
        return Vec2f(x / r, y / r)
    }

    fun angle(other: Vec2f): Float {
        return acos((this dot other) / (length * other.length))
    }

    override fun toString(): String {
        return "Vec2f[${this.x}, ${this.y}]"
    }

    companion object {
        val ZERO = Vec2f(0f, 0f)

        @JvmStatic
        fun getX(bits: Long): Float {
            return Float.fromBits((bits shr 32).toInt())
        }

        @JvmStatic
        fun getY(bits: Long): Float {
            return Float.fromBits((bits and 0xFFFFFFFF).toInt())
        }
    }

}