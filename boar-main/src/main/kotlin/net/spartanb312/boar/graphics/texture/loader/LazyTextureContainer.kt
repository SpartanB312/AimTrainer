package net.spartanb312.boar.graphics.texture.loader

import net.spartanb312.boar.graphics.OpenGL
import net.spartanb312.boar.graphics.texture.MipmapTexture
import net.spartanb312.boar.graphics.texture.Texture
import net.spartanb312.boar.graphics.texture.delegate.LateUploadTexture
import net.spartanb312.boar.utils.ResourceHelper
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

class LazyTextureContainer(
    private val texture: LateUploadTexture = LateUploadTexture(),
    private val ioJob: () -> BufferedImage
) : Texture by texture {

    constructor(
        path: String,
        format: Int = OpenGL.GL_RGBA,
        levels: Int = 3,
        useMipmap: Boolean = true,
        qualityLevel: Int = 2
    ) : this(
        MipmapTexture.lateUpload(format, levels, useMipmap, qualityLevel),
        { ImageIO.read(ResourceHelper.getResourceStream(path)) }
    )

    val state get() = texture.state

    // Return a RenderThread Job
    fun asyncLoad(): () -> LateUploadTexture {
        val image = ioJob.invoke()
        return {
            texture.apply { upload(image) }
        }
    }

    fun register(loader: TextureLoader): LazyTextureContainer {
        loader.add(this)
        return this
    }

}