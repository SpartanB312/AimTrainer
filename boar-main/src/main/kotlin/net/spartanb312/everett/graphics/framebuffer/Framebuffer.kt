package net.spartanb312.everett.graphics.framebuffer

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
    val texture: FramebufferTexture
    val rbo: Int

    fun bindFramebuffer(viewPort: Boolean = true) {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo)
        if (viewPort) GL11.glViewport(0, 0, width, height)
        GL11.glClearColor(clearColor.rFloat, clearColor.gFloat, clearColor.bFloat, clearColor.aFloat)
    }

    fun unbindFramebuffer() = GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0)

    fun delete() {
        unbindFramebuffer()
        if (useRBO) GL30.glDeleteRenderbuffers(rbo)
        GL30.glDeleteFramebuffers(fbo)
        texture.deleteTexture()
    }
}