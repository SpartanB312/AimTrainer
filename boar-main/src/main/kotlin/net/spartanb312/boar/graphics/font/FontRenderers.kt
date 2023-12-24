package net.spartanb312.boar.graphics.font

import net.spartanb312.boar.graphics.texture.loader.TextureLoader
import net.spartanb312.boar.utils.ResourceHelper
import net.spartanb312.boar.utils.color.ColorHSB
import net.spartanb312.boar.utils.color.ColorRGB
import java.awt.Font

fun LocalFontRenderer(
    name: String,
    size: Float,
    style: Int = Font.PLAIN,
    antiAlias: Boolean = true,
    fractionalMetrics: Boolean = false,
    imgSize: Int = 512,
    chunkSize: Int = 64,
    linearMag: Boolean = false,
    useMipmap: Boolean = true,
    scaleFactor: Float = 1f,
    textureLoader: TextureLoader? = null
): FontRenderer = UnicodeFontRenderer(
    Font(name, style, size.toInt()),
    size.toInt(), antiAlias, fractionalMetrics, imgSize, chunkSize, linearMag, useMipmap, scaleFactor, textureLoader
)

fun FontRenderer(
    path: String,
    size: Float,
    type: Int = Font.TRUETYPE_FONT,
    style: Int = Font.PLAIN,
    antiAlias: Boolean = true,
    fractionalMetrics: Boolean = false,
    imgSize: Int = 512,
    chunkSize: Int = 64,
    linearMag: Boolean = false,
    useMipmap: Boolean = true,
    scaleFactor: Float = 1f,
    textureLoader: TextureLoader? = null
): FontRenderer = UnicodeFontRenderer(
    Font.createFont(type, ResourceHelper.getResourceStream(path)!!)!!
        .deriveFont(size)
        .deriveFont(style),
    size.toInt(), antiAlias, fractionalMetrics, imgSize, chunkSize, linearMag, useMipmap, scaleFactor, textureLoader
)

fun LazyFontRenderer(
    path: String,
    size: Float,
    type: Int = Font.TRUETYPE_FONT,
    style: Int = Font.PLAIN,
    antiAlias: Boolean = true,
    fractionalMetrics: Boolean = false,
    imgSize: Int = 512,
    chunkSize: Int = 64,
    linearMag: Boolean = false,
    useMipmap: Boolean = true,
    scaleFactor: Float = 1f,
    textureLoader: TextureLoader? = null
): Lazy<FontRenderer> = lazy {
    FontRenderer(
        path,
        size,
        type,
        style,
        antiAlias,
        fractionalMetrics,
        imgSize,
        chunkSize,
        linearMag,
        useMipmap,
        scaleFactor,
        textureLoader
    )
}

fun LocalStaticFontRenderer(
    text: String,
    name: String,
    size: Float,
    style: Int = Font.PLAIN,
    antiAlias: Boolean = true,
    fractionalMetrics: Boolean = false,
    imgWidth: Int = 1024,
    imgHeight: Int = 64,
    linearMag: Boolean = false,
    useMipmap: Boolean = true,
    offsetPixel: Int = 0,
    scaleFactor: Float = 1f,
    textureLoader: TextureLoader? = null
): StaticFontRenderer = UnicodeStaticFontRenderer(
    text,
    Font(name, style, size.toInt()),
    antiAlias, fractionalMetrics, imgWidth, imgHeight, linearMag, useMipmap, offsetPixel, scaleFactor, textureLoader
)

fun StaticFontRenderer(
    text: String,
    path: String,
    size: Float,
    type: Int = Font.TRUETYPE_FONT,
    style: Int = Font.PLAIN,
    antiAlias: Boolean = true,
    fractionalMetrics: Boolean = false,
    imgWidth: Int = 1024,
    imgHeight: Int = 64,
    linearMag: Boolean = false,
    useMipmap: Boolean = true,
    offsetPixel: Int = 0,
    scaleFactor: Float = 1f,
    textureLoader: TextureLoader? = null
): StaticFontRenderer = UnicodeStaticFontRenderer(
    text,
    Font.createFont(type, ResourceHelper.getResourceStream(path)!!)!!
        .deriveFont(size)
        .deriveFont(style),
    antiAlias, fractionalMetrics, imgWidth, imgHeight, linearMag, useMipmap, offsetPixel, scaleFactor, textureLoader
)

fun LazyStaticFontRenderer(
    text: String,
    path: String,
    size: Float,
    type: Int = Font.TRUETYPE_FONT,
    style: Int = Font.PLAIN,
    antiAlias: Boolean = true,
    fractionalMetrics: Boolean = false,
    imgWidth: Int = 1024,
    imgHeight: Int = 64,
    linearMag: Boolean = false,
    useMipmap: Boolean = true,
    offsetPixel: Int = 0,
    scaleFactor: Float = 1f,
    textureLoader: TextureLoader? = null
): Lazy<StaticFontRenderer> = lazy {
    StaticFontRenderer(
        text,
        path,
        size,
        type,
        style,
        antiAlias,
        fractionalMetrics,
        imgWidth,
        imgHeight,
        linearMag,
        useMipmap,
        offsetPixel,
        scaleFactor,
        textureLoader
    )
}

val startTime = System.currentTimeMillis()

fun FontRenderer.drawColoredString(
    str: String,
    x: Float,
    y: Float,
    scale: Float = 1f,
    startX: Float = x,
    circleWidth: Float = getWidth(str, scale),
    timeGap: Int = 5000,
    shadowDepth: Float = 0f
): Float {
    val colors = mutableListOf<ColorRGB>()
    var currentX = x
    str.forEach {
        val offset = (currentX - startX) % circleWidth / circleWidth
        val hue = ((System.currentTimeMillis() - startTime) % timeGap) / timeGap.toFloat()
        colors.add(ColorHSB(hue - offset, 1f, 1f).toRGB())
        currentX += getWidth(it.toString(), scale)
    }
    val offset = (currentX - startX) % circleWidth / circleWidth
    val hue = ((System.currentTimeMillis() - startTime) % timeGap) / timeGap.toFloat()
    colors.add(ColorHSB(hue - offset, 1f, 1f).toRGB())
    if (shadowDepth != 0f) {
        drawGradientStringWithShadow(str, x, y, colors.toTypedArray(), scale, sliceMode = true)
    } else drawGradientString(str, x, y, colors.toTypedArray(), scale, sliceMode = true)
    return getWidth(str, scale)
}