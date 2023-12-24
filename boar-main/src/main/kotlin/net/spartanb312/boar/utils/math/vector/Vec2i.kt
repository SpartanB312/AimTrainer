package net.spartanb312.boar.utils.math.vector

@JvmInline
value class Vec2i private constructor(val bits: Long) {

    constructor(x: Number, y: Number) : this(
        (x.toInt().toLong() shl 32) or (y.toInt().toLong() and 0xFFFFFFFF)
    )

    constructor(vec2d: Vec2d) : this(vec2d.x.toFloat(), vec2d.y.toFloat())

    inline val x get() = getX(bits)
    inline val y get() = getY(bits)
    inline val xLong get() = x.toLong()
    inline val yLong get() = y.toLong()
    inline val xDouble get() = x.toDouble()
    inline val yDouble get() = y.toDouble()
    inline val xFloat get() = x.toFloat()
    inline val yFloat get() = y.toFloat()

    // Divide
    operator fun div(vec2i: Vec2i) = div(vec2i.x, vec2i.y)

    operator fun div(divider: Int) = div(divider, divider)

    fun div(x: Int, y: Int) = Vec2i(this.x / x, this.y / y)

    // Multiply
    operator fun times(vec2i: Vec2i) = times(vec2i.x, vec2i.y)

    operator fun times(multiplier: Int) = times(multiplier, multiplier)

    fun times(x: Int, y: Int) = Vec2i(this.x * x, this.y * y)

    // Minus
    operator fun minus(vec2i: Vec2i) = minus(vec2i.x, vec2i.y)

    operator fun minus(value: Int) = minus(value, value)

    fun minus(x: Int, y: Int) = plus(-x, -y)

    // Plus
    operator fun plus(vec2i: Vec2i) = plus(vec2i.x, vec2i.y)

    operator fun plus(value: Int) = plus(value, value)

    fun plus(x: Int, y: Int) = Vec2i(this.x + x, this.y + y)

    operator fun component1() = x

    operator fun component2() = y

    infix fun dot(target: Vec2i): Int = x * target.x + y * target.y

    infix fun cross(target: Vec2i): Vec3i = Vec3i(
        0,
        0,
        x * target.y - y * target.x
    )

    override fun toString(): String {
        return "Vec2i[${this.x}, ${this.y}]"
    }

    companion object {
        val ZERO = Vec2i(0f, 0f)

        @JvmStatic
        fun getX(bits: Long): Int {
            return (bits shr 32).toInt()
        }

        @JvmStatic
        fun getY(bits: Long): Int {
            return (bits and 0xFFFFFFFF).toInt()
        }
    }

}