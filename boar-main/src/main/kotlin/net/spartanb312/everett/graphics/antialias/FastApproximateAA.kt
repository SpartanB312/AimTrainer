package net.spartanb312.everett.graphics.antialias

import net.spartanb312.everett.game.event.ResolutionUpdateEvent
import net.spartanb312.everett.graphics.GLHelper
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.drawing.pmvbo.PersistentMappedVertexBuffer
import net.spartanb312.everett.graphics.event.ScaleUpdateEvent
import net.spartanb312.everett.graphics.matrix.applyOrtho
import net.spartanb312.everett.graphics.matrix.scope
import net.spartanb312.everett.graphics.shader.Shader
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.event.ListenerOwner
import net.spartanb312.everett.utils.event.listener
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30.*
import java.nio.ByteBuffer

class FastApproximateAA(
    width: Int,
    height: Int,
) : ListenerOwner(), AntiAlias {

    private var currentWidth = width
    private var currentHeight = height
    private var currentFBO = AAFrameBuffer(width, height)

    init {
        listener<ScaleUpdateEvent> {
            refresh(
                width = it.newScaledWidth,
                height = it.newScaledHeight
            )
        }
        listener<ResolutionUpdateEvent> {
            refresh(
                width = it.newScaledWidth,
                height = it.newScaledHeight
            )
        }
        subscribe()
    }

    fun refresh(width: Int = currentWidth, height: Int = currentHeight) {
        if (currentWidth != width || currentHeight != height) {
            currentWidth = width
            currentHeight = height
            currentFBO.delete()
            currentFBO = AAFrameBuffer(currentWidth, currentHeight)
        }
    }

    private val program = Shader(
        "assets/shader/FXAA.vert",
        "assets/shader/FXAA.frag",
    ).apply {
        bind()
        glUniform1i(getUniformLocation("screenTexture"), 0)
    }

    private val invResU = GL20.glGetUniformLocation(program.id, "invRes")

    override fun startRendering() {
        GLHelper.bindFramebuffer(currentFBO.fbo, true)
        glDrawBuffer(GL_COLOR_ATTACHMENT0)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
    }

    override fun endRendering() = RS.matrixLayer.scope {
        GLHelper.bindFramebuffer(RS.scaling.fbo)
        glClear(GL_COLOR_BUFFER_BIT)
        glViewport(0, 0, RS.scaledWidth, RS.scaledHeight)
        applyOrtho(0.0f, RS.scaledWidthF, RS.scaledHeightF, 0.0f, -1.0f, 1.0f)
        GLHelper.useProgram(program.id, true)
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, currentFBO.colorTex)
        glUniform2f(invResU, 1f / RS.scaledWidthF, 1f / RS.scaledHeightF)
        with(PersistentMappedVertexBuffer.VertexMode.Universal) {
            universal(-1f, -1f, 0f, 0f, ColorRGB.WHITE)
            universal(1f, -1f, 1f, 0f, ColorRGB.WHITE)
            universal(-1f, 1f, 0f, 1f, ColorRGB.WHITE)
            universal(1f, 1f, 1f, 1f, ColorRGB.WHITE)
            draw(GL_TRIANGLE_STRIP, this@FastApproximateAA.program)
        }
        RS.scaling.drawScaledFramebuffer()
    }

    inner class AAFrameBuffer(width: Int, height: Int) {
        val fbo = glGenFramebuffers()
        val colorTex = glGenTextures()
        private val rboDepth = glGenRenderbuffers()

        init {
            glBindTexture(GL_TEXTURE_2D, colorTex)
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, width, height, 0, GL_RGBA, GL_FLOAT, null as ByteBuffer?)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
            GLHelper.bindFramebuffer(fbo, true)
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorTex, 0)
            glBindRenderbuffer(GL_RENDERBUFFER, rboDepth)
            glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height)
            glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rboDepth)
        }

        fun delete() {
            glDeleteFramebuffers(fbo)
            GL11.glDeleteTextures(colorTex)
            glDeleteRenderbuffers(rboDepth)
        }
    }

}