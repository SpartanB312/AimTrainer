package net.spartanb312.boar.graphics.texture.delegate

import net.spartanb312.boar.graphics.texture.AbstractTexture
import org.lwjgl.opengl.GL11
import java.awt.image.BufferedImage

open class LateUploadTexture(
    private val format: Int = GL11.GL_RGBA,
    private val beforeUpload: AbstractTexture.() -> Unit = {},
    private val afterUpload: AbstractTexture.() -> Unit = {}
) : DelegateTexture() {
    @Synchronized
    fun upload(
        bufferedImage: BufferedImage
    ) {
        uploadImage(bufferedImage, format, beforeUpload, afterUpload)
    }

    final override var width = 0
    final override var height = 0
}