package net.spartanb312.everett.graphics.font

import net.spartanb312.everett.graphics.cache.CacheSavable
import net.spartanb312.everett.utils.color.ColorRGB

interface FontRenderer : CacheSavable {

    var absoluteHeight: Int
    var scaleFactor: Float

    fun _getWidth(text: String): Int

    fun getHeight(scale: Float = 1f): Float = absoluteHeight * scale * this.scaleFactor

    fun getWidth(text: String, scale: Float = 1f): Float = _getWidth(text) * scale * this.scaleFactor

    fun _drawString(
        text: String,
        x: Float,
        y: Float,
        color0: ColorRGB,
        gradient: Boolean = false,
        colors: Array<ColorRGB>,
        sliceMode: Boolean = true,
        scale0: Float,
        shadow: Boolean,
    )

    @Suppress("NOTHING_TO_INLINE")
    private inline fun _drawString(
        text: String,
        x: Float,
        y: Float,
        color0: ColorRGB,
        scale0: Float,
        shadow: Boolean,
    ) = _drawString(text, x, y, color0, false, emptyArray(), false, scale0, shadow)

    @Suppress("NOTHING_TO_INLINE")
    private inline fun _drawString(
        text: String,
        x: Float,
        y: Float,
        colors: Array<ColorRGB>,
        scale0: Float,
        shadow: Boolean,
    ) = _drawString(text, x, y, colors[0], true, colors, false, scale0, shadow)

    @Suppress("NOTHING_TO_INLINE")
    private inline fun _drawString(
        text: String,
        x: Float,
        y: Float,
        colors: Array<ColorRGB>,
        sliceMode: Boolean = true,
        scale0: Float,
        shadow: Boolean
    ) = _drawString(text, x, y, colors[0], true, colors, sliceMode, scale0, shadow)

    fun drawGradientString(
        text: String,
        x: Float,
        y: Float,
        colors: Array<ColorRGB>,
        scale: Float = 1f,
        shadow: Boolean = false,
    ) {
        if (shadow) drawGradientStringWithShadow(text, x, y, colors, scale)
        _drawString(text, x, y, colors, scale, false)
    }

    fun drawGradientStringWithShadow(
        text: String,
        x: Float,
        y: Float,
        colors: Array<ColorRGB>,
        scale: Float = 1f,
        shadowDepth: Float = 1f,
    ) {
        _drawString(text, x + shadowDepth, y + shadowDepth, colors, scale, true)
        _drawString(text, x, y, colors, scale, false)
    }

    fun drawString(
        text: String,
        x: Float,
        y: Float,
        color: ColorRGB = ColorRGB.WHITE,
        scale: Float = 1f,
        shadow: Boolean = false,
    ) {
        if (shadow) drawStringWithShadow(text, x, y, color)
        else _drawString(text, x, y, color, scale, false)
    }

    fun drawString(
        text: String,
        x: Double,
        y: Double,
        color: ColorRGB = ColorRGB.WHITE,
        scale: Float = 1f,
        shadow: Boolean = false,
    ) {
        if (shadow) drawStringWithShadow(text, x, y, color)
        else _drawString(text, x.toFloat(), y.toFloat(), color, scale, false)
    }

    fun drawString(
        text: String,
        x: Int,
        y: Int,
        color: ColorRGB = ColorRGB.WHITE,
        scale: Float = 1f,
        shadow: Boolean = false,
    ) {
        if (shadow) drawStringWithShadow(text, x, y, color)
        else _drawString(text, x.toFloat(), y.toFloat(), color, scale, false)
    }

    fun drawStringWithShadow(
        text: String,
        x: Float,
        y: Float,
        color: ColorRGB = ColorRGB.WHITE,
        scale: Float = 1f,
        shadowDepth: Float = 1f,
    ) {
        _drawString(text, x + shadowDepth, y + shadowDepth, color, scale, true)
        _drawString(text, x, y, color, scale, false)
    }

    fun drawStringWithShadow(
        text: String,
        x: Double,
        y: Double,
        color: ColorRGB = ColorRGB.WHITE,
        scale: Float = 1f,
        shadowDepth: Float = 1f,
    ) {
        _drawString(text, (x + shadowDepth).toFloat(), (y + shadowDepth).toFloat(), color, scale, true)
        _drawString(text, x.toFloat(), y.toFloat(), color, scale, false)
    }

