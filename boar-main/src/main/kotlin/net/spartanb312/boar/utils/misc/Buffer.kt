package net.spartanb312.boar.utils.misc

import org.lwjgl.system.MemoryStack
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.DoubleBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.LongBuffer

/**
 * Created on 2/11/2023 by B312
 */
inline val memStack get() = MemoryStack.stackPush()

fun mallocInt(size: Int): IntBuffer = memStack.mallocInt(size)

fun mallocFloat(size: Int): FloatBuffer = memStack.mallocFloat(size)

fun mallocDouble(size: Int): DoubleBuffer = memStack.mallocDouble(size)

fun createDirectByteBuffer(capacity: Int): ByteBuffer {
    return ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder())
}

fun createDirectIntBuffer(capacity: Int): IntBuffer {
    return createDirectByteBuffer(capacity shl 2).asIntBuffer()
}

fun createDirectFloatBuffer(capacity: Int): FloatBuffer {
    return createDirectByteBuffer(capacity shl 2).asFloatBuffer()
}

fun createDirectDoubleBuffer(capacity: Int): DoubleBuffer {
    return createDirectByteBuffer(capacity shl 4).asDoubleBuffer()
}

fun createDirectLongBuffer(capacity: Int): LongBuffer {
    return createDirectByteBuffer(capacity shl 4).asLongBuffer()
}