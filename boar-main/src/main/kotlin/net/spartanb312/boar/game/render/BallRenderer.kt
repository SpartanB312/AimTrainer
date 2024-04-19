package net.spartanb312.boar.game.render

import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.graphics.Sphere
import net.spartanb312.boar.graphics.drawing.VertexFormat
import net.spartanb312.boar.graphics.drawing.buffer.ArrayedVertexBuffer.buffer
import net.spartanb312.boar.graphics.drawing.buffer.PersistentMappedVertexBuffer
import net.spartanb312.boar.graphics.drawing.buffer.PersistentMappedVertexBuffer.draw
import net.spartanb312.boar.graphics.matrix.scalef
import net.spartanb312.boar.graphics.matrix.scope
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
        level: Int = 13
    ) {
        val vertices = getVertices(level)
        RS.matrixLayer.scope {
            translatef(x, y, z)
            scalef(r, r, r)
            if (outline) {
                GL11.glLineWidth(outlineWidth)
                if (RS.compatMode) GL11.GL_LINE_STRIP.buffer(VertexFormat.Pos3fColor, vertices.size * 4) {
                    vertices.forEach {
                        v3fc(it.x, it.y, it.z, outlineColor)
                    }
                } else GL11.GL_LINE_STRIP.draw(PersistentMappedVertexBuffer.VertexMode.Universe) {
                    vertices.forEach {
                        universe(it.x, it.y, it.z, outlineColor)
                    }
                }
            }
            var count = 0
            if (RS.compatMode) GL11.GL_QUADS.buffer(VertexFormat.Pos3fColor, vertices.size * 4) {
                vertices.forEach {
                    //if (count !in (12 * 4)..(12 * 4 + 3))
                    v3fc(it.x, it.y, it.z, color)
                    count++
                }
            } else GL11.GL_QUADS.draw(PersistentMappedVertexBuffer.VertexMode.Universe) {
                vertices.forEach {
                    //if (count !in (12 * 4)..(12 * 4 + 3))
                    universe(it.x, it.y, it.z, color)
                    count++
                }
            }
        }
    }

}