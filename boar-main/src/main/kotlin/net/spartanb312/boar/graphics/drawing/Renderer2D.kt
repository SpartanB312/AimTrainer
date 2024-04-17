package net.spartanb312.boar.graphics.drawing

import net.spartanb312.boar.utils.color.ColorRGB
import net.spartanb312.boar.utils.math.vector.Vec2f

/**
 * Basic 2D stuff
 */
interface Renderer2D {

    fun drawPoint0(x: Float, y: Float, size: Float, color: ColorRGB)

    fun drawLine0(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        width: Float,
        color1: ColorRGB,
        color2: ColorRGB,
    )

    fun drawLinesStrip0(vertexArray: Array<Vec2f>, width: Float, color: ColorRGB)

    fun drawLinesLoop0(vertexArray: Array<Vec2f>, width: Float, color: ColorRGB)

    fun drawTriangle0(
        pos1X: Float,
        pos1Y: Float,
        pos2X: Float,
        pos2Y: Float,
        pos3X: Float,
        pos3Y: Float,
        color: ColorRGB,
    )

    fun drawTriangleFan0(
        centerX: Float,
        centerY: Float,
        vertices: Array<Vec2f>,
        centerColor: ColorRGB,
        color: ColorRGB
    )

    fun drawTriangleOutline0(
        pos1X: Float,
        pos1Y: Float,
        pos2X: Float,
        pos2Y: Float,
        pos3X: Float,
        pos3Y: Float,
        width: Float,
        color: ColorRGB,
    )

    fun drawRect0(startX: Float, startY: Float, endX: Float, endY: Float, color: ColorRGB)

    fun drawGradientRect0(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        color1: ColorRGB,
        color2: ColorRGB,
        color3: ColorRGB,
        color4: ColorRGB,
    )

    fun drawRectOutline0(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        width: Float,
        color1: ColorRGB,
        color2: ColorRGB,
        color3: ColorRGB,
        color4: ColorRGB,
    )

    fun drawArc0(
        centerX: Float,
        centerY: Float,
        radius: Float,
        angleRange: ClosedFloatingPointRange<Float>,
        segments: Int,
        color: ColorRGB,
    )

}