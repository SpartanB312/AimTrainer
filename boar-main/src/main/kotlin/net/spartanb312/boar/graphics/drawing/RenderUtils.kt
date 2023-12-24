package net.spartanb312.boar.graphics.drawing

import net.spartanb312.boar.graphics.GLHelper
import net.spartanb312.boar.graphics.drawing.VertexBuffer.buffer
import net.spartanb312.boar.utils.collection.mapArray
import net.spartanb312.boar.utils.color.ColorRGB
import net.spartanb312.boar.utils.math.MathUtils
import net.spartanb312.boar.utils.math.vector.Vec2d
import net.spartanb312.boar.utils.math.vector.Vec2f
import net.spartanb312.boar.utils.math.vector.toVec2d
import org.lwjgl.opengl.GL11
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

object RenderUtils {

    fun drawPoint(
        x: Double,
        y: Double,
        size: Float = 1F,
        color: ColorRGB,
    ) = drawPoint0(x, y, size, color)

    fun drawPoint(
        x: Int,
        y: Int,
        size: Float = 1F,
        color: ColorRGB,
    ) = drawPoint0(x.toDouble(), y.toDouble(), size, color)

    fun drawPoint(
        x: Float,
        y: Float,
        size: Float = 1F,
        color: ColorRGB,
    ) = drawPoint0(x.toDouble(), y.toDouble(), size, color)

    fun drawLine(
        startX: Double,
        startY: Double,
        endX: Double,
        endY: Double,
        width: Float = 1F,
        color1: ColorRGB,
        color2: ColorRGB = color1,
    ) = drawLine0(startX, startY, endX, endY, width, color1, color2)

    fun drawLine(
        startX: Int,
        startY: Int,
        endX: Int,
        endY: Int,
        width: Float = 1F,
        color1: ColorRGB,
        color2: ColorRGB = color1,
    ) = drawLine0(
        startX.toDouble(),
        startY.toDouble(),
        endX.toDouble(),
        endY.toDouble(),
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
        startX.toDouble(),
        startY.toDouble(),
        endX.toDouble(),
        endY.toDouble(),
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
    ) = drawLine0(start.x, start.y, end.x, end.y, width, color1, color2)

    fun drawLine(
        start: Vec2f,
        end: Vec2f,
        width: Float = 1F,
        color1: ColorRGB,
        color2: ColorRGB = color1,
    ) = drawLine0(
        start.x.toDouble(),
        start.y.toDouble(),
        end.x.toDouble(),
        end.y.toDouble(),
        width,
        color1,
        color2
    )

    fun drawLinesStrip(
        vertexArray: Array<Vec2d>,
        width: Float = 1F,
        color: ColorRGB,
    ) = drawLinesStrip0(vertexArray, width, color)

    fun drawLinesLoop(
        vertexArray: Array<Vec2d>,
        width: Float = 1F,
        color: ColorRGB,
    ) = drawLinesLoop0(vertexArray, width, color)

    fun drawLinesStrip(
        vertexArray: Array<Vec2f>,
        width: Float = 1F,
        color: ColorRGB,
    ) = drawLinesStrip0(vertexArray.mapArray { it.toVec2d() }, width, color)

    fun drawLinesLoop(
        vertexArray: Array<Vec2f>,
        width: Float = 1F,
        color: ColorRGB,
    ) = drawLinesLoop0(vertexArray.mapArray { it.toVec2d() }, width, color)

    fun drawTriangle(
        pos1X: Double,
        pos1Y: Double,
        pos2X: Double,
        pos2Y: Double,
        pos3X: Double,
        pos3Y: Double,
        color: ColorRGB,
    ) = drawTriangle0(pos1X, pos1Y, pos2X, pos2Y, pos3X, pos3Y, color)

    fun drawTriangle(
        pos1X: Int,
        pos1Y: Int,
        pos2X: Int,
        pos2Y: Int,
        pos3X: Int,
        pos3Y: Int,
        color: ColorRGB,
    ) = drawTriangle0(
        pos1X.toDouble(),
        pos1Y.toDouble(),
        pos2X.toDouble(),
        pos2Y.toDouble(),
        pos3X.toDouble(),
        pos3Y.toDouble(),
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
        pos1X.toDouble(),
        pos1Y.toDouble(),
        pos2X.toDouble(),
        pos2Y.toDouble(),
        pos3X.toDouble(),
        pos3Y.toDouble(),
        color
    )

