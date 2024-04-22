package net.spartanb312.everett.graphics.drawing.buffer

import dev.luna5ama.kmogus.Arr
import dev.luna5ama.kmogus.asMutable
import net.spartanb312.everett.graphics.GLHelper
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.drawing.VertexAttribute
import net.spartanb312.everett.graphics.drawing.VertexFormat
import net.spartanb312.everett.graphics.matrix.MatrixLayerStack
import net.spartanb312.everett.graphics.shader.Shader
import net.spartanb312.everett.utils.color.ColorRGB
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL11C.glDrawArrays
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30C.glBindVertexArray
import java.nio.ByteBuffer

/**
 * Requires OpenGL 4.5
 */
object PersistentMappedVertexBuffer {

    private val usingPMVBP = RS.compat.openGL45 && !RS.compatMode

    fun onSync() {
        VertexMode.values.forEach { it.onSync() }
    }

    sealed class VertexMode(val format: VertexFormat, val shader: Shader) {
        companion object {
            val values = listOf(Pos2fColor, Pos3fColor, Pos2fColorTex, Pos3fColorTex, Universe)
        }

        data object Pos2fColor : VertexMode(
            VertexFormat.Pos2fColor, Shader(
                "assets/shader/general/450/Pos2fColor.vsh",
                "assets/shader/general/450/Pos2fColor.fsh"
            )
        )

        data object Pos3fColor : VertexMode(
            VertexFormat.Pos3fColor, Shader(
                "assets/shader/general/450/Pos3fColor.vsh",
                "assets/shader/general/450/Pos3fColor.fsh"
            )
        )

        data object Pos2fColorTex : VertexMode(
            VertexFormat.Pos2fColorTex, Shader(
                "assets/shader/general/450/Pos2fColorTex.vsh",
                "assets/shader/general/450/Pos2fColorTex.fsh"
            ).apply {
                glUniform1i(glGetUniformLocation(id, "texture"), 0)
            }
        )

        data object Pos3fColorTex : VertexMode(
            VertexFormat.Pos3fColorTex, Shader(
                "assets/shader/general/450/Pos3fColorTex.vsh",
                "assets/shader/general/450/Pos3fColorTex.fsh"
            ).apply
            {
                glUniform1i(glGetUniformLocation(id, "texture"), 0)
            }
        )

        data object Pos3fTex : VertexMode(
            VertexFormat.Pos3fTex, Shader(
                "assets/shader/general/450/Pos3fTex.vsh",
                "assets/shader/general/450/Pos3fTex.fsh"
            ).apply {
                glUniform1i(glGetUniformLocation(id, "texture"), 0)
            }
        )

        data object Universe : VertexMode(
            VertexFormat.Pos3fColorTex, Shader(
                "assets/shader/general/450/UniverseDraw.vsh",
                "assets/shader/general/450/UniverseDraw.fsh"
            ).apply {
                glUniform1i(glGetUniformLocation(id, "texture"), 0)
            }
        )

        private val matrixUniform = glGetUniformLocation(shader.id, "matrix")
        private var currentCheckID = 0L

        fun updateMatrix(stack: MatrixLayerStack) {
            val checkID = stack.checkID
            if (checkID != currentCheckID) {
                currentCheckID = checkID
                glUniformMatrix4fv(matrixUniform, false, stack.matrixArray)
            }
        }

        private val vbo = GL45C.glCreateBuffers().apply {
            GL45C.glNamedBufferStorage(
                this,
                64L * 1024L * 1024L,
                GL32.GL_MAP_WRITE_BIT or GL44.GL_MAP_PERSISTENT_BIT or GL44.GL_MAP_COHERENT_BIT
            )
        }
        private val arr = Arr.wrap(
            GL45C.glMapNamedBufferRange(
                vbo,
                0,
                64L * 1024L * 1024L,
                GL32.GL_MAP_WRITE_BIT or GL44.GL_MAP_PERSISTENT_BIT or GL44.GL_MAP_COHERENT_BIT or GL32.GL_MAP_UNSYNCHRONIZED_BIT
            ) as ByteBuffer
        ).asMutable()

        private var drawOffset = 0
        private var sync = 0L

        private fun end(stride: Int) {
            drawOffset = (arr.pos / stride).toInt()
            // println("Stride $stride, $drawOffset")
        }

