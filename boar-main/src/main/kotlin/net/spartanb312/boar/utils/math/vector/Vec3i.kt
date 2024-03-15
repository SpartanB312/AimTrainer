package net.spartanb312.boar.utils.math.vector

import kotlin.math.sqrt

data class Vec3i(val x: Int = 0, val y: Int = 0, val z: Int = 0) {

    constructor(x: Number, y: Number, z: Number) : this(x.toInt(), y.toInt(), z.toInt())

    constructor(vec3i: Vec3i) : this(vec3i.x, vec3i.y, vec3i.z)

    constructor(vec2i: Vec2i, z: Int = 0) : this(vec2i.x, vec2i.y, z)

    inline val xLong get() = x.toLong()
    inline val yLong get() = y.toLong()
    inline val zLong get() = z.toLong()
    inline val xDouble get() = x.toDouble()
    inline val yDouble get() = y.toDouble()
    inline val zDouble get() = z.toDouble()
    inline val xFloat get() = x.toFloat()
    inline val yFloat get() = y.toFloat()
    inline val zFloat get() = z.toFloat()
    inline val length get() = sqrt((x * x + y * y + z * z).toDouble())

    // Divide
    operator fun div(vec3i: Vec3i) = div(vec3i.x, vec3i.y, vec3i.z)

    operator fun div(divider: Int) = div(divider, divider, divider)

    fun div(x: Int, y: Int, z: Int) = Vec3i(this.x / x, this.y / y, this.z / z)

    // Multiply
    operator fun times(vec3i: Vec3i) = times(vec3i.x, vec3i.y, vec3i.z)

    operator fun times(multiplier: Int) = times(multiplier, multiplier, multiplier)

    fun times(x: Int, y: Int, z: Int) = Vec3i(this.x * x, this.y * y, this.y * z)

    // Minus
    operator fun minus(vec3i: Vec3i) = minus(vec3i.x, vec3i.y, vec3i.z)

    operator fun minus(sub: Int) = minus(sub, sub, sub)

    fun minus(x: Int, y: Int, z: Int) = plus(-x, -y, -z)

    // Plus
    operator fun plus(vec3i: Vec3i) = plus(vec3i.x, vec3i.y, vec3i.z)

    operator fun plus(add: Int) = plus(add, add, add)

    fun plus(x: Int, y: Int, z: Int) = Vec3i(this.x + x, this.y + y, this.z + z)

    infix fun dot(target: Vec3i): Int = x * target.x + y * target.y + z * target.z

    infix fun cross(target: Vec3i): Vec3i = Vec3i(
        y * target.z - z * target.y,
        z * target.x - x * target.z,
        x * target.y - y * target.x
    )

    override fun toString(): String {
        return "Vec3i[${this.x}, ${this.y}, ${this.z}]"
    }

    override fun equals(other: Any?): Boolean {
        return other is Vec3i && other.x == x && other.y == y && other.z == z
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        return result
    }
    companion object {
        @JvmField
        val ZERO = Vec3i(0, 0, 0)
    }

}