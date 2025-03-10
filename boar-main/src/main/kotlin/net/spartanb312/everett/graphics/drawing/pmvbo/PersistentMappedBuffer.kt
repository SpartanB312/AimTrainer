package net.spartanb312.everett.graphics.drawing.pmvbo

import dev.luna5ama.kmogus.Arr
import dev.luna5ama.kmogus.asMutable
import net.spartanb312.everett.graphics.RS
import org.lwjgl.opengl.*
import java.nio.ByteBuffer

open class PersistentMappedBuffer(size: Long =64L * 1024L * 1024L)  {

    val vbo = run {
        val buffer = when {
            RS.compat.openGL45 -> GL45.glCreateBuffers()
            RS.compat.arbDirectAccess -> ARBDirectStateAccess.glCreateBuffers()
            else -> GL15.glGenBuffers()
        }
        when {
            RS.compat.openGL45 -> GL45.glNamedBufferStorage(
                buffer,
                size,
                GL44.GL_MAP_WRITE_BIT or GL44.GL_MAP_PERSISTENT_BIT or GL44.GL_MAP_COHERENT_BIT
            )

            RS.compat.arbBufferStorage -> ARBBufferStorage.glNamedBufferStorageEXT(
                buffer,
                size,
                GL44.GL_MAP_WRITE_BIT or GL44.GL_MAP_PERSISTENT_BIT or GL44.GL_MAP_COHERENT_BIT
            )

            else -> throw Exception("Your graphics card doesn't support persistent mapped buffer")
        }
        buffer
    }

    val arr = Arr.wrap(
        when {
            RS.compat.openGL45 -> GL45.glMapNamedBufferRange(
                vbo,
                0,
                size,
                GL45.GL_MAP_WRITE_BIT or GL45.GL_MAP_PERSISTENT_BIT or GL45.GL_MAP_COHERENT_BIT or GL45.GL_MAP_UNSYNCHRONIZED_BIT
            )

            RS.compat.arbDirectAccess -> ARBDirectStateAccess.glMapNamedBufferRange(
                vbo,
                0,
                size,
                GL45.GL_MAP_WRITE_BIT or GL45.GL_MAP_PERSISTENT_BIT or GL45.GL_MAP_COHERENT_BIT or GL45.GL_MAP_UNSYNCHRONIZED_BIT
            )

            RS.compat.extDirectAccess -> EXTDirectStateAccess.glMapNamedBufferRangeEXT(
                vbo,
                0,
                size,
                GL45.GL_MAP_WRITE_BIT or GL45.GL_MAP_PERSISTENT_BIT or GL45.GL_MAP_COHERENT_BIT or GL45.GL_MAP_UNSYNCHRONIZED_BIT
            )

            else -> throw Exception("Your graphics card doesn't support persistent mapped buffer")
        } as ByteBuffer
    ).asMutable()

}