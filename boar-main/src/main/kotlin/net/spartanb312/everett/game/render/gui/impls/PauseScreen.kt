package net.spartanb312.everett.game.render.gui.impls

import net.spartanb312.everett.game.Language.lang
import net.spartanb312.everett.game.render.Component2D
import net.spartanb312.everett.game.render.FontRendererBig
import net.spartanb312.everett.game.render.FontRendererBold
import net.spartanb312.everett.game.render.FontRendererMain
import net.spartanb312.everett.game.render.FontRendererROG
import net.spartanb312.everett.game.render.gui.GuiScreen
import net.spartanb312.everett.game.render.gui.Render2DManager
import net.spartanb312.everett.game.render.scene.Scene
import net.spartanb312.everett.game.render.scene.impls.AimTrainingScene
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.drawing.RenderUtils
import net.spartanb312.everett.graphics.event.EngineLoopEvent
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.event.IListenerOwner
import net.spartanb312.everett.utils.event.Listener
import net.spartanb312.everett.utils.event.ParallelListener
import net.spartanb312.everett.utils.event.listener
import net.spartanb312.everett.utils.language.MultiText
import net.spartanb312.everett.utils.math.vector.Vec2f
import org.lwjgl.glfw.GLFW

class PauseScreen(val scene: Scene) : GuiScreen(), IListenerOwner {

    override val listeners = ArrayList<Listener>()
    override val parallelListeners = ArrayList<ParallelListener>()

    init {
        listener<EngineLoopEvent.Loop.Post> {
            if (scene is AimTrainingScene && Render2DManager.currentScreen == null) scene.currentTraining?.resume()
        }
        subscribe()
    }

    val buttons = mutableListOf<Button>()

    companion object {
        fun drawTitle(scale: Float) {
            // Title
            RenderUtils.drawRect(
                RS.widthF * 0.05f + 10f * scale,
                RS.heightF * 0.1f,
                RS.widthF * 0.6f,
                RS.heightF * 0.1f + scale * 250f,
                ColorRGB.BLACK.alpha(96)
            )
            val tr = AimTrainingScene.currentTraining
            val str = if (tr != null) {
                "${tr.category.uppercase()}: &b${tr.trainingName.uppercase()}"
            } else "NULL"
            val desc = tr?.description ?: "No description"
            var ty = RS.heightF * 0.1f + 5F * scale
            FontRendererBold.drawString(
                str,
                RS.widthF * 0.05f + 25f * scale,
                ty,
                scale = scale * 1.5f
            )
            ty += FontRendererBold.getHeight(scale * 1.5f)
            RenderUtils.drawLine(
                Vec2f(RS.widthF * 0.05f + 25f * scale, ty),
                Vec2f(RS.widthF * 0.6f - 25f * scale, ty),
                2f * scale / RS.scaling.scale,
                ColorRGB.WHITE
            )
            ty += FontRendererBold.getHeight(scale * 0.5f)
            FontRendererMain.drawString(
                desc.uppercase(),
                RS.widthF * 0.05f + 25f * scale,
                ty,
                scale = scale * 0.75f
            )
        }
    }

    override fun onRender(mouseX: Double, mouseY: Double) {
        RenderUtils.drawRect(0f, 0f, RS.widthF, RS.heightF, ColorRGB.BLACK.alpha(64))
        val scale = RS.generalScale
        val height = buttons.size * 60f * scale
        val startX = RS.widthF * 0.05f
        var startY = RS.heightF * 0.9f - height
        drawTitle(scale)
        buttons.forEach {
            it.width = 300f * scale
            it.height = 50f * scale
            it.x = startX + 10 * scale
            it.y = startY
            startY += 60f * scale
            it.onRender2D(mouseX, mouseY, 255f)
        }
    }

    override fun onMouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean {
        for (b in buttons) {
            if (b.onMouseClicked(mouseX, mouseY, button)) return true
        }
        return false
    }

    override fun onKeyTyped(keyCode: Int, modifier: Int): Boolean {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            Render2DManager.popScreen()
            //if (scene is AimTrainingScene) scene.currentTraining?.resume()
            return true
        }
        return false
    }

    class Button(str: MultiText, private val action: () -> Unit) : Component2D {
        override var x = 0f
        override var y = 0f
        override var width = 300f
        override var height = 50f
        private val string by str
        override fun onRender2D(mouseX: Double, mouseY: Double, alpha: Float) {
            val isHoovered = isHoovered(mouseX.toInt(), mouseY.toInt())
            val width = width
            val height = height
            RenderUtils.drawRect(
                x, y, x + width, y + height,
                if (isHoovered) ColorRGB.WHITE.alpha((255 * 0.25f).toInt())
                else ColorRGB.WHITE.alpha((255 * 0.1f).toInt())
            )
            FontRendererBold.drawString(
                string.uppercase(),
                x + width / 20f,
                y,
                scale = RS.generalScale * 1.5f
            )
        }

        override fun onMouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int): Boolean {
            if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_1 && isHoovered(mouseX, mouseY)) {
                action()
                return true
            }
            return false
        }
    }

}