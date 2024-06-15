package net.spartanb312.everett.graphics.drawing.pmvbo

import dev.luna5ama.kmogus.Arr
import dev.luna5ama.kmogus.asMutable
import net.spartanb312.everett.graphics.GLHelper
import net.spartanb312.everett.graphics.drawing.VertexAttribute
import org.lwjgl.opengl.*
import java.nio.ByteBuffer

object PersistentMappedVBO  {

    private val vbo = GL45.glCreateBuffers().apply {
        GL45.glNamedBufferStorage(
            this,
            64L * 1024L * 1024L,
            GL45.GL_MAP_WRITE_BIT or GL45.GL_MAP_PERSISTENT_BIT or GL45.GL_MAP_COHERENT_BIT
        )
    }
    val arr = Arr.wrap(
        GL45.glMapNamedBufferRange(
            vbo,
            0,
            64L * 1024L * 1024L,
            GL45.GL_MAP_WRITE_BIT or GL45.GL_MAP_PERSISTENT_BIT or GL45.GL_MAP_COHERENT_BIT or GL45.GL_MAP_UNSYNCHRONIZED_BIT
        ) as ByteBuffer
    ).asMutable()

    var drawOffset = 0
    private var sync = 0L

    fun end(stride: Int) {
        drawOffset = (arr.pos / stride).toInt()
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

    fun createVao(vertexAttribute: VertexAttribute): Int {
        val vaoID = GL30.glGenVertexArrays()
        GLHelper.bindVertexArray(vaoID)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo)
        vertexAttribute.apply()
        GLHelper.bindVertexArray(0)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
        return vaoID
    }

}