    fun drawTriangle(
        pos1: Vec2d,
        pos2: Vec2d,
        pos3: Vec2d,
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

    fun drawTriangle(
        pos1: Vec2f,
        pos2: Vec2f,
        pos3: Vec2f,
        color: ColorRGB,
    ) = drawTriangle0(
        pos1.x.toDouble(),
        pos1.y.toDouble(),
        pos2.x.toDouble(),
        pos2.y.toDouble(),
        pos3.x.toDouble(),
        pos3.y.toDouble(),
        color
    )

    fun drawTriangleOutline(
        pos1X: Double,
        pos1Y: Double,
        pos2X: Double,
        pos2Y: Double,
        pos3X: Double,
        pos3Y: Double,
        width: Float = 1F,
        color: ColorRGB,
    ) = drawTriangleOutline0(pos1X, pos1Y, pos2X, pos2Y, pos3X, pos3Y, width, color)

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
        pos1X.toDouble(),
        pos1Y.toDouble(),
        pos2X.toDouble(),
        pos2Y.toDouble(),
        pos3X.toDouble(),
        pos3Y.toDouble(),
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
        pos1X.toDouble(),
        pos1Y.toDouble(),
        pos2X.toDouble(),
        pos2Y.toDouble(),
        pos3X.toDouble(),
        pos3Y.toDouble(),
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
        pos1.x,
        pos1.y,
        pos2.x,
        pos2.y,
        pos3.x,
        pos3.y,
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
        pos1.x.toDouble(),
        pos1.y.toDouble(),
        pos2.x.toDouble(),
        pos2.y.toDouble(),
        pos3.x.toDouble(),
        pos3.y.toDouble(),
        width,
        color
    )

    fun drawRect(
        startX: Double,
        startY: Double,
        endX: Double,
        endY: Double,
        color: ColorRGB,
    ) = drawRect0(startX, startY, endX, endY, color)

    fun drawRect(
        startX: Int,
        startY: Int,
        endX: Int,
        endY: Int,
        color: ColorRGB,
    ) = drawRect0(startX.toDouble(), startY.toDouble(), endX.toDouble(), endY.toDouble(), color)

    fun drawRect(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        color: ColorRGB,
    ) = drawRect0(startX.toDouble(), startY.toDouble(), endX.toDouble(), endY.toDouble(), color)

    fun drawRect(
        start: Vec2d,
        end: Vec2d,
        color: ColorRGB,
    ) = drawRect0(start.x, start.y, end.x, end.y, color)

    fun drawRect(
        start: Vec2f,
        end: Vec2f,
        color: ColorRGB,
    ) = drawRect0(start.x.toDouble(), start.y.toDouble(), end.x.toDouble(), end.y.toDouble(), color)

