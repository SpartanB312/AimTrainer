package net.spartanb312.boar.graphics.compat.vertexattrib

import org.lwjgl.opengl.ARBVertexShader
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer

object ARBVertexAttrib : IVertexAttrib {

    override fun glEnableVertexAttribArray(index: Int) =
        ARBVertexShader.glEnableVertexAttribArrayARB(index)

    override fun glDisableVertexAttribArray(index: Int) =
        ARBVertexShader.glDisableVertexAttribArrayARB(index)

    override fun glVertexAttribPointer(
        index: Int,
        size: Int,
        type: Int,
        normalized: Boolean,
        stride: Int,
        pointer: Long,
    ) = ARBVertexShader.glVertexAttribPointerARB(index, size, type, normalized, stride, pointer)

    override fun glVertexAttribPointer(
        index: Int,
        size: Int,
        type: Int,
        normalized: Boolean,
        stride: Int,
        pointer: ShortBuffer,
    ) = ARBVertexShader.glVertexAttribPointerARB(index, size, type, normalized, stride, pointer)

    override fun glVertexAttribPointer(
        index: Int,
        size: Int,
        type: Int,
        normalized: Boolean,
        stride: Int,
        pointer: IntBuffer,
    ) = ARBVertexShader.glVertexAttribPointerARB(index, size, type, normalized, stride, pointer)

    override fun glVertexAttribPointer(
        index: Int,
        size: Int,
        type: Int,
        normalized: Boolean,
        stride: Int,
        pointer: FloatBuffer,
    ) = ARBVertexShader.glVertexAttribPointerARB(index, size, type, normalized, stride, pointer)

}