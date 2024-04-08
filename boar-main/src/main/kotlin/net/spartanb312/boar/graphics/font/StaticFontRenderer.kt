package net.spartanb312.boar.graphics.font

import net.spartanb312.boar.graphics.cache.CacheSavable
import net.spartanb312.boar.utils.color.ColorRGB

interface StaticFontRenderer : CacheSavable {

    var absoluteWidth: Int
    var absoluteHeight: Int
    var scaleFactor: Float
    val isReady: Boolean
    fun getWidth(scale: Float = 1f): Float
    fun getHeight(scale: Float = 1f): Float
    fun _drawString(
        x: Float,
        y: Float,
        color1: ColorRGB,//RT
        color2: ColorRGB,//LT
        color3: ColorRGB,//LB
        color4: ColorRGB,//RB
        scale0: Float,
        shadow: Boolean
    )

    // Normal
    fun drawString(
        x: Float,
        y: Float,
        color: ColorRGB = ColorRGB.WHITE,
        scale: Float = 1f,
    ) = _drawString(x, y, color, color, color, color, scale, false)

    fun drawStringWithShadow(
        x: Float,
        y: Float,
        color: ColorRGB = ColorRGB.WHITE,
        scale: Float = 1f,
        shadowDepth: Float = 1f,
    ) {
        _drawString(x + shadowDepth, y + shadowDepth, color, color, color, color, scale, true)
        _drawString(x, y, color, color, color, color, scale, false)
    }

    fun drawCenteredString(
        x: Float,
        y: Float,
        color: ColorRGB = ColorRGB.WHITE,
        scale: Float = 1f,
    ) {
        val width = getWidth(scale)
        val height = this.absoluteHeight * scale * scaleFactor
        val x1 = x - width / 2f
        val y1 = y - height / 2f
        _drawString(x1, y1, color, color, color, color, scale, false)
    }

    fun drawCenteredStringWithShadow(
        x: Float,
        y: Float,
        color: ColorRGB = ColorRGB.WHITE,
        scale: Float = 1f,
        shadowDepth: Float = 1f,
    ) {
        val width = getWidth(scale)
        val height = this.absoluteHeight * scale * scaleFactor
        val x1 = x - width / 2f
        val y1 = y - height / 2f
        _drawString(x1 + shadowDepth, y1 + shadowDepth, color, color, color, color, scale, true)
        _drawString(x1, y1, color, color, color, color, scale, false)
    }

    // Gradient
    fun drawGradientString(
        x: Float,
        y: Float,
        colors: Array<ColorRGB>,
        scale: Float = 1f
    ) {
        assert(colors.size == 4) { "ColorArray size should be 4" }
        _drawString(x, y, colors[0], colors[1], colors[2], colors[3], scale, false)
    }

    fun drawGradientStringWithShadow(
        x: Float,
        y: Float,
        colors: Array<ColorRGB>,
        scale: Float = 1f,
        shadowDepth: Float = 1f,
    ) {
        assert(colors.size == 4) { "ColorArray size should be 4" }
        _drawString(x + shadowDepth, y + shadowDepth, colors[0], colors[1], colors[2], colors[3], scale, true)
        _drawString(x, y, colors[0], colors[1], colors[2], colors[3], scale, false)
    }

    fun drawCenteredGradientString(
        x: Float,
        y: Float,
        colors: Array<ColorRGB>,
        scale: Float = 1f,
    ) {
        assert(colors.size == 4) { "ColorArray size should be 4" }
        val width = getWidth(scale)
        val height = this.absoluteHeight * scale * scaleFactor
        val x1 = x - width / 2f
        val y1 = y - height / 2f
        _drawString(x1, y1, colors[0], colors[1], colors[2], colors[3], scale, false)
    }

    fun drawCenteredGradientStringWithShadow(
        x: Float,
        y: Float,
        colors: Array<ColorRGB>,
        scale: Float = 1f,
        shadowDepth: Float = 1f,
    ) {
        assert(colors.size == 4) { "ColorArray size should be 4" }
        val width = getWidth(scale)
        val height = this.absoluteHeight * scale * scaleFactor
        val x1 = x - width / 2f
        val y1 = y - height / 2f
        _drawString(x1 + shadowDepth, y1 + shadowDepth, colors[0], colors[1], colors[2], colors[3], scale, true)
        _drawString(x1, y1, colors[0], colors[1], colors[2], colors[3], scale, false)
    }

