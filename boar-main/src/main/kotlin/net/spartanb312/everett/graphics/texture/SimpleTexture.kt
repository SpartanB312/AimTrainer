package net.spartanb312.everett.graphics.texture

import net.spartanb312.everett.graphics.OpenGL.*
import net.spartanb312.everett.graphics.texture.ImageUtils.uploadImage
import net.spartanb312.everett.utils.ResourceHelper.getResourceStream
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

class SimpleTexture(img: BufferedImage, format: Int = GL_RGBA) : Texture {

    constructor(path: String, format: Int = GL_RGBA) : this(ImageIO.read(getResourceStream(path)), format)

    override val id = glGenTextures()
    override var height: Int
    override var width: Int
    override val available = true

    init {
        bindTexture()
        width = img.width
        height = img.height
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        uploadImage(img, GL_RGBA, width, height)
        unbindTexture()
    }

    override fun bindTexture() = glBindTexture(GL_TEXTURE_2D, id)
    override fun unbindTexture() = glBindTexture(GL_TEXTURE_2D, 0)
    override fun deleteTexture() = glDeleteTextures(id)

}