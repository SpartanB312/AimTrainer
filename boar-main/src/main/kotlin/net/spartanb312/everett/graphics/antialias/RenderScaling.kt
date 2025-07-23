package net.spartanb312.everett.graphics.antialias

import net.spartanb312.everett.AimTrainer.drawTexture
import net.spartanb312.everett.game.event.ResolutionUpdateEvent
import net.spartanb312.everett.game.option.impls.VideoOption
import net.spartanb312.everett.graphics.GLDataType
import net.spartanb312.everett.graphics.GLHelper
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.drawing.buildAttribute
import net.spartanb312.everett.graphics.drawing.pmvbo.PersistentMappedVertexBuffer
import net.spartanb312.everett.graphics.event.EngineLoopEvent
import net.spartanb312.everett.graphics.event.ScaleUpdateEvent
import net.spartanb312.everett.graphics.framebuffer.ResizableFramebuffer
import net.spartanb312.everett.graphics.matrix.applyOrtho
import net.spartanb312.everett.graphics.matrix.scope
import net.spartanb312.everett.graphics.shader.Shader
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.event.ListenerOwner
import net.spartanb312.everett.utils.event.listener
import net.spartanb312.everett.utils.math.ceilToInt
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL45.*

class RenderScaling(initialScale: Float = 1f) : ListenerOwner() {

    var scale = initialScale; private set
    private var targetScale = initialScale;
    val framebuffer by lazy { DisplayFramebuffer() }
    inline val fbo get() = framebuffer.framebuffer.fbo
    inline val tex get() = framebuffer.renderLayer.texture.id

    init {
        listener<EngineLoopEvent.Sync.Pre> {
            rescale(targetScale)
        }
        subscribe()
        update(RS.displayWidth, RS.displayHeight)
    }

    // properties
    var scaledWidth = 0; private set
    var scaledHeight = 0; private set
    var scaledWidthF = 0f; private set
    var scaledHeightF = 0f; private set
    var scaledWidthD = 0.0; private set
    var scaledHeightD = 0.0; private set

    fun update(rawWidth: Int, rawHeight: Int) {
        scaledWidth = (rawWidth * scale).ceilToInt()
        scaledHeight = (rawHeight * scale).ceilToInt()
        scaledWidthF = scaledWidth.toFloat()
        scaledHeightF = scaledHeight.toFloat()
        scaledWidthD = scaledWidth.toDouble()
        scaledHeightD = scaledHeight.toDouble()
    }

    fun setScale(targetScale: Float) {
        this.targetScale = targetScale
    }

    private fun rescale(newScale: Float) {
        if (newScale != scale) {
            val oldScale = scale
            scale = newScale
            val oldScaledWidth = scaledWidth
            val oldScaledHeight = scaledHeight
            update(RS.displayWidth, RS.displayHeight)
            val newScaledWidth = scaledWidth
            val newScaledHeight = scaledHeight
            ScaleUpdateEvent(
                oldScale,
                newScale,
                oldScaledWidth,
                oldScaledHeight,
                newScaledWidth,
                newScaledHeight
            ).post()
        }
    }

    fun drawScaledFramebuffer() {
        RS.matrixLayer.scope {
            GLHelper.bindFramebuffer(0)
            glClear(GL_COLOR_BUFFER_BIT)
            glViewport(0, 0, RS.displayWidth, RS.displayHeight)
            applyOrtho(0.0f, RS.displayWidthF, RS.displayHeightF, 0.0f, -1.0f, 1.0f)
            if (VideoOption.ffxCAS.value) {
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex)
                val shader = if (VideoOption.casMode == VideoOption.CASMode.FAST) FastShader else RCASShader
                GLHelper.useProgram(shader.id)
                GL20.glUniform1f(shader.sharpnessU, VideoOption.sharpness)
                GL20.glUniformMatrix4fv(shader.matrixU, false, RS.matrixLayer.matrixArray)
                with(PersistentMappedVertexBuffer.VertexMode.Universal) {
                    universal(RS.displayWidthF, 0f, 1f, 1f, ColorRGB.WHITE)
                    universal(0f, 0f, 0f, 1f, ColorRGB.WHITE)
                    universal(RS.displayWidthF, RS.displayHeightF, 1f, 0f, ColorRGB.WHITE)
                    universal(0f, RS.displayHeightF, 0f, 0f, ColorRGB.WHITE)
                    draw(GL11.GL_TRIANGLE_STRIP, shader)
                }
            } else drawTexture(
                tex,
                0f,
                0f,
                RS.displayWidthF,
                RS.displayHeightF,
                0f,
                1f,
                1f,
                0f,
            )
        }
    }

    // AMD FFX CAS
    interface CASShader {
        val sharpnessU: Int
        val matrixU: Int
    }

    object FastShader : Shader(
        "assets/shader/general/AMD_FFX_CAS.vsh",
        "assets/shader/general/AMD_FFX_CAS_Fast.fsh"
    ), CASShader {
        override val sharpnessU = getUniformLocation("sharpness")
        override val matrixU = getUniformLocation("matrix")

        init {
            bind()
            GL20.glUniform1i(getUniformLocation("inputTexture"), 0)
        }
    }

    object RCASShader : Shader(
        "assets/shader/general/AMD_FFX_CAS.vsh",
        "assets/shader/general/AMD_FFX_CAS_RCAS.fsh"
    ), CASShader {
        override val sharpnessU = getUniformLocation("sharpness")
        override val matrixU = getUniformLocation("matrix")

        init {
            bind()
            GL20.glUniform1i(getUniformLocation("inputTexture"), 0)
        }
    }


    // scaled framebuffer
    inner class DisplayFramebuffer : ListenerOwner() {
        val framebuffer = ResizableFramebuffer(scaledWidth, scaledHeight, true)
        val renderLayer = framebuffer.generateColorLayer()

        init {
            listener<ResolutionUpdateEvent> {
                framebuffer.resize(it.newScaledWidth, it.newScaledHeight)
            }
            listener<ScaleUpdateEvent> {
                framebuffer.resize(it.newScaledWidth, it.newScaledHeight)
            }
            subscribe()
        }
    }

}