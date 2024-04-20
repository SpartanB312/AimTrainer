package net.spartanb312.everett.graphics.drawing

import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.drawing.renderer.ArrayedRenderer2D
import net.spartanb312.everett.graphics.drawing.renderer.PMVBORenderer2D
import net.spartanb312.everett.utils.collection.mapArray
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.math.MathUtils
import net.spartanb312.everett.utils.math.toRadian
import net.spartanb312.everett.utils.math.vector.Vec2d
import net.spartanb312.everett.utils.math.vector.Vec2f
import net.spartanb312.everett.utils.math.vector.toVec2f
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

object RenderUtils : Renderer2D by if (RS.compatMode) ArrayedRenderer2D else PMVBORenderer2D {

    fun drawPoint(
        x: Double,
        y: Double,
        size: Float = 1F,
        color: ColorRGB,
    ) = drawPoint0(x.toFloat(), y.toFloat(), size, color)

    fun drawPoint(
        x: Int,
        y: Int,
        size: Float = 1F,
        color: ColorRGB,
    ) = drawPoint0(x.toFloat(), y.toFloat(), size, color)

    fun drawPoint(
        x: Float,
        y: Float,
        size: Float = 1F,
        color: ColorRGB,
    ) = drawPoint0(x, y, size, color)

    fun drawLine(
        startX: Double,
        startY: Double,
        endX: Double,
        endY: Double,
        width: Float = 1F,
        color1: ColorRGB,
        color2: ColorRGB = color1,
    ) = drawLine0(startX.toFloat(), startY.toFloat(), endX.toFloat(), endY.toFloat(), width, color1, color2)

    fun drawLine(
        startX: Int,
        startY: Int,
        endX: Int,
        endY: Int,
        width: Float = 1F,
        color1: ColorRGB,
        color2: ColorRGB = color1,
    ) = drawLine0(
        startX.toFloat(),
        startY.toFloat(),
        endX.toFloat(),
        endY.toFloat(),
        width,
        color1,
        color2
    )

    fun drawLine(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        width: Float = 1F,
        color1: ColorRGB,
        color2: ColorRGB = color1,
    ) = drawLine0(
        startX,
        startY,
        endX,
        endY,
        width,
        color1,
        color2
    )

    fun drawLine(
        start: Vec2d,
        end: Vec2d,
        width: Float = 1F,
        color1: ColorRGB,
        color2: ColorRGB = color1,
    ) = drawLine0(start.x.toFloat(), start.y.toFloat(), end.x.toFloat(), end.y.toFloat(), width, color1, color2)

    fun drawLine(
        start: Vec2f,
        end: Vec2f,
        width: Float = 1F,
        color1: ColorRGB,
        color2: ColorRGB = color1,
    ) = drawLine0(
        start.x,
        start.y,
        end.x,
        end.y,
        width,
        color1,
        color2
    )

    fun drawLinesStrip(
        vertexArray: Array<Vec2d>,
        width: Float = 1F,
        color: ColorRGB,
    ) = drawLinesStrip0(vertexArray.mapArray { it.toVec2f() }, width, color)

    fun drawLinesLoop(
        vertexArray: Array<Vec2d>,
        width: Float = 1F,
        color: ColorRGB,
    ) = drawLinesLoop0(vertexArray.mapArray { it.toVec2f() }, width, color)

    fun drawLinesStrip(
        vertexArray: Array<Vec2f>,
        width: Float = 1F,
        color: ColorRGB,
    ) = drawLinesStrip0(vertexArray, width, color)

    fun drawLinesLoop(
        vertexArray: Array<Vec2f>,
        width: Float = 1F,
        color: ColorRGB,
    ) = drawLinesLoop0(vertexArray, width, color)

    fun drawTriangle(
        pos1X: Double,
        pos1Y: Double,
        pos2X: Double,
        pos2Y: Double,
        pos3X: Double,
        pos3Y: Double,
        color: ColorRGB,
    ) = drawTriangle0(
        pos1X.toFloat(),
        pos1Y.toFloat(),
        pos2X.toFloat(),
        pos2Y.toFloat(),
        pos3X.toFloat(),
        pos3Y.toFloat(),
        color
    )

