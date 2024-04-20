package net.spartanb312.everett.graphics.compat.vertexattrib

import net.spartanb312.everett.graphics.compat.Delegate
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer

interface IVertexAttrib {
    //GL20
    @Delegate
    fun glEnableVertexAttribArray(index: Int)

    @Delegate
    fun glDisableVertexAttribArray(index: Int)

    @Delegate
    fun glVertexAttribPointer(
        index: Int,
        size: Int,
        type: Int,
        normalized: Boolean,
        stride: Int,
        pointer: Long,
    )

    @Delegate
    fun glVertexAttribPointer(
        index: Int,
        size: Int,
        type: Int,
        normalized: Boolean,
        stride: Int,
        pointer: ShortBuffer,
    )

    @Delegate
    fun glVertexAttribPointer(
        index: Int,
        size: Int,
        type: Int,
        normalized: Boolean,
        stride: Int,
        pointer: IntBuffer,
    )

    @Delegate
    fun glVertexAttribPointer(
        index: Int,
        size: Int,
        type: Int,
        normalized: Boolean,
        stride: Int,
        pointer: FloatBuffer,
    )

}