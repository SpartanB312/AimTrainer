package net.spartanb312.everett.graphics.texture.delegate

import net.spartanb312.everett.graphics.texture.AbstractTexture
import org.lwjgl.opengl.GL11
import java.awt.image.BufferedImage

class InstantTexture(
    bufferedImage: BufferedImage,
    format: Int = GL11.GL_RGBA,
    beforeUpload: AbstractTexture.() -> Unit = {},
    afterUpload: AbstractTexture.() -> Unit = {}
) : DelegateTexture() {
    init {
        uploadImage(bufferedImage, format, beforeUpload, afterUpload)
    }

    override var width = 0
    override var height = 0
}