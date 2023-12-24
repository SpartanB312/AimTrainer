package net.spartanb312.boar.graphics.compat.vertexattrib

import org.lwjgl.opengl.GL20
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer

object GL20VertexAttrib : IVertexAttrib {

    override fun glEnableVertexAttribArray(index: Int) =
        GL20.glEnableVertexAttribArray(index)

    override fun glDisableVertexAttribArray(index: Int) =
        GL20.glDisableVertexAttribArray(index)

    override fun glVertexAttribPointer(
        index: Int,
        size: Int,
        type: Int,
        normalized: Boolean,
        stride: Int,
        pointer: Long,
    ) = GL20.glVertexAttribPointer(index, size, type, normalized, stride, pointer)

    override fun glVertexAttribPointer(
        index: Int,
        size: Int,
        type: Int,
        normalized: Boolean,
        stride: Int,
        pointer: ShortBuffer,
    ) = GL20.glVertexAttribPointer(index, size, type, normalized, stride, pointer)

    override fun glVertexAttribPointer(
        index: Int,
        size: Int,
        type: Int,
        normalized: Boolean,
        stride: Int,
        pointer: IntBuffer,
    ) = GL20.glVertexAttribPointer(index, size, type, normalized, stride, pointer)

    override fun glVertexAttribPointer(
        index: Int,
        size: Int,
        type: Int,
        normalized: Boolean,
        stride: Int,
        pointer: FloatBuffer,
    ) = GL20.glVertexAttribPointer(index, size, type, normalized, stride, pointer)

}
