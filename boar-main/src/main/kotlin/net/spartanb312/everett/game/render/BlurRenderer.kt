package net.spartanb312.everett.game.render

import net.spartanb312.everett.AimTrainer
import net.spartanb312.everett.graphics.GLDataType
import net.spartanb312.everett.graphics.GLHelper
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.RenderSystem
import net.spartanb312.everett.graphics.drawing.buildAttribute
import net.spartanb312.everett.graphics.drawing.pmvbo.PersistentMappedVBO
import net.spartanb312.everett.graphics.framebuffer.FixedFramebuffer
import net.spartanb312.everett.graphics.framebuffer.Framebuffer
import net.spartanb312.everett.graphics.matrix.getFloatArray
import net.spartanb312.everett.graphics.shader.Shader
import net.spartanb312.everett.graphics.shader.useShader
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL45.glTextureParameteri

object BlurRenderer {

    private val attribute = buildAttribute(16) { float(0, 4, GLDataType.GL_FLOAT, false) }
    private val vao = PersistentMappedVBO.createVao(attribute)

    private val passH = Pass("assets/shader/general/BlurH.vsh")
    private val passV = Pass("assets/shader/general/BlurV.vsh")
    private var fbo1 = FixedFramebuffer(RS.width, RS.height, false)
    private var fbo2 = FixedFramebuffer(RS.width, RS.height, false)

    fun updateResolution(width: Int, height: Int) {
        fbo1 = FixedFramebuffer(width, height, false)
        fbo2 = FixedFramebuffer(width, height, false)
        passH.updateResolution(width.toFloat(), height.toFloat())
        passV.updateResolution(width.toFloat(), height.toFloat())
        setTextureParam(fbo1.texture.id)
        setTextureParam(fbo2.texture.id)
    }

    fun render(startX: Float, startY: Float, endX: Float, endY: Float, pass: Int) {
        if (pass == 0) return

        //RenderUtils.drawRect(startX,startY,endX,endY, ColorRGB.RED)
        setTextureParam(AimTrainer.framebuffer.texture.id)
        putVertex(startX, startY, endX, endY)
        GLHelper.blend = false
        GLHelper.depth = false
        passH.updateMatrix(RS.matrixLayer.matrixArray)
        passV.updateMatrix(RS.matrixLayer.matrixArray)

        GL30.glBindVertexArray(vao)

        var extend = pass - 1f
        bindFbo(AimTrainer.framebuffer, fbo1)
        drawPass(passH, extend, extend + 1.0f)

        while (extend > 0) {
            bindFbo(fbo1, fbo2)
            drawPass(passV, extend, extend)

            bindFbo(fbo2, fbo1)
            drawPass(passH, extend - 1.0f, extend)
            extend--
        }

        bindFbo(fbo1, AimTrainer.framebuffer)
        drawPass(passV, 0.0f, 0.0f)

        fbo1.texture.unbindTexture()
        GLHelper.blend = true
        PersistentMappedVBO.end(16)
        GL30.glBindVertexArray(0)
    }

    private fun setTextureParam(textureID: Int) {
        glTextureParameteri(textureID, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTextureParameteri(textureID, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTextureParameteri(textureID, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTextureParameteri(textureID, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
    }

    private fun putVertex(x1: Float, y1: Float, x2: Float, y2: Float) {
        val arr = PersistentMappedVBO.arr
        val pointer = arr.ptr

        pointer[0] = x1
        pointer[4] = y1
        pointer[8] = -1.0f
        pointer[12] = 1.0f

        pointer[16] = x1
        pointer[20] = y2
        pointer[24] = -1.0f
        pointer[28] = -1.0f

        pointer[32] = x2
        pointer[36] = y2
        pointer[40] = 1.0f
        pointer[44] = -1.0f

        pointer[48] = x2
        pointer[52] = y1
        pointer[56] = 1.0f
        pointer[60] = 1.0f

        pointer[64] = x1
        pointer[68] = y1
        pointer[72] = -1.0f
        pointer[76] = 1.0f

        pointer[80] = x2
        pointer[84] = y2
        pointer[88] = 1.0f
        pointer[92] = -1.0f

        arr += 96
    }

    private fun drawPass(shader: Pass, x: Float, y: Float) {
        shader.bind()
        shader.updateExtend(x, y)
        glDrawArrays(GL_TRIANGLES, PersistentMappedVBO.drawOffset, 6)
    }

    private fun bindFbo(from: Framebuffer, to: Framebuffer) {
        from.texture.bindTexture()
        to.bindFramebuffer(false)
    }

    private class Pass(vsh: String) : Shader(vsh, "assets/shader/general/Blur.fsh") {
        val matrixUniform = getUniformLocation("matrix")
        val reverseProjection = getUniformLocation("reverseProjection")
        val resolution = getUniformLocation("resolution")
        val extend = getUniformLocation("extend")

        init {
            useShader {
                updateResolution(RenderSystem.widthF, RenderSystem.heightF)
                GL20.glUniform1i(getUniformLocation("background"), 0)
            }
        }

        fun updateMatrix(matrix: FloatArray) {
            GL20.glUniformMatrix4fv(matrixUniform, false, matrix)
        }

        fun updateResolution(width: Float, height: Float) {
            GL20.glUniform2f(resolution, width, height)
            val matrix = Matrix4f()
                .ortho(0.0f, width, 0.0f, height, 1000.0f, 3000.0f)
                .invert()
            GL20.glUniformMatrix4fv(reverseProjection, false, matrix.getFloatArray())
        }

        fun updateExtend(x: Float, y: Float) {
            GL20.glUniform2f(extend, x, y)
        }
    }

}