    fun drawGradientRect(
        startX: Double,
        startY: Double,
        endX: Double,
        endY: Double,
        color1: ColorRGB,
        color2: ColorRGB,
        color3: ColorRGB,
        color4: ColorRGB,
    ) = drawGradientRect0(startX, startY, endX, endY, color1, color2, color3, color4)

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
        startX.toDouble(),
        startY.toDouble(),
        endX.toDouble(),
        endY.toDouble(),
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
        startX.toDouble(),
        startY.toDouble(),
        endX.toDouble(),
        endY.toDouble(),
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
    ) = drawGradientRect0(start.x, start.y, end.x, end.y, color1, color2, color3, color4)

    fun drawGradientRect(
        start: Vec2f,
        end: Vec2f,
        color1: ColorRGB,
        color2: ColorRGB,
        color3: ColorRGB,
        color4: ColorRGB,
    ) = drawGradientRect0(
        start.x.toDouble(),
        start.y.toDouble(),
        end.x.toDouble(),
        end.y.toDouble(),
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
    ) = drawGradientRect0(startX, startY, endX, endY, endColorRGB, startColor, startColor, endColorRGB)

    fun drawHorizontalRect(
        startX: Int,
        startY: Int,
        endX: Int,
        endY: Int,
        startColor: ColorRGB,
        endColorRGB: ColorRGB,
    ) = drawGradientRect0(
        startX.toDouble(),
        startY.toDouble(),
        endX.toDouble(),
        endY.toDouble(),
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
        startX.toDouble(),
        startY.toDouble(),
        endX.toDouble(),
        endY.toDouble(),
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
        start.x,
        start.y,
        end.x,
        end.y,
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
        start.x.toDouble(),
        start.y.toDouble(),
        end.x.toDouble(),
        end.y.toDouble(),
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
    ) = drawGradientRect0(startX, startY, endX, endY, startColor, startColor, endColorRGB, endColorRGB)

    fun drawVerticalRect(
        startX: Int,
        startY: Int,
        endX: Int,
        endY: Int,
        startColor: ColorRGB,
        endColorRGB: ColorRGB,
    ) = drawGradientRect0(
        startX.toDouble(),
        startY.toDouble(),
        endX.toDouble(),
        endY.toDouble(),
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
        startX.toDouble(),
        startY.toDouble(),
        endX.toDouble(),
        endY.toDouble(),
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
        start.x,
        start.y,
        end.x,
        end.y,
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
        start.x.toDouble(),
        start.y.toDouble(),
        end.x.toDouble(),
        end.y.toDouble(),
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
    ) = drawRectOutline0(startX, startY, endX, endY, width, color1, color2, color3, color4)

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
        startX.toDouble(),
        startY.toDouble(),
        endX.toDouble(),
        endY.toDouble(),
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
        startX.toDouble(),
        startY.toDouble(),
        endX.toDouble(),
        endY.toDouble(),
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
    ) = drawRectOutline0(start.x, start.y, end.x, end.y, width, color1, color2, color3, color4)

    fun drawRectOutline(
        start: Vec2f,
        end: Vec2f,
        width: Float = 1F,
        color1: ColorRGB,
        color2: ColorRGB = color1,
        color3: ColorRGB = color1,
        color4: ColorRGB = color1,
    ) = drawRectOutline0(
        start.x.toDouble(),
        start.y.toDouble(),
        end.x.toDouble(),
        end.y.toDouble(),
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
    ) = drawRectOutline0(startX, startY, endX, endY, width, endColorRGB, startColor, startColor, endColorRGB)

    fun drawHorizontalRectOutline(
        startX: Int,
        startY: Int,
        endX: Int,
        endY: Int,
        width: Float = 1F,
        startColor: ColorRGB,
        endColorRGB: ColorRGB,
    ) = drawRectOutline0(
        startX.toDouble(),
        startY.toDouble(),
        endX.toDouble(),
        endY.toDouble(),
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
        startX.toDouble(),
        startY.toDouble(),
        endX.toDouble(),
        endY.toDouble(),
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

    fun drawHorizontalRectOutline(
        start: Vec2f,
        end: Vec2f,
        width: Float = 1F,
        startColor: ColorRGB,
        endColorRGB: ColorRGB,
    ) = drawRectOutline0(
        start.x.toDouble(),
        start.y.toDouble(),
        end.x.toDouble(),
        end.y.toDouble(),
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
    ) = drawRectOutline0(startX, startY, endX, endY, width, startColor, startColor, endColorRGB, endColorRGB)

    fun drawVerticalRectOutline(
        startX: Int,
        startY: Int,
        endX: Int,
        endY: Int,
        width: Float = 1F,
        startColor: ColorRGB,
        endColorRGB: ColorRGB,
    ) = drawRectOutline0(
        startX.toDouble(),
        startY.toDouble(),
        endX.toDouble(),
        endY.toDouble(),
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
        startX.toDouble(),
        startY.toDouble(),
        endX.toDouble(),
        endY.toDouble(),
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

    fun drawVerticalRectOutline(
        start: Vec2f,
        end: Vec2f,
        width: Float = 1F,
        startColor: ColorRGB,
        endColorRGB: ColorRGB,
    ) = drawRectOutline0(
        start.x.toDouble(),
        start.y.toDouble(),
        end.x.toDouble(),
        end.y.toDouble(),
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
    ) = drawArc0(centerX, centerY, radius, angleRange, segments, color)

    fun drawArc(
        centerX: Int,
        centerY: Int,
        radius: Float,
        angleRange: ClosedFloatingPointRange<Float>,
        segments: Int = 0,
        color: ColorRGB,
    ) = drawArc0(centerX.toDouble(), centerY.toDouble(), radius, angleRange, segments, color)

    fun drawArc(
        centerX: Float,
        centerY: Float,
        radius: Float,
        angleRange: ClosedFloatingPointRange<Float>,
        segments: Int = 0,
        color: ColorRGB,
    ) = drawArc0(centerX.toDouble(), centerY.toDouble(), radius, angleRange, segments, color)

    fun drawArc(
        center: Vec2d,
        radius: Float,
        angleRange: ClosedFloatingPointRange<Float>,
        segments: Int = 0,
        color: ColorRGB,
    ) = drawArc0(center.x, center.y, radius, angleRange, segments, color)

    fun drawArc(
        center: Vec2f,
        radius: Float,
        angleRange: ClosedFloatingPointRange<Float>,
        segments: Int = 0,
        color: ColorRGB,
    ) = drawArc0(center.x.toDouble(), center.y.toDouble(), radius, angleRange, segments, color)

    fun drawArcOutline(
        centerX: Double,
        centerY: Double,
        radius: Float,
        angleRange: ClosedFloatingPointRange<Float>,
        segments: Int = 0,
        lineWidth: Float = 1f,
        color: ColorRGB,
    ) = drawLinesStrip0(
        getArcVertices(centerX, centerY, radius, angleRange, segments),
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
        getArcVertices(centerX.toDouble(), centerY.toDouble(), radius, angleRange, segments),
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
        getArcVertices(centerX.toDouble(), centerY.toDouble(), radius, angleRange, segments),
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
        getArcVertices(center.x, center.y, radius, angleRange, segments),
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
        getArcVertices(center.x.toDouble(), center.y.toDouble(), radius, angleRange, segments),
        lineWidth,
        color
    )

    private fun drawPoint0(x: Double, y: Double, size: Float, color: ColorRGB) {
        GLHelper.pointSmooth = true
        GL11.glPointSize(size)
        GL11.GL_POINTS.buffer(VertexFormat.Pos2dColor, 1) {
            v2dc(x, y, color)
        }
        GLHelper.pointSmooth = false
    }

    private fun drawLine0(
        startX: Double,
        startY: Double,
        endX: Double,
        endY: Double,
        width: Float,
        color1: ColorRGB,
        color2: ColorRGB,
    ) {
        GLHelper.lineSmooth = true
        GL11.glLineWidth(width)
        GL11.GL_LINES.buffer(VertexFormat.Pos2dColor, 2) {
            v2dc(startX, startY, color1)
            v2dc(endX, endY, color2)
        }
        GLHelper.lineSmooth = false
    }

    private fun drawLinesStrip0(vertexArray: Array<Vec2d>, width: Float, color: ColorRGB) {
        GLHelper.lineSmooth = true
        GL11.glLineWidth(width)
        GL11.GL_LINE_STRIP.buffer(VertexFormat.Pos2dColor, vertexArray.size) {
            for (v in vertexArray) {
                v2dc(v.x, v.y, color)
            }
        }
        GLHelper.lineSmooth = false
    }

    private fun drawLinesLoop0(vertexArray: Array<Vec2d>, width: Float, color: ColorRGB) {
        GLHelper.lineSmooth = true
        GL11.glLineWidth(width)
        GL11.GL_LINE_LOOP.buffer(VertexFormat.Pos2dColor, vertexArray.size) {
            for (v in vertexArray) {
                v2dc(v.x, v.y, color)
            }
        }
        GLHelper.lineSmooth = false
    }

    private fun drawTriangle0(
        pos1X: Double,
        pos1Y: Double,
        pos2X: Double,
        pos2Y: Double,
        pos3X: Double,
        pos3Y: Double,
        color: ColorRGB,
    ) {
        GL11.GL_TRIANGLES.buffer(VertexFormat.Pos2dColor, 3) {
            v2dc(pos1X, pos1Y, color)
            v2dc(pos2X, pos2Y, color)
            v2dc(pos3X, pos3Y, color)
        }
    }

    private fun drawTriangleOutline0(
        pos1X: Double,
        pos1Y: Double,
        pos2X: Double,
        pos2Y: Double,
        pos3X: Double,
        pos3Y: Double,
        width: Float,
        color: ColorRGB,
    ) {
        GLHelper.lineSmooth = true
        GL11.glLineWidth(width)
        GL11.GL_LINE_STRIP.buffer(VertexFormat.Pos2dColor, 3) {
            v2dc(pos1X, pos1Y, color)
            v2dc(pos2X, pos2Y, color)
            v2dc(pos3X, pos3Y, color)
        }
        GLHelper.lineSmooth = false
    }

    private fun drawRect0(startX: Double, startY: Double, endX: Double, endY: Double, color: ColorRGB) {
        GL11.GL_TRIANGLE_STRIP.buffer(VertexFormat.Pos2dColor, 4) {
            v2dc(endX, startY, color)
            v2dc(startX, startY, color)
            v2dc(endX, endY, color)
            v2dc(startX, endY, color)
        }

    }

    private fun drawGradientRect0(
        startX: Double,
        startY: Double,
        endX: Double,
        endY: Double,
        color1: ColorRGB,
        color2: ColorRGB,
        color3: ColorRGB,
        color4: ColorRGB,
    ) {
        GL11.GL_QUADS.buffer(VertexFormat.Pos2dColor, 4) {
            v2dc(endX, startY, color1)
            v2dc(startX, startY, color2)
            v2dc(startX, endY, color3)
            v2dc(endX, endY, color4)
        }

    }

    private fun drawRectOutline0(
        startX: Double,
        startY: Double,
        endX: Double,
        endY: Double,
        width: Float,
        color1: ColorRGB,
        color2: ColorRGB,
        color3: ColorRGB,
        color4: ColorRGB,
    ) {
        GLHelper.lineSmooth = true
        GL11.glLineWidth(width)
        GL11.GL_LINE_LOOP.buffer(VertexFormat.Pos2dColor, 4) {
            v2dc(endX, startY, color1)
            v2dc(startX, startY, color2)
            v2dc(startX, endY, color3)
            v2dc(endX, endY, color4)
        }
        GLHelper.lineSmooth = false

    }

    private fun drawArc0(
        centerX: Double,
        centerY: Double,
        radius: Float,
        angleRange: ClosedFloatingPointRange<Float>,
        segments: Int,
        color: ColorRGB,
    ) {
        val arcVertices = getArcVertices(centerX, centerY, radius, angleRange, segments)
        GL11.GL_TRIANGLE_FAN.buffer(VertexFormat.Pos2dColor, arcVertices.size) {
            for (v in arcVertices) {
                v2dc(v.x, v.y, color)
            }
        }
    }

    private fun getArcVertices(
        centerX: Double,
        centerY: Double,
        radius: Float,
        angleRange: ClosedFloatingPointRange<Float> = 0f..360f,
        segments: Int = 0,
    ): Array<Vec2d> {
        val range = max(angleRange.start, angleRange.endInclusive) - min(angleRange.start, angleRange.endInclusive)
        val seg = MathUtils.calcSegments(segments, radius.toDouble(), range)
        val segAngle = (range.toDouble() / seg.toDouble())

        return Array(seg + 1) {
            val angle = Math.toRadians(it * segAngle + angleRange.start.toDouble())
            val unRounded = Vec2d(sin(angle), -cos(angle)).times(radius.toDouble()).plus(Vec2d(centerX, centerY))
            Vec2d(MathUtils.round(unRounded.x, 8), MathUtils.round(unRounded.y, 8))
        }
    }

}