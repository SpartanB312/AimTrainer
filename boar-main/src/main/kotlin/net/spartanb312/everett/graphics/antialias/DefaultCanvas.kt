package net.spartanb312.everett.graphics.antialias

import net.spartanb312.everett.graphics.RS
import org.lwjgl.opengl.GL45.*

class DefaultCanvas : AntiAlias {

    override fun startRendering() {
        RS.scaling.framebuffer.framebuffer.bindFramebuffer()
        glDrawBuffer(GL_COLOR_ATTACHMENT0)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
    }

    override fun endRendering() {
        RS.scaling.drawScaledFramebuffer()
    }

}