    fun drawTriangle(
        pos1X: Int,
        pos1Y: Int,
        pos2X: Int,
        pos2Y: Int,
        pos3X: Int,
        pos3Y: Int,
        color: ColorRGB,
    ) = drawTriangle0(
        pos1X.toFloat(),
        pos1Y.toFloat(),
        pos2X.toFloat(),
        pos2Y.toFloat(),
        pos3X.toFloat(),
        pos3Y.toFloat(),
        color
    )

    fun drawTriangle(
        pos1X: Float,
        pos1Y: Float,
        pos2X: Float,
        pos2Y: Float,
        pos3X: Float,
        pos3Y: Float,
        color: ColorRGB,
    ) = drawTriangle0(
        pos1X,
        pos1Y,
        pos2X,
        pos2Y,
        pos3X,
        pos3Y,
        color
    )

    fun drawTriangle(
        pos1: Vec2d,
        pos2: Vec2d,
        pos3: Vec2d,
        color: ColorRGB,
    ) = drawTriangle0(
        pos1.x.toFloat(),
        pos1.y.toFloat(),
        pos2.x.toFloat(),
        pos2.y.toFloat(),
        pos3.x.toFloat(),
        pos3.y.toFloat(),
        color
    )

    fun drawTriangle(
        pos1: Vec2f,
        pos2: Vec2f,
        pos3: Vec2f,
        color: ColorRGB,
    ) = drawTriangle0(
        pos1.x,
        pos1.y,
        pos2.x,
        pos2.y,
        pos3.x,
        pos3.y,
        color
    )

    fun drawTriangleFan(
        centerX: Double,
        centerY: Double,
        vertices: Array<Vec2f>,
        centerColor: ColorRGB,
        color: ColorRGB = centerColor
    ) = drawTriangleFan0(centerX.toFloat(), centerY.toFloat(), vertices, centerColor, color)

    fun drawTriangleFan(
        centerX: Float,
        centerY: Float,
        vertices: Array<Vec2f>,
        centerColor: ColorRGB,
        color: ColorRGB = centerColor
    ) = drawTriangleFan0(centerX, centerY, vertices, centerColor, color)

    fun drawTriangleOutline(
        pos1X: Double,
        pos1Y: Double,
        pos2X: Double,
        pos2Y: Double,
        pos3X: Double,
        pos3Y: Double,
        width: Float = 1F,
        color: ColorRGB,
    ) = drawTriangleOutline0(
        pos1X.toFloat(),
        pos1Y.toFloat(),
        pos2X.toFloat(),
        pos2Y.toFloat(),
        pos3X.toFloat(),
        pos3Y.toFloat(),
        width,
        color
    )

    fun drawTriangleOutline(
        pos1X: Int,
        pos1Y: Int,
        pos2X: Int,
        pos2Y: Int,
        pos3X: Int,
        pos3Y: Int,
        width: Float = 1F,
        color: ColorRGB,
    ) = drawTriangleOutline0(
        pos1X.toFloat(),
        pos1Y.toFloat(),
        pos2X.toFloat(),
        pos2Y.toFloat(),
        pos3X.toFloat(),
        pos3Y.toFloat(),
        width,
        color
    )

    fun drawTriangleOutline(
        pos1X: Float,
        pos1Y: Float,
        pos2X: Float,
        pos2Y: Float,
        pos3X: Float,
        pos3Y: Float,
        width: Float = 1F,
        color: ColorRGB,
    ) = drawTriangleOutline0(
        pos1X,
        pos1Y,
        pos2X,
        pos2Y,
        pos3X,
        pos3Y,
        width,
        color
    )

    fun drawTriangleOutline(
        pos1: Vec2d,
        pos2: Vec2d,
        pos3: Vec2d,
        width: Float = 1F,
        color: ColorRGB,
    ) = drawTriangleOutline0(
        pos1.x.toFloat(),
        pos1.y.toFloat(),
        pos2.x.toFloat(),
        pos2.y.toFloat(),
        pos3.x.toFloat(),
        pos3.y.toFloat(),
        width,
        color
    )

    fun drawTriangleOutline(
        pos1: Vec2f,
        pos2: Vec2f,
        pos3: Vec2f,
        width: Float = 1F,
        color: ColorRGB,
    ) = drawTriangleOutline0(
        pos1.x,
        pos1.y,
        pos2.x,
        pos2.y,
        pos3.x,
        pos3.y,
        width,
        color
    )

    fun drawRect(
        startX: Double,
        startY: Double,
        endX: Double,
        endY: Double,
        color: ColorRGB,
    ) = drawRect0(startX.toFloat(), startY.toFloat(), endX.toFloat(), endY.toFloat(), color)

