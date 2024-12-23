package net.spartanb312.everett.graphics.drawing.pmvbo

import net.spartanb312.everett.graphics.GLHelper
import net.spartanb312.everett.graphics.drawing.VertexAttribute
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL32
import org.lwjgl.opengl.GL32C

/**
 * Requires OpenGL 4.4
 */
object PersistentMappedVBO : PersistentMappedBuffer() {

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