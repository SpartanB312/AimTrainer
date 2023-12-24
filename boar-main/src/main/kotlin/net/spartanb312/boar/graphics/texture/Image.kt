package net.spartanb312.boar.graphics.texture

import net.spartanb312.boar.graphics.OpenGL
import net.spartanb312.boar.utils.ResourceHelper
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

class Image(
    val texture: Texture,
    val width: Int,
    val height: Int
) {
    constructor(
        img: BufferedImage,
        format: Int = OpenGL.GL_RGBA,
        levels: Int = 3,
        useMipmap: Boolean = true,
        qualityLevel: Int = 2
    ) : this(MipmapTexture(img, format, levels, useMipmap, qualityLevel), img.width, img.height)

    constructor(
        path: String,
        format: Int = OpenGL.GL_RGBA,
        levels: Int = 3,
        useMipmap: Boolean = true,
        qualityLevel: Int = 2
    ) : this(ImageIO.read(ResourceHelper.getResourceStream(path)), format, levels, useMipmap, qualityLevel)

    val aspect = width.toFloat() / height.toFloat()
    fun drawImage(x: Float, y: Float, scale: Float = 1f) {
        texture.drawTexture(x, y, x + width * scale, y + height * scale)
    }
}