    fun drawRect(
        startX: Int,
        startY: Int,
        endX: Int,
        endY: Int,
        color: ColorRGB,
    ) = drawRect0(startX.toFloat(), startY.toFloat(), endX.toFloat(), endY.toFloat(), color)

    fun drawRect(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        color: ColorRGB,
    ) = drawRect0(startX, startY, endX, endY, color)

    fun drawRect(
        start: Vec2d,
        end: Vec2d,
        color: ColorRGB,
    ) = drawRect0(start.x.toFloat(), start.y.toFloat(), end.x.toFloat(), end.y.toFloat(), color)

    fun drawRect(
        start: Vec2f,
        end: Vec2f,
        color: ColorRGB,
    ) = drawRect0(start.x, start.y, end.x, end.y, color)

    fun drawGradientRect(
        startX: Double,
        startY: Double,
        endX: Double,
        endY: Double,
        color1: ColorRGB,
        color2: ColorRGB,
        color3: ColorRGB,
        color4: ColorRGB,
    ) = drawGradientRect0(
        startX.toFloat(),
        startY.toFloat(),
        endX.toFloat(),
        endY.toFloat(),
        color1,
        color2,
        color3,
        color4
    )

    fun drawGradientRect(
        startX: Int,
        startY: Int,
        endX: Int,
        endY: Int,
        color1: ColorRGB,
        color2: ColorRGB,
        color3: ColorRGB,
        color4: ColorRGB,
    ) = drawGradientRect0(
        startX.toFloat(),
        startY.toFloat(),
        endX.toFloat(),
        endY.toFloat(),
        color1,
        color2,
        color3,
        color4
    )

    fun drawGradientRect(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        color1: ColorRGB,
        color2: ColorRGB,
        color3: ColorRGB,
        color4: ColorRGB,
    ) = drawGradientRect0(
        startX,
        startY,
        endX,
        endY,
        color1,
        color2,
        color3,
        color4
    )

    fun drawGradientRect(
        start: Vec2d,
        end: Vec2d,
        color1: ColorRGB,
        color2: ColorRGB,
        color3: ColorRGB,
        color4: ColorRGB,
    ) = drawGradientRect0(
        start.x.toFloat(),
        start.y.toFloat(),
        end.x.toFloat(),
        end.y.toFloat(),
        color1,
        color2,
        color3,
        color4
    )

    fun drawGradientRect(
        start: Vec2f,
        end: Vec2f,
        color1: ColorRGB,
        color2: ColorRGB,
        color3: ColorRGB,
        color4: ColorRGB,
    ) = drawGradientRect0(
        start.x,
        start.y,
        end.x,
        end.y,
        color1,
        color2,
        color3,
        color4
    )

    fun drawHorizontalRect(
        startX: Double,
        startY: Double,
        endX: Double,
        endY: Double,
        startColor: ColorRGB,
        endColorRGB: ColorRGB,
    ) = drawGradientRect0(
        startX.toFloat(),
        startY.toFloat(),
        endX.toFloat(),
        endY.toFloat(),
        endColorRGB,
        startColor,
        startColor,
        endColorRGB
    )

    fun drawHorizontalRect(
        startX: Int,
        startY: Int,
        endX: Int,
        endY: Int,
        startColor: ColorRGB,
        endColorRGB: ColorRGB,
    ) = drawGradientRect0(
        startX.toFloat(),
        startY.toFloat(),
        endX.toFloat(),
        endY.toFloat(),
        endColorRGB,
        startColor,
        startColor,
        endColorRGB
    )

    fun drawHorizontalRect(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        startColor: ColorRGB,
        endColorRGB: ColorRGB,
    ) = drawGradientRect0(
        startX,
        startY,
        endX,
        endY,
        endColorRGB,
        startColor,
        startColor,
        endColorRGB
    )

    fun drawHorizontalRect(
        start: Vec2d,
        end: Vec2d,
        startColor: ColorRGB,
        endColorRGB: ColorRGB,
    ) = drawGradientRect0(
        start.x.toFloat(),
        start.y.toFloat(),
        end.x.toFloat(),
        end.y.toFloat(),
        endColorRGB,
        startColor,
        startColor,
        endColorRGB
    )

