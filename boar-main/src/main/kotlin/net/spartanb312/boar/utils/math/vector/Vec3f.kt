package net.spartanb312.boar.utils.math.vector

import kotlin.math.sqrt

data class Vec3f(val x: Float = 0f, val y: Float = 0f, val z: Float = 0f) {

    constructor(x: Number, y: Number, z: Number) : this(x.toFloat(), y.toFloat(), z.toFloat())

    constructor(vec3f: Vec3f) : this(vec3f.x, vec3f.y, vec3f.z)

    constructor(vec2f: Vec2f, z: Float = 0f) : this(vec2f.x, vec2f.y, z)

    inline val xInt get() = x.toInt()
    inline val yInt get() = y.toInt()
    inline val zInt get() = z.toInt()
    inline val xLong get() = x.toLong()
    inline val yLong get() = y.toLong()
    inline val zLong get() = z.toLong()
    inline val xDouble get() = x.toDouble()
    inline val yDouble get() = y.toDouble()
    inline val zDouble get() = z.toDouble()

    // Divide
    operator fun div(vec3f: Vec3f) = div(vec3f.x, vec3f.y, vec3f.z)

    operator fun div(divider: Float) = div(divider, divider, divider)

    fun div(x: Float, y: Float, z: Float) = Vec3f(this.x / x, this.y / y, this.z / z)

    // Multiply
    operator fun times(vec3f: Vec3f) = times(vec3f.x, vec3f.y, vec3f.z)

    operator fun times(multiplier: Float) = times(multiplier, multiplier, multiplier)

    fun times(x: Float, y: Float, z: Float) = Vec3f(this.x * x, this.y * y, this.y * z)

    // Minus
    operator fun minus(vec3f: Vec3f) = minus(vec3f.x, vec3f.y, vec3f.z)

    operator fun minus(sub: Float) = minus(sub, sub, sub)

    fun minus(x: Float, y: Float, z: Float) = plus(-x, -y, -z)

    // Plus
    operator fun plus(vec3f: Vec3f) = plus(vec3f.x, vec3f.y, vec3f.z)

    operator fun plus(add: Float) = plus(add, add, add)

    fun plus(x: Float, y: Float, z: Float) = Vec3f(this.x + x, this.y + y, this.z + z)

    infix fun dot(target: Vec3f): Float = x * target.x + y * target.y + z * target.z

    infix fun cross(target: Vec3f): Vec3f = Vec3f(
        y * target.z - z * target.y,
        z * target.x - x * target.z,
        x * target.y - y * target.x
    )

    fun normalize(): Vec3f {
        val r = sqrt((x * x + y * y + z * z).toDouble())
        if (r == 0.0) return this
        return Vec3f(x / r, y / r, z / r)
    }

    override fun toString(): String {
        return "Vec3f[${this.x}, ${this.y}, ${this.z}]"
    }

    companion object {
        @JvmField
        val ZERO = Vec3f(0f, 0f, 0f)
    }

}