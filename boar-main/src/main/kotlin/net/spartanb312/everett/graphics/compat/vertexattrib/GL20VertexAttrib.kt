package net.spartanb312.everett.graphics.compat.vertexattrib

import org.lwjgl.opengl.GL20C
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer

object GL20VertexAttrib : IVertexAttrib {

    override fun glEnableVertexAttribArray(index: Int) =
        GL20C.glEnableVertexAttribArray(index)

    override fun glDisableVertexAttribArray(index: Int) =
        GL20C.glDisableVertexAttribArray(index)

    override fun glVertexAttribPointer(
        index: Int,
        size: Int,
        type: Int,
        normalized: Boolean,
        stride: Int,
        pointer: Long,
    ) = GL20C.glVertexAttribPointer(index, size, type, normalized, stride, pointer)

    override fun glVertexAttribPointer(
        index: Int,
        size: Int,
        type: Int,
        normalized: Boolean,
        stride: Int,
        pointer: ShortBuffer,
    ) = GL20C.glVertexAttribPointer(index, size, type, normalized, stride, pointer)

    override fun glVertexAttribPointer(
        index: Int,
        size: Int,
        type: Int,
        normalized: Boolean,
        stride: Int,
        pointer: IntBuffer,
    ) = GL20C.glVertexAttribPointer(index, size, type, normalized, stride, pointer)

    override fun glVertexAttribPointer(
        index: Int,
        size: Int,
        type: Int,
        normalized: Boolean,
        stride: Int,
        pointer: FloatBuffer,
    ) = GL20C.glVertexAttribPointer(index, size, type, normalized, stride, pointer)

}
