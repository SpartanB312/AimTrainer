package net.spartanb312.everett.graphics.antialias

import net.spartanb312.everett.game.event.ResolutionUpdateEvent
import net.spartanb312.everett.graphics.GLHelper
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.event.ScaleUpdateEvent
import net.spartanb312.everett.utils.event.ListenerOwner
import net.spartanb312.everett.utils.event.listener
import org.lwjgl.opengl.GL45.*

class MultiSampleAA(
    level: Int,
    width: Int,
    height: Int,
) : ListenerOwner(), AntiAlias {

    private var currentLevel = level
    private var currentWidth = width
    private var currentHeight = height
    private var currentFBO = AAFrameBuffer(level, width, height)

    init {
        listener<ScaleUpdateEvent> {
            refresh(
                level = currentLevel,
                width = it.newScaledWidth,
                height = it.newScaledHeight
            )
        }
        listener<ResolutionUpdateEvent> {
            refresh(
                level = currentLevel,
                width = it.newScaledWidth,
                height = it.newScaledHeight
            )
        }
        subscribe()
    }

    fun refresh(level: Int = currentLevel, width: Int = currentWidth, height: Int = currentHeight) {
        if (currentLevel != level || currentWidth != width || currentHeight != height) {
            currentLevel = level
            currentWidth = width
            currentHeight = height
            currentFBO.delete()
            currentFBO = AAFrameBuffer(currentLevel, currentWidth, currentHeight)
        }
    }

    override fun startRendering() {
        GLHelper.bindFramebuffer(currentFBO.fbo, true)
        glDrawBuffer(GL_COLOR_ATTACHMENT0)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
    }

    override fun endRendering() {
        blitFramebuffer(RS.scaling.fbo)
        RS.scaling.drawScaledFramebuffer()
    }

    fun blitFramebuffer(
        targetFramebuffer: Int = 0,
        srcW: Int = currentWidth,
        srcH: Int = currentHeight,
        dstW: Int = currentWidth,
        dstH: Int = currentHeight,
    ) = glBlitNamedFramebuffer(
        currentFBO.fbo,
        targetFramebuffer,
        0,
        0,
        srcW,
        srcH,
        0,
        0,
        dstW,
        dstH,
        GL_COLOR_BUFFER_BIT,
        GL_NEAREST
    )

    inner class AAFrameBuffer(level: Int, width: Int, height: Int) {
        val fbo = glGenFramebuffers()
        private val rboColor = glGenRenderbuffers()
        private val rboDepth = glGenRenderbuffers()

        init {
            GLHelper.bindFramebuffer(fbo, true)
            glBindRenderbuffer(GL_RENDERBUFFER, rboColor)
            glRenderbufferStorageMultisample(GL_RENDERBUFFER, level, GL_RGBA8, width, height)
            glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, rboColor)
            glBindRenderbuffer(GL_RENDERBUFFER, rboDepth)
            glRenderbufferStorageMultisample(GL_RENDERBUFFER, level, GL_DEPTH24_STENCIL8, width, height)
            glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rboDepth)
        }

        fun delete() {
            glDeleteFramebuffers(fbo)
            glDeleteRenderbuffers(rboColor)
            glDeleteRenderbuffers(rboDepth)
        }
    }

}