package net.spartanb312.boar.utils.color

import net.spartanb312.boar.utils.math.floorToInt

@JvmInline
value class ColorRGB(val rgba: Int) {

    constructor(r: Int, g: Int, b: Int) :
            this(r, g, b, 255)

    constructor(r: Int, g: Int, b: Int, a: Int) :
            this(
                (r and 255 shl 24) or
                        (g and 255 shl 16) or
                        (b and 255 shl 8) or
                        (a and 255)
            )

    constructor(r: Float, g: Float, b: Float) :
            this((r * 255.0f).toInt(), (g * 255.0f).toInt(), (b * 255.0f).toInt())

    constructor(r: Float, g: Float, b: Float, a: Float) :
            this((r * 255.0f).toInt(), (g * 255.0f).toInt(), (b * 255.0f).toInt(), (a * 255.0f).toInt())

    companion object {
        val WHITE = ColorRGB(255, 255, 255)
        val BLACK = ColorRGB(0, 0, 0)
        val DARK_BLUE = ColorRGB(0, 0, 170)
        val DARK_GREEN = ColorRGB(0, 170, 0)
        val DARK_AQUA = ColorRGB(0, 170, 170)
        val DARK_RED = ColorRGB(170, 0, 0)
        val DARK_PURPLE = ColorRGB(170, 0, 170)
        val GOLD = ColorRGB(250, 170, 0)
        val GRAY = ColorRGB(170, 170, 170)
        val DARK_GRAY = ColorRGB(85, 85, 85)
        val BLUE = ColorRGB(85, 85, 255)
        val GREEN = ColorRGB(85, 255, 85)
        val AQUA = ColorRGB(85, 255, 255)
        val RED = ColorRGB(255, 85, 85)
        val LIGHT_PURPLE = ColorRGB(255, 85, 255)
        val YELLOW = ColorRGB(255, 255, 85)
    }

    // Int color
    val r: Int
        get() = rgba shr 24 and 255

    val g: Int
        get() = rgba shr 16 and 255

    val b: Int
        get() = rgba shr 8 and 255

    val a: Int
        get() = rgba and 255

    // Float color
    val rFloat: Float
        get() = r / 255.0f

    val gFloat: Float
        get() = g / 255.0f

    val bFloat: Float
        get() = b / 255.0f

    val aFloat: Float
        get() = a / 255.0f

    // HSB
    val hue: Float
        get() = ColorUtils.rgbToHue(r, g, b)

    val saturation: Float
        get() = ColorUtils.rgbToSaturation(r, g, b)

    val brightness: Float
        get() = ColorUtils.rgbToBrightness(r, g, b)

    // HSL
    val lightness: Float
        get() = ColorUtils.rgbToLightness(r, g, b)

    // Modification
    fun red(r: Int): ColorRGB = ColorRGB(rgba and 0xFFFFFF or (r shl 24))

    fun green(g: Int): ColorRGB = ColorRGB(rgba and -16711681 or (g shl 16))

    fun blue(b: Int): ColorRGB = ColorRGB(rgba and -65281 or (b shl 8))

    fun alpha(a: Int = this.a, rate: Float = 1f): ColorRGB = ColorRGB(rgba and -256 or (a * rate).toInt())

    fun transparency(a: Int = this.a, rate: Double) = ColorRGB(rgba and -256 or ((a * rate).toInt()))

    fun blend(rate: Float, alpha: Boolean = false): ColorRGB =
        ColorRGB(
            (r * rate).toInt().coerceIn(0..255),
            (g * rate).toInt().coerceIn(0..255),
            (b * rate).toInt().coerceIn(0..255),
            (if (alpha) a * rate else a).toInt().coerceIn(0..255)
        )

    fun circle(other: ColorRGB, hue: Float): ColorRGB {
        val h = hue.keepDecimal()
        return if (h < 0.5f) mix(other, h * 2.0f)
        else mix(other, (1.0f - h) * 2.0f)
    }

    private fun Float.keepDecimal(): Float = this - this.floorToInt()

    // Misc
    fun mix(other: ColorRGB, ratio: Float): ColorRGB {
        val rationSelf = 1.0f - ratio
        return ColorRGB(
            (r * rationSelf + other.r * ratio).toInt(),
            (g * rationSelf + other.g * ratio).toInt(),
            (b * rationSelf + other.b * ratio).toInt(),
            (a * rationSelf + other.a * ratio).toInt()
        )
    }

    infix fun mix(other: ColorRGB): ColorRGB {
        return ColorRGB(
            (r + other.r) / 2,
            (g + other.g) / 2,
            (b + other.b) / 2,
            (a + other.a) / 2
        )
    }

    fun toArgb() = ColorUtils.rgbaToArgb(rgba)

    fun toHSB() = ColorUtils.rgbToHSB(r, g, b, a)

    override fun toString(): String = "$r, $g, $b, $a"

}