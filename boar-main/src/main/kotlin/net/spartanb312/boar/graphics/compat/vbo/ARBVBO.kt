package net.spartanb312.boar.graphics.compat.vbo

import org.lwjgl.opengl.ARBVertexBufferObject
import java.nio.ByteBuffer

object ARBVBO : IVBO {

    override fun glGenBuffers(): Int =
        ARBVertexBufferObject.glGenBuffersARB()

    override fun glBindBuffer(target: Int, buffer: Int) =
        ARBVertexBufferObject.glBindBufferARB(target, buffer)

    override fun glBufferData(target: Int, data: ByteBuffer, usage: Int) =
        ARBVertexBufferObject.glBufferDataARB(target, data, usage)

    override fun glDeleteBuffers(buffer: Int) =
        ARBVertexBufferObject.glDeleteBuffersARB(buffer)

}