    // Horizontal
    fun drawHorizontalString(
        x: Float,
        y: Float,
        leftColor: ColorRGB = ColorRGB.WHITE,
        rightColor: ColorRGB = ColorRGB.WHITE,
        scale: Float = 1f
    ) {
        _drawString(x, y, rightColor, leftColor, leftColor, rightColor, scale, false)
    }

    fun drawHorizontalStringWithShadow(
        x: Float,
        y: Float,
        leftColor: ColorRGB = ColorRGB.WHITE,
        rightColor: ColorRGB = ColorRGB.WHITE,
        scale: Float = 1f,
        shadowDepth: Float = 1f,
    ) {
        _drawString(x + shadowDepth, y + shadowDepth, rightColor, leftColor, leftColor, rightColor, scale, true)
        _drawString(x, y, rightColor, leftColor, leftColor, rightColor, scale, false)
    }

    fun drawCenteredHorizontalString(
        x: Float,
        y: Float,
        leftColor: ColorRGB = ColorRGB.WHITE,
        rightColor: ColorRGB = ColorRGB.WHITE,
        scale: Float = 1f,
    ) {
        val width = getWidth(scale)
        val height = this.absoluteHeight * scale * scaleFactor
        val x1 = x - width / 2f
        val y1 = y - height / 2f
        _drawString(x1, y1, rightColor, leftColor, leftColor, rightColor, scale, false)
    }

    fun drawCenteredHorizontalStringWithShadow(
        x: Float,
        y: Float,
        leftColor: ColorRGB = ColorRGB.WHITE,
        rightColor: ColorRGB = ColorRGB.WHITE,
        scale: Float = 1f,
        shadowDepth: Float = 1f,
    ) {
        val width = getWidth(scale)
        val height = this.absoluteHeight * scale * scaleFactor
        val x1 = x - width / 2f
        val y1 = y - height / 2f
        _drawString(x1 + shadowDepth, y1 + shadowDepth, rightColor, leftColor, leftColor, rightColor, scale, true)
        _drawString(x1, y1, rightColor, leftColor, leftColor, rightColor, scale, false)
    }

    // Vertical
    fun drawVerticalString(
        x: Float,
        y: Float,
        topColor: ColorRGB = ColorRGB.WHITE,
        bottomColor: ColorRGB = ColorRGB.WHITE,
        scale: Float = 1f
    ) {
        _drawString(x, y, topColor, topColor, bottomColor, bottomColor, scale, false)
    }

    fun drawVerticalStringWithShadow(
        x: Float,
        y: Float,
        topColor: ColorRGB = ColorRGB.WHITE,
        bottomColor: ColorRGB = ColorRGB.WHITE,
        scale: Float = 1f,
        shadowDepth: Float = 1f,
    ) {
        _drawString(x + shadowDepth, y + shadowDepth, topColor, topColor, bottomColor, bottomColor, scale, true)
        _drawString(x, y, topColor, topColor, bottomColor, bottomColor, scale, false)
    }

    fun drawCenteredVerticalString(
        x: Float,
        y: Float,
        topColor: ColorRGB = ColorRGB.WHITE,
        bottomColor: ColorRGB = ColorRGB.WHITE,
        scale: Float = 1f,
    ) {
        val width = getWidth(scale)
        val height = this.absoluteHeight * scale * scaleFactor
        val x1 = x - width / 2f
        val y1 = y - height / 2f
        _drawString(x1, y1, topColor, topColor, bottomColor, bottomColor, scale, false)
    }

    fun drawCenteredVerticalStringWithShadow(
        x: Float,
        y: Float,
        topColor: ColorRGB = ColorRGB.WHITE,
        bottomColor: ColorRGB = ColorRGB.WHITE,
        scale: Float = 1f,
        shadowDepth: Float = 1f,
    ) {
        val width = getWidth(scale)
        val height = this.absoluteHeight * scale * scaleFactor
        val x1 = x - width / 2f
        val y1 = y - height / 2f
        _drawString(x1 + shadowDepth, y1 + shadowDepth, topColor, topColor, bottomColor, bottomColor, scale, true)
        _drawString(x1, y1, topColor, topColor, bottomColor, bottomColor, scale, false)
    }

}