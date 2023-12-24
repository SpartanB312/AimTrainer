package net.spartanb312.boar.utils.color

@JvmInline
value class ColorHSB(val hsba: Int) {

    constructor(h: Int, s: Int, b: Int) :
            this(h, s, b, 255)

    constructor(h: Int, s: Int, b: Int, a: Int) :
            this(
                (h and 255 shl 24) or
                        (s and 255 shl 16) or
                        (b and 255 shl 8) or
                        (a and 255)
            )

    constructor(h: Float, s: Float, b: Float) :
            this((h * 255.0f).toInt(), (s * 255.0f).toInt(), (b * 255.0f).toInt())

    constructor(h: Float, s: Float, b: Float, a: Float) :
            this((h * 255.0f).toInt(), (s * 255.0f).toInt(), (b * 255.0f).toInt(), (a * 255.0f).toInt())

    companion object {
        val WHITE = ColorHSB(0, 0, 255)
        val BLACK = ColorHSB(0, 0, 0)
        val DARK_BLUE = ColorHSB(170, 255, 170)
        val DARK_GREEN = ColorHSB(85, 255, 170)
        val DARK_AQUA = ColorHSB(127, 255, 170)
        val DARK_RED = ColorHSB(0, 255, 170)
        val DARK_PURPLE = ColorHSB(212, 255, 170)
        val GOLD = ColorHSB(28, 255, 250)
        val GRAY = ColorHSB(0, 0, 170)
        val DARK_GRAY = ColorHSB(0, 0, 85)
        val BLUE = ColorHSB(170, 170, 255)
        val GREEN = ColorHSB(85, 170, 255)
        val AQUA = ColorHSB(127, 170, 255)
        val RED = ColorHSB(0, 170, 255)
        val LIGHT_PURPLE = ColorHSB(212, 170, 255)
        val YELLOW = ColorHSB(42, 170, 255)
    }

    // Int color
    val h: Int
        get() = hsba shr 24 and 255

    val s: Int
        get() = hsba shr 16 and 255

    val b: Int
        get() = hsba shr 8 and 255

    val a: Int
        get() = hsba and 255

    // Float color
    val hFloat: Float
        get() = h / 255.0f

    val sFloat: Float
        get() = s / 255.0f

    val bFloat: Float
        get() = b / 255.0f

    val aFloat: Float
        get() = a / 255.0f

    // RGB
    val red: Float
        get() = toRGB().rFloat

    val green: Float
        get() = toRGB().gFloat

    val blue: Float
        get() = toRGB().bFloat

    // HSL
    val lightness
        get() = toRGB().lightness

    // Modification
    fun hue(h: Int): ColorHSB = ColorHSB(hsba and 0xFFFFFF or (h shl 24))

    fun saturation(s: Int): ColorHSB = ColorHSB(hsba and -16711681 or (s shl 16))

    fun brightness(b: Int): ColorHSB = ColorHSB(hsba and -65281 or (b shl 8))

    fun alpha(a: Int = this.a, rate: Float = 1f): ColorHSB = ColorHSB(hsba and -256 or (a * rate).toInt())

    // Misc
    fun mix(other: ColorHSB, ratio: Float): ColorHSB {
        val rationSelf = 1.0f - ratio
        return ColorHSB(
            (h * rationSelf + other.h * ratio).toInt(),
            (s * rationSelf + other.s * ratio).toInt(),
            (b * rationSelf + other.b * ratio).toInt(),
            (a * rationSelf + other.a * ratio).toInt()
        )
    }

    infix fun mix(other: ColorHSB): ColorHSB {
        return ColorHSB(
            (h + other.h) / 2,
            (s + other.s) / 2,
            (b + other.b) / 2,
            (a + other.a) / 2
        )
    }

    fun toArgb() = ColorUtils.rgbaToArgb(toRGB().rgba)

    fun toRGB() = ColorUtils.hsbToRGB(hFloat, sFloat, bFloat, aFloat)

    override fun toString(): String = "$h, $s, $b, $a"

}