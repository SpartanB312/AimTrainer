package net.spartanb312.everett.graphics.drawing.renderer

import net.spartanb312.everett.graphics.GLHelper
import net.spartanb312.everett.graphics.drawing.RenderUtils
import net.spartanb312.everett.graphics.drawing.Renderer2D
import net.spartanb312.everett.graphics.drawing.buffer.PersistentMappedVertexBuffer
import net.spartanb312.everett.graphics.drawing.buffer.PersistentMappedVertexBuffer.draw
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.math.vector.Vec2f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.GL_POINTS

object PMVBORenderer2D : Renderer2D {

    override fun drawPoint0(x: Float, y: Float, size: Float, color: ColorRGB) {
        GLHelper.pointSmooth = true
        GL11.glPointSize(size)
        GL_POINTS.draw(PersistentMappedVertexBuffer.VertexMode.Universe) {
            universe(x, y, color)
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
        GL11.GL_LINES.draw(PersistentMappedVertexBuffer.VertexMode.Universe) {
            universe(startX, startY, color1)
            universe(endX, endY, color2)
        }
        GLHelper.lineSmooth = false
    }

    override fun drawLinesStrip0(vertexArray: Array<Vec2f>, width: Float, color: ColorRGB) {
        GLHelper.lineSmooth = true
        GL11.glLineWidth(width)
        GL11.GL_LINE_STRIP.draw(PersistentMappedVertexBuffer.VertexMode.Universe) {
            for (v in vertexArray) {
                universe(v.x, v.y, color)
            }
        }
        GLHelper.lineSmooth = false
    }

    override fun drawLinesLoop0(vertexArray: Array<Vec2f>, width: Float, color: ColorRGB) {
        GLHelper.lineSmooth = true
        GL11.glLineWidth(width)
        GL11.GL_LINE_LOOP.draw(PersistentMappedVertexBuffer.VertexMode.Universe) {
            for (v in vertexArray) {
                universe(v.x, v.y, color)
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
        GL11.GL_TRIANGLES.draw(PersistentMappedVertexBuffer.VertexMode.Universe) {
            universe(pos1X, pos1Y, color)
            universe(pos2X, pos2Y, color)
            universe(pos3X, pos3Y, color)
        }
    }

    override fun drawTriangleFan0(
        centerX: Float,
        centerY: Float,
        vertices: Array<Vec2f>,
        centerColor: ColorRGB,
        color: ColorRGB
    ) {
        GL11.GL_TRIANGLE_FAN.draw(PersistentMappedVertexBuffer.VertexMode.Universe) {
            universe(centerX, centerY, centerColor)
            for (v in vertices) {
                universe(v.x, v.y, color)
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
        GL11.GL_LINE_STRIP.draw(PersistentMappedVertexBuffer.VertexMode.Universe) {
            universe(pos1X, pos1Y, color)
            universe(pos2X, pos2Y, color)
            universe(pos3X, pos3Y, color)
        }
        GLHelper.lineSmooth = false
    }

    override fun drawRect0(startX: Float, startY: Float, endX: Float, endY: Float, color: ColorRGB) {
        GL11.GL_TRIANGLE_STRIP.draw(PersistentMappedVertexBuffer.VertexMode.Universe) {
            universe(endX, startY, color)
            universe(startX, startY, color)
            universe(endX, endY, color)
            universe(startX, endY, color)
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
        GL11.GL_TRIANGLE_STRIP.draw(PersistentMappedVertexBuffer.VertexMode.Universe) {
            universe(endX, startY, color1)
            universe(startX, startY, color2)
            universe(endX, endY, color4)
            universe(startX, endY, color3)
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
        GL11.GL_LINE_LOOP.draw(PersistentMappedVertexBuffer.VertexMode.Universe) {
            universe(endX, startY, color1)
            universe(startX, startY, color2)
            universe(startX, endY, color3)
            universe(endX, endY, color4)
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
        GL11.GL_TRIANGLE_FAN.draw(PersistentMappedVertexBuffer.VertexMode.Universe) {
            universe(centerX, centerY, color)
            for (v in arcVertices) {
                universe(v.x, v.y, color)
            }
        }
    }

}