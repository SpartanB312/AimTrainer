package net.spartanb312.boar.graphics.compat.vbo

import org.lwjgl.opengl.GL15
import java.nio.ByteBuffer

object GL15VBO : IVBO {

    override fun glGenBuffers(): Int =
        GL15.glGenBuffers()

    override fun glBindBuffer(target: Int, buffer: Int) =
        GL15.glBindBuffer(target, buffer)

    override fun glBufferData(target: Int, data: ByteBuffer, usage: Int) =
        GL15.glBufferData(target, data, usage)

    override fun glDeleteBuffers(buffer: Int) =
        GL15.glDeleteBuffers(buffer)

}