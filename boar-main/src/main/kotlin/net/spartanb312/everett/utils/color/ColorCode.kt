package net.spartanb312.everett.utils.color

val colorArray = arrayOf(
    ColorRGB(0, 0, 0),
    ColorRGB(0, 0, 170),
    ColorRGB(0, 170, 0),
    ColorRGB(0, 170, 170),
    ColorRGB(170, 0, 0),
    ColorRGB(170, 0, 170),
    ColorRGB(255, 170, 0),
    ColorRGB(170, 170, 170),
    ColorRGB(85, 85, 85),
    ColorRGB(85, 85, 255),
    ColorRGB(85, 255, 85),
    ColorRGB(85, 255, 255),
    ColorRGB(255, 85, 85),
    ColorRGB(255, 85, 255),
    ColorRGB(255, 255, 85),
    ColorRGB(255, 255, 255)
)

val Char.colorCode
    get() = when (val preCode = code) {
        in 48..57 -> preCode - 48
        in 97..102 -> preCode - 87
        else -> null
    }

fun Char.getColor(prev: ColorRGB = ColorRGB.WHITE): ColorRGB? {
    return if (this == 'r') prev
    else {
        val code = colorCode ?: return null
        colorArray.getOrNull(code)
    }
}

val emptyColorArray = emptyArray<ColorRGB>()
val shadowColor = ColorRGB.BLACK.alpha(128)