    fun drawStringWithShadow(
        text: String,
        x: Int,
        y: Int,
        color: ColorRGB = ColorRGB.WHITE,
        scale: Float = 1f,
        shadowDepth: Float = 1f,
    ) {
        _drawString(text, x + shadowDepth, y + shadowDepth, color, scale, true)
        _drawString(text, x.toFloat(), y.toFloat(), color, scale, false)
    }

    fun drawCenteredString(
        text: String,
        x: Float,
        y: Float,
        color: ColorRGB = ColorRGB.WHITE,
        scale: Float = 1f,
        shadow: Boolean = false,
    ) {
        val width = getWidth(text, scale)
        val height = this.absoluteHeight * scale * scaleFactor
        val x1 = x - width / 2f
        val y1 = y - height / 2f
        drawString(text, x1, y1, color, scale, shadow)
    }

    fun drawCenteredString(
        text: String,
        x: Double,
        y: Double,
        color: ColorRGB = ColorRGB.WHITE,
        scale: Float = 1f,
        shadow: Boolean = false,
    ) {
        val width = getWidth(text, scale)
        val height = this.absoluteHeight * scale * scaleFactor
        val x1 = x - width / 2f
        val y1 = y - height / 2f
        drawString(text, x1, y1, color, scale, shadow)
    }

    fun drawCenteredString(
        text: String,
        x: Int,
        y: Int,
        color: ColorRGB = ColorRGB.WHITE,
        scale: Float = 1f,
        shadow: Boolean = false,
    ) {
        val width = getWidth(text, scale)
        val height = this.absoluteHeight * scale * scaleFactor
        val x1 = x - width / 2f
        val y1 = y - height / 2f
        drawString(text, x1, y1, color, scale, shadow)
    }

    fun drawCenteredStringWithShadow(
        text: String,
        x: Float,
        y: Float,
        color: ColorRGB = ColorRGB.WHITE,
        scale: Float = 1f,
    ) {
        val width = getWidth(text, scale)
        val height = this.absoluteHeight * scale * scaleFactor
        val x1 = x - width / 2f
        val y1 = y - height / 2f
        drawStringWithShadow(text, x1, y1, color, scale)
    }

    fun drawCenteredStringWithShadow(
        text: String,
        x: Double,
        y: Double,
        color: ColorRGB = ColorRGB.WHITE,
        scale: Float = 1f,
    ) {
        val width = getWidth(text, scale)
        val height = this.absoluteHeight * scale * scaleFactor
        val x1 = x - width / 2f
        val y1 = y - height / 2f
        drawStringWithShadow(text, x1, y1, color, scale)
    }

    fun drawCenteredStringWithShadow(
        text: String,
        x: Int,
        y: Int,
        color: ColorRGB = ColorRGB.WHITE,
        scale: Float = 1f,
    ) {
        val width = getWidth(text, scale)
        val height = this.absoluteHeight * scale * scaleFactor
        val x1 = x - width / 2f
        val y1 = y - height / 2f
        drawStringWithShadow(text, x1, y1, color, scale)
    }

    fun drawGradientString(
        text: String,
        x: Float,
        y: Float,
        colors: Array<ColorRGB>,
        scale: Float = 1f,
        shadow: Boolean = false,
        sliceMode: Boolean = true
    ) {
        if (shadow) drawGradientStringWithShadow(text, x, y, colors, scale)
        _drawString(text, x, y, colors, sliceMode, scale, false)
    }

    fun drawGradientStringWithShadow(
        text: String,
        x: Float,
        y: Float,
        colors: Array<ColorRGB>,
        scale: Float = 1f,
        shadowDepth: Float = 1f,
        sliceMode: Boolean = true
    ) {
        _drawString(text, x + shadowDepth, y + shadowDepth, colors, sliceMode, scale, true)
        _drawString(text, x, y, colors, sliceMode, scale, false)
    }

    fun saveCache(savePath: String = "disk_cache/$serialCode/", initAllChunks: Boolean = false): Boolean

    fun initAllChunks(instantLoad: Boolean = false)

    fun initChunkForce(chunk: Int, instantLoad: Boolean = false)

    val loadedChunks: Set<Int>

}