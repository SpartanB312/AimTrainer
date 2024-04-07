package net.spartanb312.boar.graphics.texture

import net.spartanb312.boar.graphics.OpenGL.*
import net.spartanb312.boar.graphics.texture.delegate.LateUploadTexture
import java.awt.image.BufferedImage
import java.util.concurrent.atomic.AtomicReference

abstract class AbstractTexture : Texture {

    override val id = glGenTextures()
    var type = TextureType.DIFFUSE

    val state: AtomicReference<State> = AtomicReference(State.CREATED)
    override val available get() = state.get() == State.UPLOADED

    override fun bindTexture() {
        val state = state.get()
        when {
            state == State.UPLOADED -> glBindTexture(GL_TEXTURE_2D, id)
            state == State.CREATED && this is LateUploadTexture -> glBindTexture(GL_TEXTURE_2D, id)
            state == State.DELETED -> throw IllegalStateException("This texture has been deleted!")
            state == State.CREATED -> throw IllegalStateException("This texture has not uploaded!")
        }
    }

    override fun unbindTexture() = glBindTexture(GL_TEXTURE_2D, 0)

    override fun deleteTexture() {
        glDeleteTextures(id)
        state.set(State.DELETED)
    }

    override fun equals(other: Any?) =
        this === other || other is AbstractTexture && this.id == other.id

    override fun hashCode() = id

    fun uploadImage(
        bufferedImage: BufferedImage,
        format: Int,
        beforeUpload: AbstractTexture.() -> Unit,
        afterUpload: AbstractTexture.() -> Unit
    ) {
        if (state.get() == State.CREATED) {
            state.set(State.UPLOADED)
            width = bufferedImage.width
            height = bufferedImage.height

            useTexture {
                beforeUpload.invoke(this@AbstractTexture)
                ImageUtils.uploadImage(bufferedImage, format, width, height)
                afterUpload.invoke(this@AbstractTexture)
            }

        } else throw IllegalStateException("Texture already uploaded or deleted.")
    }

    enum class State {
        CREATED,
        UPLOADED,
        DELETED
    }
}

enum class TextureType {
    DIFFUSE,
    NORMAL,
    SPECULAR,
    DISPLACEMENT,
    AMBIENT,
    ROUGHNESS
}