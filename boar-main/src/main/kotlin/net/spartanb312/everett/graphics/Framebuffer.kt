package net.spartanb312.everett.graphics

import org.lwjgl.opengl.GL30

class Framebuffer(val width: Int, height: Int) {
    val fbo = GL30.glGenFramebuffers()

    fun bindFramebuffer() = GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo)
    fun unbindFramebuffer() = GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0)


}