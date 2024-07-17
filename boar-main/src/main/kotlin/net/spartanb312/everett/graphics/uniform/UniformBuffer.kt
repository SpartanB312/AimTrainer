package net.spartanb312.everett.graphics.uniform

import net.spartanb312.everett.graphics.GLObject
import net.spartanb312.everett.graphics.OpenGL
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL31
import java.nio.ByteBuffer

class UniformBuffer(private val uboBlock: UniformBlock, private val bindPos: Int) : GLObject {

    override val id = GL15.glGenBuffers()

    init {
        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, id)
        GL31.glBindBufferBase(GL31.GL_UNIFORM_BUFFER, bindPos, id)
        GL15.glBufferData(id, 32, OpenGL.GL_DYNAMIC_DRAW)
        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0)
    }

    fun uploadData(buffer: ByteBuffer) = uploadData(0, buffer)

    fun uploadData(offset: Int, buffer: ByteBuffer) {
        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, id)
        GL15.glBufferSubData(GL31.GL_UNIFORM_BUFFER, offset.toLong(), buffer)
        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0)
    }

}