    fun drawHorizontalRect(
        start: Vec2f,
        end: Vec2f,
        startColor: ColorRGB,
        endColorRGB: ColorRGB,
    ) = drawGradientRect0(
        start.x,
        start.y,
        end.x,
        end.y,
        endColorRGB,
        startColor,
        startColor,
        endColorRGB
    )

    fun drawVerticalRect(
        startX: Double,
        startY: Double,
        endX: Double,
        endY: Double,
        startColor: ColorRGB,
        endColorRGB: ColorRGB,
    ) = drawGradientRect0(
        startX.toFloat(),
        startY.toFloat(),
        endX.toFloat(),
        endY.toFloat(),
        startColor,
        startColor,
        endColorRGB,
        endColorRGB
    )

    fun drawVerticalRect(
        startX: Int,
        startY: Int,
        endX: Int,
        endY: Int,
        startColor: ColorRGB,
        endColorRGB: ColorRGB,
    ) = drawGradientRect0(
        startX.toFloat(),
        startY.toFloat(),
        endX.toFloat(),
        endY.toFloat(),
        startColor,
        startColor,
        endColorRGB,
        endColorRGB
    )

    fun drawVerticalRect(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        startColor: ColorRGB,
        endColorRGB: ColorRGB,
    ) = drawGradientRect0(
        startX,
        startY,
        endX,
        endY,
        startColor,
        startColor,
        endColorRGB,
        endColorRGB
    )

    fun drawVerticalRect(
        start: Vec2d,
        end: Vec2d,
        startColor: ColorRGB,
        endColorRGB: ColorRGB,
    ) = drawGradientRect0(
        start.x.toFloat(),
        start.y.toFloat(),
        end.x.toFloat(),
        end.y.toFloat(),
        startColor,
        startColor,
        endColorRGB,
        endColorRGB
    )

    fun drawVerticalRect(
        start: Vec2f,
        end: Vec2f,
        startColor: ColorRGB,
        endColorRGB: ColorRGB,
    ) = drawGradientRect0(
        start.x,
        start.y,
        end.x,
        end.y,
        startColor,
        startColor,
        endColorRGB,
        endColorRGB
    )

    fun drawRectOutline(
        startX: Double,
        startY: Double,
        endX: Double,
        endY: Double,
        width: Float = 1F,
        color1: ColorRGB,
        color2: ColorRGB = color1,
        color3: ColorRGB = color1,
        color4: ColorRGB = color1,
    ) = drawRectOutline0(
        startX.toFloat(),
        startY.toFloat(),
        endX.toFloat(),
        endY.toFloat(),
        width,
        color1,
        color2,
        color3,
        color4
    )

    fun drawRectOutline(
        startX: Int,
        startY: Int,
        endX: Int,
        endY: Int,
        width: Float = 1F,
        color1: ColorRGB,
        color2: ColorRGB = color1,
        color3: ColorRGB = color1,
        color4: ColorRGB = color1,
    ) = drawRectOutline0(
        startX.toFloat(),
        startY.toFloat(),
        endX.toFloat(),
        endY.toFloat(),
        width,
        color1,
        color2,
        color3,
        color4
    )

    fun drawRectOutline(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        width: Float = 1F,
        color1: ColorRGB,
        color2: ColorRGB = color1,
        color3: ColorRGB = color1,
        color4: ColorRGB = color1,
    ) = drawRectOutline0(
        startX,
        startY,
        endX,
        endY,
        width,
        color1,
        color2,
        color3,
        color4
    )

    fun drawRectOutline(
        start: Vec2d,
        end: Vec2d,
        width: Float = 1F,
        color1: ColorRGB,
        color2: ColorRGB = color1,
        color3: ColorRGB = color1,
        color4: ColorRGB = color1,
    ) = drawRectOutline0(
        start.x.toFloat(),
        start.y.toFloat(),
        end.x.toFloat(),
        end.y.toFloat(),
        width,
        color1,
        color2,
        color3,
        color4
    )

    fun drawRectOutline(
        start: Vec2f,
        end: Vec2f,
        width: Float = 1F,
        color1: ColorRGB,
        color2: ColorRGB = color1,
        color3: ColorRGB = color1,
        color4: ColorRGB = color1,
    ) = drawRectOutline0(
        start.x,
        start.y,
        end.x,
        end.y,
        width,
        color1,
        color2,
        color3,
        color4
    )