        fun onSync() {
            if (!usingPMVBP) return
            if (sync == 0L) {
                if (arr.pos >= arr.len / 2) {
                    sync = GL32C.glFenceSync(GL32.GL_SYNC_GPU_COMMANDS_COMPLETE, 0)
                }
            } else if (IntArray(1).apply {
                    GL32C.glGetSynciv(
                        sync,
                        GL32.GL_SYNC_STATUS,
                        IntArray(1),
                        this
                    )
                }[0] == GL32.GL_SIGNALED) {
                GL32.glDeleteSync(sync)
                sync = 0L
                arr.pos = 0L
                drawOffset = 0
            }
        }

        private var vertexSize = 0
        private val vao = createVao(format.attribute, vbo)
        private val stride = format.totalLength

        fun universe(posX: Float, posY: Float, color: ColorRGB) {
            val pointer = arr.ptr
            pointer[0] = posX
            pointer[4] = posY
            pointer[8] = 0f
            pointer[12] = color.rgba
            pointer[16] = -114514f
            pointer[20] = -114514f
            arr += 24
            vertexSize++
        }

        fun universe(posX: Float, posY: Float, u: Float, v: Float, color: ColorRGB) {
            val pointer = arr.ptr
            pointer[0] = posX
            pointer[4] = posY
            pointer[8] = 0f
            pointer[12] = color.rgba
            pointer[16] = u
            pointer[20] = v
            arr += 24
            vertexSize++
        }

        fun universe(posX: Float, posY: Float, posZ: Float, color: ColorRGB) {
            val pointer = arr.ptr
            pointer[0] = posX
            pointer[4] = posY
            pointer[8] = posZ
            pointer[12] = color.rgba
            pointer[16] = -114514f
            pointer[20] = -114514f
            arr += 24
            vertexSize++
        }

        fun universe(posX: Float, posY: Float, posZ: Float, u: Float, v: Float, color: ColorRGB) {
            val pointer = arr.ptr
            pointer[0] = posX
            pointer[4] = posY
            pointer[8] = posZ
            pointer[12] = color.rgba
            pointer[16] = u
            pointer[20] = v
            arr += 24
            vertexSize++
        }

        fun putVertex(posX: Float, posY: Float, u: Float, v: Float, color: ColorRGB) {
            val pointer = arr.ptr
            pointer[0] = posX
            pointer[4] = posY
            pointer[8] = color.rgba
            pointer[12] = u
            pointer[16] = v
            arr += 20
            vertexSize++
        }

        fun vert(posX: Float, posY: Float, posZ: Float, u: Float, v: Float) {
            val pointer = arr.ptr
            pointer[0] = posX
            pointer[4] = posY
            pointer[8] = posZ
            pointer[12] = u
            pointer[16] = v
            arr += 20
            vertexSize++
        }

        fun putVertex(posX: Float, posY: Float, posZ: Float, u: Float, v: Float, color: ColorRGB) {
            val pointer = arr.ptr
            pointer[0] = posX
            pointer[4] = posY
            pointer[8] = posZ
            pointer[12] = color.rgba
            pointer[16] = u
            pointer[20] = v
            arr += 24
            vertexSize++
        }

        fun putVertex(posX: Float, posY: Float, posZ: Float, color: ColorRGB) {
            val pointer = arr.ptr
            pointer[0] = posX
            pointer[4] = posY
            pointer[8] = posZ
            pointer[12] = color.rgba
            arr += 16
            vertexSize++
        }

        fun putVertex(posX: Float, posY: Float, color: ColorRGB) {
            val pointer = arr.ptr
            pointer[0] = posX
            pointer[4] = posY
            pointer[8] = color.rgba
            arr += 12
            vertexSize++
        }

        fun draw(vertexMode: VertexMode, shader: Shader, mode: Int) {
            if (vertexSize == 0) return
            shader.bind()
            vertexMode.updateMatrix(RS.matrixLayer)
            GLHelper.glBindVertexArray(vertexMode.vao)
            glDrawArrays(mode, drawOffset, vertexSize)
            end(vertexMode.stride)
            glBindVertexArray(0)
            vertexSize = 0
        }
    }

    fun createVao(vertexAttribute: VertexAttribute, vbo: Int): Int {
        val vaoID = GL45C.glCreateVertexArrays()
        GL32.glBindVertexArray(vaoID)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo)
        vertexAttribute.apply()
        GL32.glBindVertexArray(0)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
        return vaoID
    }

    fun Int.draw(vertexMode: VertexMode, shader: Shader = vertexMode.shader, block: VertexMode.() -> Unit) {
        vertexMode.block()
        vertexMode.draw(vertexMode, shader, this)
    }

}