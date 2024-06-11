package net.spartanb312.everett.graphics.drawing.pmvbo

import dev.luna5ama.kmogus.Arr
import dev.luna5ama.kmogus.asMutable
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.drawing.VertexAttribute
import net.spartanb312.everett.graphics.drawing.VertexFormat
import net.spartanb312.everett.graphics.matrix.MatrixLayerStack
import net.spartanb312.everett.graphics.shader.Shader
import net.spartanb312.everett.utils.color.ColorRGB
import org.lwjgl.opengl.*
import java.nio.ByteBuffer

/**
 * Requires OpenGL 4.5
 */
object PersistentMappedVertexBuffer {

    fun onSync() {
        VertexMode.values.forEach { it.onSync() }
    }

    open class VertexMode(val format: VertexAttribute, val shader: Shader) {
        companion object {
            val values = listOf(Pos2fColor, Pos3fColor, Pos2fColorTex, Pos3fColorTex, Universal)
        }

        data object Pos2fColor : VertexMode(
            VertexFormat.Pos2fColor.attribute, Shader(
                "assets/shader/general/Pos2fColor.vsh",
                "assets/shader/general/Pos2fColor.fsh"
            )
        )

        data object Pos3fColor : VertexMode(
            VertexFormat.Pos3fColor.attribute, Shader(
                "assets/shader/general/Pos3fColor.vsh",
                "assets/shader/general/Pos3fColor.fsh"
            )
        )

        data object Pos2fColorTex : VertexMode(
            VertexFormat.Pos2fColorTex.attribute, Shader(
                "assets/shader/general/Pos2fColorTex.vsh",
                "assets/shader/general/Pos2fColorTex.fsh"
            ).apply {
                GL20.glUniform1i(getUniformLocation("texture"), 0)
            }
        )

        data object Pos3fColorTex : VertexMode(
            VertexFormat.Pos3fColorTex.attribute, Shader(
                "assets/shader/general/Pos3fColorTex.vsh",
                "assets/shader/general/Pos3fColorTex.fsh"
            ).apply
            {
                GL20.glUniform1i(getUniformLocation("texture"), 0)
            }
        )

        data object Pos3fTex : VertexMode(
            VertexFormat.Pos3fTex.attribute, Shader(
                "assets/shader/general/Pos3fTex.vsh",
                "assets/shader/general/Pos3fTex.fsh"
            ).apply {
                GL20.glUniform1i(getUniformLocation("texture"), 0)
            }
        )

        data object Universal : VertexMode(
            VertexFormat.Pos3fColorTex.attribute, Shader(
                "assets/shader/general/UniversalDraw.vsh",
                "assets/shader/general/UniversalDraw.fsh"
            ).apply {
                GL20.glUniform1i(getUniformLocation("texture"), 0)
            }
        )

        private val matrixUniform = shader.getUniformLocation("matrix")
        private var currentCheckID = 0L

        fun updateMatrix(stack: MatrixLayerStack) {
            val checkID = stack.checkID
            if (checkID != currentCheckID) {
                currentCheckID = checkID
                GL20.glUniformMatrix4fv(matrixUniform, false, stack.matrixArray)
            }
        }

        private val vbo = GL45.glCreateBuffers().apply {
            GL45.glNamedBufferStorage(
                this,
                64L * 1024L * 1024L,
                GL45.GL_MAP_WRITE_BIT or GL45.GL_MAP_PERSISTENT_BIT or GL45.GL_MAP_COHERENT_BIT
            )
        }
        private val arr = Arr.wrap(
            GL45.glMapNamedBufferRange(
                vbo,
                0,
                64L * 1024L * 1024L,
                GL45.GL_MAP_WRITE_BIT or GL45.GL_MAP_PERSISTENT_BIT or GL45.GL_MAP_COHERENT_BIT or GL45.GL_MAP_UNSYNCHRONIZED_BIT
            ) as ByteBuffer
        ).asMutable()

        private var drawOffset = 0
        private var sync = 0L

        private fun end(stride: Int) {
            drawOffset = (arr.pos / stride).toInt()
            // println("Stride $stride, $drawOffset")
        }

        fun onSync() {
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
                GL32C.glDeleteSync(sync)
                sync = 0L
                arr.pos = 0L
                drawOffset = 0
            }
        }

        private var vertexSize = 0
        private val vao = createVao(format, vbo)
        private val stride = format.stride

        fun universal(posX: Float, posY: Float, color: ColorRGB) {
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

        fun universal(posX: Float, posY: Float, u: Float, v: Float, color: ColorRGB) {
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

        fun universal(posX: Float, posY: Float, posZ: Float, color: ColorRGB) {
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

        fun universal(posX: Float, posY: Float, posZ: Float, u: Float, v: Float, color: ColorRGB) {
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
            GL30.glBindVertexArray(vertexMode.vao)
            GL11C.glDrawArrays(mode, drawOffset, vertexSize)
            end(vertexMode.stride)
            vertexSize = 0
        }
    }

    fun createVao(vertexAttribute: VertexAttribute, vbo: Int): Int {
        val vaoID = GL30.glGenVertexArrays()
        GL30.glBindVertexArray(vaoID)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo)
        vertexAttribute.apply()
        GL30.glBindVertexArray(0)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
        return vaoID
    }

    fun Int.draw(vertexMode: VertexMode, shader: Shader = vertexMode.shader, block: VertexMode.() -> Unit) {
        vertexMode.block()
        vertexMode.draw(vertexMode, shader, this)
    }

}