    fun drawHorizontalRectOutline(
        startX: Double,
        startY: Double,
        endX: Double,
        endY: Double,
        width: Float = 1F,
        startColor: ColorRGB,
        endColorRGB: ColorRGB,
    ) = drawRectOutline0(
        startX.toFloat(),
        startY.toFloat(),
        endX.toFloat(),
        endY.toFloat(),
        width,
        endColorRGB,
        startColor,
        startColor,
        endColorRGB
    )

    fun drawHorizontalRectOutline(
        startX: Int,
        startY: Int,
        endX: Int,
        endY: Int,
        width: Float = 1F,
        startColor: ColorRGB,
        endColorRGB: ColorRGB,
    ) = drawRectOutline0(
        startX.toFloat(),
        startY.toFloat(),
        endX.toFloat(),
        endY.toFloat(),
        width,
        endColorRGB,
        startColor,
        startColor,
        endColorRGB
    )

    fun drawHorizontalRectOutline(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        width: Float = 1F,
        startColor: ColorRGB,
        endColorRGB: ColorRGB,
    ) = drawRectOutline0(
        startX,
        startY,
        endX,
        endY,
        width,
        endColorRGB,
        startColor,
        startColor,
        endColorRGB
    )

    fun drawHorizontalRectOutline(
        start: Vec2d,
        end: Vec2d,
        width: Float = 1F,
        startColor: ColorRGB,
        endColorRGB: ColorRGB,
    ) = drawRectOutline0(
        start.x.toFloat(),
        start.y.toFloat(),
        end.x.toFloat(),
        end.y.toFloat(),
        width,
        endColorRGB,
        startColor,
        startColor,
        endColorRGB
    )

    fun drawHorizontalRectOutline(
        start: Vec2f,
        end: Vec2f,
        width: Float = 1F,
        startColor: ColorRGB,
        endColorRGB: ColorRGB,
    ) = drawRectOutline0(
        start.x,
        start.y,
        end.x,
        end.y,
        width,
        endColorRGB,
        startColor,
        startColor,
        endColorRGB
    )

    fun drawVerticalRectOutline(
        startX: Double,
        startY: Double,
        endX: Double,
        endY: Double,
        width: Float = 1F,
        startColor: ColorRGB,
        endColorRGB: ColorRGB,
    ) = drawRectOutline0(
        startX.toFloat(),
        startY.toFloat(),
        endX.toFloat(),
        endY.toFloat(),
        width,
        startColor,
        startColor,
        endColorRGB,
        endColorRGB
    )

    fun drawVerticalRectOutline(
        startX: Int,
        startY: Int,
        endX: Int,
        endY: Int,
        width: Float = 1F,
        startColor: ColorRGB,
        endColorRGB: ColorRGB,
    ) = drawRectOutline0(
        startX.toFloat(),
        startY.toFloat(),
        endX.toFloat(),
        endY.toFloat(),
        width,
        startColor,
        startColor,
        endColorRGB,
        endColorRGB
    )

    fun drawVerticalRectOutline(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        width: Float = 1F,
        startColor: ColorRGB,
        endColorRGB: ColorRGB,
    ) = drawRectOutline0(
        startX,
        startY,
        endX,
        endY,
        width,
        startColor,
        startColor,
        endColorRGB,
        endColorRGB
    )

    fun drawVerticalRectOutline(
        start: Vec2d,
        end: Vec2d,
        width: Float = 1F,
        startColor: ColorRGB,
        endColorRGB: ColorRGB,
    ) = drawRectOutline0(
        start.x.toFloat(),
        start.y.toFloat(),
        end.x.toFloat(),
        end.y.toFloat(),
        width,
        startColor,
        startColor,
        endColorRGB,
        endColorRGB
    )

    fun drawVerticalRectOutline(
        start: Vec2f,
        end: Vec2f,
        width: Float = 1F,
        startColor: ColorRGB,
        endColorRGB: ColorRGB,
    ) = drawRectOutline0(
        start.x,
        start.y,
        end.x,
        end.y,
        width,
        startColor,
        startColor,
        endColorRGB,
        endColorRGB
    )

    fun drawArc(
        centerX: Double,
        centerY: Double,
        radius: Float,
        angleRange: ClosedFloatingPointRange<Float>,
        segments: Int = 0,
        color: ColorRGB,
    ) = drawArc0(centerX.toFloat(), centerY.toFloat(), radius, angleRange, segments, color)

