package net.spartanb312.everett.graphics.compat.vbo

import net.spartanb312.everett.graphics.compat.Delegate
import java.nio.ByteBuffer

interface IVBO {

    @Delegate
    fun glGenBuffers(): Int

    @Delegate
    fun glBindBuffer(target: Int, buffer: Int)

    @Delegate
    fun glBufferData(target: Int, data: ByteBuffer, usage: Int)

    @Delegate
    fun glDeleteBuffers(buffer: Int)

}