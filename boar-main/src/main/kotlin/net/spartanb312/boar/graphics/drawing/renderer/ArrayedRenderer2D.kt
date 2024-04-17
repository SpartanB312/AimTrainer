package net.spartanb312.boar.graphics.drawing.renderer

import net.spartanb312.boar.graphics.GLHelper
import net.spartanb312.boar.graphics.drawing.RenderUtils
import net.spartanb312.boar.graphics.drawing.Renderer2D
import net.spartanb312.boar.graphics.drawing.VertexFormat
import net.spartanb312.boar.graphics.drawing.buffer.ArrayedVertexBuffer.buffer
import net.spartanb312.boar.utils.color.ColorRGB
import net.spartanb312.boar.utils.math.vector.Vec2f
import org.lwjgl.opengl.GL11

object ArrayedRenderer2D : Renderer2D {

    override fun drawPoint0(x: Float, y: Float, size: Float, color: ColorRGB) {
        GLHelper.pointSmooth = true
        GL11.glPointSize(size)
        GL11.GL_POINTS.buffer(VertexFormat.Pos2fColor, 1) {
            v2fc(x, y, color)
        }
        GLHelper.pointSmooth = false
    }

    override fun drawLine0(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        width: Float,
        color1: ColorRGB,
        color2: ColorRGB,
    ) {
        GLHelper.lineSmooth = true
        GL11.glLineWidth(width)
        GL11.GL_LINES.buffer(VertexFormat.Pos2fColor, 2) {
            v2fc(startX, startY, color1)
            v2fc(endX, endY, color2)
        }
        GLHelper.lineSmooth = false
    }

    override fun drawLinesStrip0(vertexArray: Array<Vec2f>, width: Float, color: ColorRGB) {
        GLHelper.lineSmooth = true
        GL11.glLineWidth(width)
        GL11.GL_LINE_STRIP.buffer(VertexFormat.Pos2fColor, vertexArray.size) {
            for (v in vertexArray) {
                v2fc(v.x, v.y, color)
            }
        }
        GLHelper.lineSmooth = false
    }

    override fun drawLinesLoop0(vertexArray: Array<Vec2f>, width: Float, color: ColorRGB) {
        GLHelper.lineSmooth = true
        GL11.glLineWidth(width)
        GL11.GL_LINE_LOOP.buffer(VertexFormat.Pos2fColor, vertexArray.size) {
            for (v in vertexArray) {
                v2fc(v.x, v.y, color)
            }
        }
        GLHelper.lineSmooth = false
    }

    override fun drawTriangle0(
        pos1X: Float,
        pos1Y: Float,
        pos2X: Float,
        pos2Y: Float,
        pos3X: Float,
        pos3Y: Float,
        color: ColorRGB,
    ) {
        GL11.GL_TRIANGLES.buffer(VertexFormat.Pos2fColor, 3) {
            v2fc(pos1X, pos1Y, color)
            v2fc(pos2X, pos2Y, color)
            v2fc(pos3X, pos3Y, color)
        }
    }

    override fun drawTriangleFan0(
        centerX: Float,
        centerY: Float,
        vertices: Array<Vec2f>,
        centerColor: ColorRGB,
        color: ColorRGB
    ) {
        GL11.GL_TRIANGLE_FAN.buffer(VertexFormat.Pos2fColor, vertices.size + 1) {
            v2fc(centerX, centerY, centerColor)
            for (v in vertices) {
                v2fc(v.x, v.y, color)
            }
        }
    }

    override fun drawTriangleOutline0(
        pos1X: Float,
        pos1Y: Float,
        pos2X: Float,
        pos2Y: Float,
        pos3X: Float,
        pos3Y: Float,
        width: Float,
        color: ColorRGB,
    ) {
        GLHelper.lineSmooth = true
        GL11.glLineWidth(width)
        GL11.GL_LINE_STRIP.buffer(VertexFormat.Pos2fColor, 3) {
            v2fc(pos1X, pos1Y, color)
            v2fc(pos2X, pos2Y, color)
            v2fc(pos3X, pos3Y, color)
        }
        GLHelper.lineSmooth = false
    }

    override fun drawRect0(startX: Float, startY: Float, endX: Float, endY: Float, color: ColorRGB) {
        GL11.GL_TRIANGLE_STRIP.buffer(VertexFormat.Pos2fColor, 4) {
            v2fc(endX, startY, color)
            v2fc(startX, startY, color)
            v2fc(endX, endY, color)
            v2fc(startX, endY, color)
        }

    }

    override fun drawGradientRect0(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        color1: ColorRGB,
        color2: ColorRGB,
        color3: ColorRGB,
        color4: ColorRGB,
    ) {
        GL11.GL_QUADS.buffer(VertexFormat.Pos2fColor, 4) {
            v2fc(endX, startY, color1)
            v2fc(startX, startY, color2)
            v2fc(startX, endY, color3)
            v2fc(endX, endY, color4)
        }

    }

    override fun drawRectOutline0(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        width: Float,
        color1: ColorRGB,
        color2: ColorRGB,
        color3: ColorRGB,
        color4: ColorRGB,
    ) {
        GLHelper.lineSmooth = true
        GL11.glLineWidth(width)
        GL11.GL_LINE_LOOP.buffer(VertexFormat.Pos2fColor, 4) {
            v2fc(endX, startY, color1)
            v2fc(startX, startY, color2)
            v2fc(startX, endY, color3)
            v2fc(endX, endY, color4)
        }
        GLHelper.lineSmooth = false

    }

    override fun drawArc0(
        centerX: Float,
        centerY: Float,
        radius: Float,
        angleRange: ClosedFloatingPointRange<Float>,
        segments: Int,
        color: ColorRGB,
    ) {
        val arcVertices = RenderUtils.getArcVertices(centerX, centerY, radius, angleRange, segments).reversed()
        GL11.GL_TRIANGLE_FAN.buffer(VertexFormat.Pos2fColor, arcVertices.size + 1) {
            v2fc(centerX, centerY, color)
            for (v in arcVertices) {
                v2fc(v.x, v.y, color)
            }
        }
    }

}