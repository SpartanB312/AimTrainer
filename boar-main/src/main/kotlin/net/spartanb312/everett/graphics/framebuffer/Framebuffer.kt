package net.spartanb312.everett.graphics.framebuffer

import net.spartanb312.everett.graphics.GLHelper
import net.spartanb312.everett.graphics.texture.Texture
import net.spartanb312.everett.graphics.texture.delegate.FramebufferTexture
import net.spartanb312.everett.utils.color.ColorRGB
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30

interface Framebuffer {

    val width: Int
    val height: Int
    val useRBO: Boolean
    val clearColor: ColorRGB
    val fbo: Int
    val rbo: Int
    val colorAttachments: MutableList<ColorLayer>

    val texture get() = colorAttachments[0].texture

    fun bindFramebuffer(viewPort: Boolean = true) {
        GLHelper.bindFramebuffer(fbo)
        if (viewPort) GL11.glViewport(0, 0, width, height)
        GL11.glClearColor(clearColor.rFloat, clearColor.gFloat, clearColor.bFloat, clearColor.aFloat)
    }

    fun unbindFramebuffer() = GLHelper.bindFramebuffer(0)

    fun delete(colorAttachments: Boolean = true) {
        if (useRBO) GL30.glDeleteRenderbuffers(rbo)
        GL30.glDeleteFramebuffers(fbo)
        GLHelper
        if (colorAttachments) this.colorAttachments.forEach { it.texture.deleteTexture() }
    }

    interface ColorLayer : Texture {
        val index: Int
        val framebuffer: Framebuffer
        val texture: FramebufferTexture
        val attachmentIndex get() = GL30.GL_COLOR_ATTACHMENT0 + index

        fun bindLayer() = GL11.glDrawBuffer(attachmentIndex)
        fun delete() = texture.deleteTexture()
    }

}