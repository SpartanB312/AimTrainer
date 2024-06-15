package net.spartanb312.everett.graphics.framebuffer

import net.spartanb312.everett.graphics.GLHelper
import net.spartanb312.everett.graphics.texture.Texture
import net.spartanb312.everett.graphics.texture.delegate.FramebufferTexture
import net.spartanb312.everett.utils.color.ColorRGB
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30
import java.nio.IntBuffer

class FixedFramebuffer(
    override val width: Int,
    override val height: Int,
    override val useRBO: Boolean,
    override val clearColor: ColorRGB = ColorRGB(0f, 0f, 0f, 0f)
) : Framebuffer {

    private var index = 0
    override val fbo: Int = GL30.glGenFramebuffers()
    override val colorAttachments = mutableListOf<Framebuffer.ColorLayer>()
    override val rbo: Int

    fun generateColorLayer(): FixedColorLayer {
        return FixedColorLayer(index++, this)
    }

    init {
        // fbo
        GLHelper.bindFramebuffer(fbo)

        // rbo
        if (useRBO) {
            rbo = GL30.glGenRenderbuffers()
            GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, rbo)
            GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, width, height)
            GL30.glFramebufferRenderbuffer(
                GL30.GL_FRAMEBUFFER,
                GL30.GL_DEPTH_STENCIL_ATTACHMENT,
                GL30.GL_RENDERBUFFER,
                rbo
            )
        } else rbo = -1
        GL11.glClearColor(clearColor.rFloat, clearColor.gFloat, clearColor.bFloat, clearColor.aFloat)
        // unbind
        GLHelper.bindFramebuffer(0)
    }

    class FixedColorLayer(
        override val index: Int,
        override val framebuffer: Framebuffer,
        override val texture: FramebufferTexture = FramebufferTexture(framebuffer.width, framebuffer.height)
    ) : Framebuffer.ColorLayer, Texture by texture {

        init {
            framebuffer.bindFramebuffer()
            texture.bindTexture()
            GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D,
                0,
                GL11.GL_RGBA8,
                framebuffer.width,
                framebuffer.height,
                0,
                GL11.GL_RGBA,
                GL11.GL_UNSIGNED_BYTE,
                null as IntBuffer?
            )
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
            GL30.glFramebufferTexture2D(
                GL30.GL_FRAMEBUFFER,
                GL30.GL_COLOR_ATTACHMENT0 + index,
                GL11.GL_TEXTURE_2D,
                texture.id,
                0
            )
            framebuffer.colorAttachments.add(this)
        }

    }

}