package net.spartanb312.everett.graphics.texture

import net.spartanb312.everett.graphics.GLHelper
import net.spartanb312.everett.graphics.OpenGL.*
import net.spartanb312.everett.graphics.texture.delegate.DelegateTexture
import net.spartanb312.everett.graphics.texture.delegate.InstantTexture
import net.spartanb312.everett.graphics.texture.delegate.LateUploadTexture
import net.spartanb312.everett.utils.ResourceHelper
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

class MipmapTexture(val delegate: DelegateTexture) : Texture by delegate {

    constructor(
        img: BufferedImage,
        format: Int = GL_RGBA,
        levels: Int = 3,
        useMipmap: Boolean = true,
        qualityLevel: Int = 2,
    ) : this(instant(img, format, levels, useMipmap, qualityLevel))

    constructor(
        path: String,
        format: Int = GL_RGBA,
        levels: Int = 3,
        useMipmap: Boolean = true,
        qualityLevel: Int = 2,
    ) : this(instant(ImageIO.read(ResourceHelper.getResourceStream(path)!!), format, levels, useMipmap, qualityLevel))

    companion object {

        @JvmStatic
        fun instant(
            img: BufferedImage,
            format: Int = GL_RGBA,
            levels: Int = 3,
            useMipmap: Boolean = true,
            qualityLevel: Int = 2,
        ): InstantTexture = InstantTexture(
            img, format,
            beforeUpload = beforeUpload(levels, useMipmap, qualityLevel),
            afterUpload = afterUpload(useMipmap)
        )

        @JvmStatic
        fun lateUpload(
            format: Int = GL_RGBA,
            levels: Int = 3,
            useMipmap: Boolean = true,
            qualityLevel: Int = 2,
        ): LateUploadTexture = LateUploadTexture(
            format,
            beforeUpload = beforeUpload(levels, useMipmap, qualityLevel),
            afterUpload = afterUpload(useMipmap)
        )

        @JvmStatic
        fun beforeUpload(
            levels: Int = 3,
            useMipmap: Boolean = true,
            qualityLevel: Int = 2
        ): AbstractTexture.() -> Unit = {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)

            glTexParameteri(
                GL_TEXTURE_2D,
                GL_TEXTURE_MIN_FILTER,
                if (useMipmap) {
                    if (qualityLevel >= 3) GL_LINEAR_MIPMAP_NEAREST
                    else GL_LINEAR_MIPMAP_LINEAR
                } else if (qualityLevel >= 2) GL_LINEAR else GL_NEAREST
            )
            glTexParameteri(
                GL_TEXTURE_2D,
                GL_TEXTURE_MAG_FILTER,
                if (qualityLevel >= 1) GL_LINEAR else GL_NEAREST
            )

            if (useMipmap) {
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_LOD, 0)
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LOD, levels)
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL, 0)
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, levels)
            }
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        }

        @JvmStatic
        fun afterUpload(
            useMipmap: Boolean = true
        ): AbstractTexture.() -> Unit = {
            if (useMipmap) GLHelper.glGenerateMipmap(GL_TEXTURE_2D)
        }

    }

}