    fun drawArc(
        centerX: Int,
        centerY: Int,
        radius: Float,
        angleRange: ClosedFloatingPointRange<Float>,
        segments: Int = 0,
        color: ColorRGB,
    ) = drawArc0(centerX.toFloat(), centerY.toFloat(), radius, angleRange, segments, color)

    fun drawArc(
        centerX: Float,
        centerY: Float,
        radius: Float,
        angleRange: ClosedFloatingPointRange<Float>,
        segments: Int = 0,
        color: ColorRGB,
    ) = drawArc0(centerX, centerY, radius, angleRange, segments, color)

    fun drawArc(
        center: Vec2d,
        radius: Float,
        angleRange: ClosedFloatingPointRange<Float>,
        segments: Int = 0,
        color: ColorRGB,
    ) = drawArc0(center.x.toFloat(), center.y.toFloat(), radius, angleRange, segments, color)

    fun drawArc(
        center: Vec2f,
        radius: Float,
        angleRange: ClosedFloatingPointRange<Float>,
        segments: Int = 0,
        color: ColorRGB,
    ) = drawArc0(center.x, center.y, radius, angleRange, segments, color)

    fun drawArcOutline(
        centerX: Double,
        centerY: Double,
        radius: Float,
        angleRange: ClosedFloatingPointRange<Float>,
        segments: Int = 0,
        lineWidth: Float = 1f,
        color: ColorRGB,
    ) = drawLinesStrip0(
        getArcVertices(centerX.toFloat(), centerY.toFloat(), radius, angleRange, segments),
        lineWidth,
        color
    )

    fun drawArcOutline(
        centerX: Int,
        centerY: Int,
        radius: Float,
        angleRange: ClosedFloatingPointRange<Float>,
        segments: Int = 0,
        lineWidth: Float = 1f,
        color: ColorRGB,
    ) = drawLinesStrip0(
        getArcVertices(centerX.toFloat(), centerY.toFloat(), radius, angleRange, segments),
        lineWidth,
        color
    )

    fun drawArcOutline(
        centerX: Float,
        centerY: Float,
        radius: Float,
        angleRange: ClosedFloatingPointRange<Float>,
        segments: Int = 0,
        lineWidth: Float = 1f,
        color: ColorRGB,
    ) = drawLinesStrip0(
        getArcVertices(centerX.toFloat(), centerY.toFloat(), radius, angleRange, segments),
        lineWidth,
        color
    )

    fun drawArcOutline(
        center: Vec2d,
        radius: Float,
        angleRange: ClosedFloatingPointRange<Float>,
        segments: Int = 0,
        lineWidth: Float = 1f,
        color: ColorRGB,
    ) = drawLinesStrip0(
        getArcVertices(center.x.toFloat(), center.y.toFloat(), radius, angleRange, segments),
        lineWidth,
        color
    )

    fun drawArcOutline(
        center: Vec2f,
        radius: Float,
        angleRange: ClosedFloatingPointRange<Float>,
        segments: Int = 0,
        lineWidth: Float = 1f,
        color: ColorRGB,
    ) = drawLinesStrip0(
        getArcVertices(center.x, center.y, radius, angleRange, segments),
        lineWidth,
        color
    )

    fun drawArcOutline(
        vertices: Array<Vec2d>,
        lineWidth: Float = 1f,
        color: ColorRGB,
    ) = drawLinesStrip0(
        vertices.mapArray { it.toVec2f() },
        lineWidth,
        color
    )

    fun drawArcOutline(
        vertices: Array<Vec2f>,
        lineWidth: Float = 1f,
        color: ColorRGB,
    ) = drawLinesStrip0(
        vertices,
        lineWidth,
        color
    )

    fun getArcVertices(
        centerX: Float,
        centerY: Float,
        radius: Float,
        angleRange: ClosedFloatingPointRange<Float> = 0f..360f,
        segments: Int = 0,
    ): Array<Vec2f> {
        val range = max(angleRange.start, angleRange.endInclusive) - min(angleRange.start, angleRange.endInclusive)
        val seg = MathUtils.calcSegments(segments, radius, range)
        val segAngle = (range / seg.toFloat())

        return Array(seg + 1) {
            val angle = (it * segAngle + angleRange.start).toRadian()
            val unRounded = Vec2f(sin(angle), -cos(angle)).times(radius).plus(Vec2f(centerX, centerY))
            Vec2f(MathUtils.round(unRounded.x, 8), MathUtils.round(unRounded.y, 8))
        }
    }

}