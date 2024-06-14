package net.spartanb312.everett.game.render.gui.impls

import net.spartanb312.everett.game.Language.lang
import net.spartanb312.everett.game.render.Component2D
import net.spartanb312.everett.game.render.FontRendererBig
import net.spartanb312.everett.game.render.gui.GuiScreen
import net.spartanb312.everett.game.render.gui.Render2DManager
import net.spartanb312.everett.game.render.scene.Scene
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.drawing.RenderUtils
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.language.MultiText
import org.lwjgl.glfw.GLFW

class PauseScreen(val scene: Scene) : GuiScreen() {

    val buttons = mutableListOf<Button>()
    private val paused by "Paused".lang("暂停", "暫停")

    override fun onRender(mouseX: Double, mouseY: Double) {
        val scale = RS.generalScale
        val width = 320f * scale
        val height = (buttons.size + 1) * 80f * scale
        val startX = RS.centerX - width / 2f
        var startY = RS.centerY - height / 2f
        RenderUtils.drawRect(startX, startY, startX + width, startY + height, ColorRGB.BLACK.alpha(25))
        FontRendererBig.drawCenteredString(paused, RS.centerXF, startY + 40f * scale, scale = scale)
        startY += 80f * scale
        buttons.forEach {
            it.width = 300f * scale
            it.height = 70f * scale
            it.x = startX + 10 * scale
            it.y = startY
            startY += 80f * scale
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
            return true
        }
        return false
    }

    class Button(str: MultiText, private val action: () -> Unit) : Component2D {
        override var x = 0f
        override var y = 0f
        override var width = 300f
        override var height = 70f
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
            FontRendererBig.drawCenteredString(
                string,
                x + width / 2f,
                y + height / 2f,
                scale = RS.generalScale
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