package net.spartanb312.everett.graphics.antialias

import net.spartanb312.everett.AimTrainer.drawTexture
import net.spartanb312.everett.game.event.ResolutionUpdateEvent
import net.spartanb312.everett.graphics.GLHelper
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.event.EngineLoopEvent
import net.spartanb312.everett.graphics.event.ScaleUpdateEvent
import net.spartanb312.everett.graphics.framebuffer.ResizableFramebuffer
import net.spartanb312.everett.graphics.matrix.applyOrtho
import net.spartanb312.everett.graphics.matrix.scope
import net.spartanb312.everett.utils.event.ListenerOwner
import net.spartanb312.everett.utils.event.listener
import net.spartanb312.everett.utils.math.ceilToInt
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
            drawTexture(
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