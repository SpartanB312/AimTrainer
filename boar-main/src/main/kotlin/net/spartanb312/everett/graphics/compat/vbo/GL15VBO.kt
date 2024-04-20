package net.spartanb312.everett.graphics.compat.vbo

import org.lwjgl.opengl.GL15C
import java.nio.ByteBuffer

object GL15VBO : IVBO {

    override fun glGenBuffers(): Int =
        GL15C.glGenBuffers()

    override fun glBindBuffer(target: Int, buffer: Int) =
        GL15C.glBindBuffer(target, buffer)

    override fun glBufferData(target: Int, data: ByteBuffer, usage: Int) =
        GL15C.glBufferData(target, data, usage)

    override fun glDeleteBuffers(buffer: Int) =
        GL15C.glDeleteBuffers(buffer)

}