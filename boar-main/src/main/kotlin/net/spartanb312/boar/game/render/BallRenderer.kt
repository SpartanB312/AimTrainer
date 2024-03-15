package net.spartanb312.boar.game.render

import net.spartanb312.boar.graphics.GLHelper.glMatrixScope
import net.spartanb312.boar.graphics.Sphere
import net.spartanb312.boar.graphics.drawing.VertexBuffer.buffer
import net.spartanb312.boar.graphics.drawing.VertexFormat
import net.spartanb312.boar.graphics.matrix.mulScale
import net.spartanb312.boar.graphics.matrix.mulToGL
import net.spartanb312.boar.graphics.matrix.translatef
import net.spartanb312.boar.utils.color.ColorRGB
import net.spartanb312.boar.utils.math.vector.Vec3f
import org.lwjgl.opengl.GL11

object BallRenderer {

    private val verticesMap = mutableMapOf<Int, List<Vec3f>>() // level, list
    private fun getVertices(level: Int) = verticesMap.getOrPut(level) { Sphere.generate(level, level).first }

    fun render(
        x: Float,
        y: Float,
        z: Float,
        r: Float,
        color: ColorRGB,
        outline: Boolean = false,
        outlineColor: ColorRGB = color.alpha(255),
        outlineWidth: Float = 1f,
        level: Int = 12
    ) {
        val vertices = getVertices(level)
        glMatrixScope {
            translatef(x, y, z)
                .mulScale(r, r, r)
                .mulToGL()
            if (outline) {
                GL11.glLineWidth(outlineWidth)
                GL11.GL_LINE_STRIP.buffer(VertexFormat.Pos3fColor, vertices.size * 4) {
                    vertices.forEach {
                        v3fc(it.x, it.y, it.z, outlineColor)
                    }
                }
            }
            GL11.GL_QUADS.buffer(VertexFormat.Pos3fColor, vertices.size * 4) {
                vertices.forEach {
                    v3fc(it.x, it.y, it.z, color)
                }
            }
        }
    }

}