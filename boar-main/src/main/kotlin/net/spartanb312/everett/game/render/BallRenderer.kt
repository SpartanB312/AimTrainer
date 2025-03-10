package net.spartanb312.everett.game.render

import net.spartanb312.everett.game.Player
import net.spartanb312.everett.game.option.impls.VideoOption
import net.spartanb312.everett.graphics.GLDataType
import net.spartanb312.everett.graphics.GLHelper
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.Sphere
import net.spartanb312.everett.graphics.drawing.buildAttribute
import net.spartanb312.everett.graphics.drawing.pmvbo.PersistentMappedVertexBuffer.VertexMode
import net.spartanb312.everett.graphics.drawing.pmvbo.PersistentMappedVertexBuffer.draw
import net.spartanb312.everett.graphics.event.EngineLoopEvent
import net.spartanb312.everett.graphics.matrix.scalef
import net.spartanb312.everett.graphics.matrix.scope
import net.spartanb312.everett.graphics.matrix.translatef
import net.spartanb312.everett.graphics.shader.Shader
import net.spartanb312.everett.graphics.shader.glUniform
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.event.ListenerOwner
import net.spartanb312.everett.utils.event.listener
import net.spartanb312.everett.utils.math.vector.Vec3f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20

object BallRenderer : ListenerOwner() {

    private val verticesMap = mutableMapOf<Int, List<Vec3f>>() // level, list
    private fun getVertices(level: Int) = verticesMap.getOrPut(level) { Sphere.generate(level, level).first }

    data object Ball343 : VertexMode(
        buildAttribute(28) {
            float(0, 3, GLDataType.GL_FLOAT, false) // 12
            float(1, 4, GLDataType.GL_UNSIGNED_BYTE, true) // 4
            float(2, 3, GLDataType.GL_FLOAT, false) // 12
        }, Shader(
            "assets/shader/lighting/Ball.vsh",
            "assets/shader/lighting/Ball.fsh"
        )
    ) {
        val matrixUniform = shader.getUniformLocation("matrix")
        val lightPos = shader.getUniformLocation("lightPos")
        val viewPos = shader.getUniformLocation("viewPos")
        val lightColor = shader.getUniformLocation("lightColor")
    }

    init {
        listener<EngineLoopEvent.Sync.Post> { Ball343.onSync() }
        subscribe()
    }

    fun render(
        x: Float,
        y: Float,
        z: Float,
        r: Float,
        color: ColorRGB,
        outline: Boolean = false,
        outlineColor: ColorRGB = color.alpha(255),
        outlineWidth: Float = 1f,
        level: Int = 25
    ) {
        val vertices = getVertices(level)
        RS.matrixLayer.scope {
            translatef(x, y, z)
            scalef(r, r, r)
            if (VideoOption.lighting) {
                GLHelper.useProgram(Ball343.shader.id)
                GL20.glUniformMatrix4fv(Ball343.matrixUniform, false, RS.matrixLayer.matrixArray)
                Vec3f(-50, 80f, -100f).glUniform(Ball343.lightPos)
                Player.pos.glUniform(Ball343.viewPos)
                Vec3f(1f, 1f, 1f).glUniform(Ball343.lightColor)
                GL11.GL_TRIANGLES.draw(Ball343) {
                    for (index in 0 until (vertices.size / 4)) {
                        Ball343.putVertex(vertices[index * 4 + 0], vertices[index * 4 + 0], color)
                        Ball343.putVertex(vertices[index * 4 + 1], vertices[index * 4 + 1], color)
                        Ball343.putVertex(vertices[index * 4 + 2], vertices[index * 4 + 2], color)
                        Ball343.putVertex(vertices[index * 4 + 1], vertices[index * 4 + 1], color)
                        Ball343.putVertex(vertices[index * 4 + 3], vertices[index * 4 + 3], color)
                        Ball343.putVertex(vertices[index * 4 + 2], vertices[index * 4 + 2], color)
                    }
                }
            } else {
                if (outline) {
                    GL11.glLineWidth(outlineWidth)
                    GL11.GL_LINE_STRIP.draw(VertexMode.Universal) {
                        vertices.forEach {
                            universal(it.x, it.y, it.z, outlineColor)
                        }
                    }
                }
                GL11.GL_TRIANGLE_STRIP.draw(VertexMode.Universal) {
                    vertices.forEach {
                        universal(it.x, it.y, it.z, color)
                    }
                }
            }
        }
    }

    private fun VertexMode.putVertex(pos: Vec3f, normal: Vec3f, color: ColorRGB) {
        val pointer = arr.ptr
        pointer[0] = pos.x
        pointer[4] = pos.y
        pointer[8] = pos.z
        pointer[12] = color.rgba
        pointer[16] = normal.x
        pointer[20] = normal.y
        pointer[24] = normal.z
        arr += 28
        vertexSize++
    }

}