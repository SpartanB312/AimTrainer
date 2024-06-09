package net.spartanb312.everett.utils.math.vector

import net.spartanb312.everett.utils.math.toDegree
import kotlin.math.*

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
    inline val length get() = sqrt((x * x + y * y + z * z).toDouble()).toFloat()

    inline val yaw get() = atan(z / x)
    inline val pitch get() = atan(y / sqrt(x * x + z * z))
    inline val roll get() = atan(x / y)

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
        val r = length
        if (r == 0f) return this
        return Vec3f(x / r, y / r, z / r)
    }

    fun angle(other: Vec3f): Float {
        return acos((this dot other) / (length * other.length).toDouble()).toFloat()
    }

    fun toEuler(): Triple<Float, Float, Float> {
        val normalized = normalize()
        val reference = Vec3f(0, 0, 10)
        val cosTheta = normalized.angle(reference)
        val theta = acos(cosTheta)
        val axis = reference cross normalized

        val k = axis.normalize()
        val c = cos(theta / 2)
        val s = sin(theta / 2)
        val w = c
        val x = k.x * s
        val y = k.y * s
        val z = k.z * s

        val t0 = 2.0 * (w * x + y * z)
        val t1 = 1.0 - 2.0 * (x * x + y * y)
        val roll = atan2(t0, t1)

        val t2 = (2.0 * (w * y - z * x)).coerceIn(-1.0..1.0)
        val pitch = asin(t2)

        val t3 = 2.0 * (w * z + x * y)
        val t4 = 1.0 - 2.0 * (y * y + z * z)
        val yaw = atan2(t3, t4)
        return Triple(yaw.toDegree().toFloat(), pitch.toDegree().toFloat(), roll.toDegree().toFloat())
    }

    override fun toString(): String {
        return "Vec3f[${this.x}, ${this.y}, ${this.z}]"
    }

    override fun equals(other: Any?): Boolean {
        return other is Vec3f && other.x == x && other.y == y && other.z == z
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        return result
    }

    companion object {
        @JvmField
        val ZERO = Vec3f(0f, 0f, 0f)
    }

}

fun Vec3f(yaw: Float, pitch: Float): Vec3f {
    val x = cos(yaw)
    val z = sin(yaw)
    val y = sqrt(x * x + z * z) * tan(pitch)
    return Vec3f(x, y, z)
}