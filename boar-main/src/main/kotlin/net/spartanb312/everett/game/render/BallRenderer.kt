package net.spartanb312.everett.game.render

import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.Sphere
import net.spartanb312.everett.graphics.drawing.pmvbo.PersistentMappedVertexBuffer
import net.spartanb312.everett.graphics.drawing.pmvbo.PersistentMappedVertexBuffer.draw
import net.spartanb312.everett.graphics.matrix.scalef
import net.spartanb312.everett.graphics.matrix.scope
import net.spartanb312.everett.graphics.matrix.translatef
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.math.vector.Vec3f
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
                GL11.GL_LINE_STRIP.draw(PersistentMappedVertexBuffer.VertexMode.Universal) {
                    vertices.forEach {
                        universal(it.x, it.y, it.z, outlineColor)
                    }
                }
            }
            var count = 0
            GL11.GL_TRIANGLE_STRIP.draw(PersistentMappedVertexBuffer.VertexMode.Universal) {
                vertices.forEach {
                    // TODO: GL_TRIANGLE_STRIP
                    universal(it.x, it.y, it.z, color)
                    count++
                }
            }

            //translatef(-0.5f,-13.2f,0f)
            //scalef(7f, 7f, 7f)
            //AimTrainer.model.drawModel(this)
        }
    }

}