package net.spartanb312.boar.game.render.gui.impls

import net.spartanb312.boar.game.render.Component2D
import net.spartanb312.boar.game.render.FontRendererBig
import net.spartanb312.boar.game.render.gui.GuiScreen
import net.spartanb312.boar.game.render.gui.Render2DManager
import net.spartanb312.boar.game.render.scene.Scene
import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.graphics.drawing.RenderUtils
import net.spartanb312.boar.language.Language.m
import net.spartanb312.boar.language.MultiText
import net.spartanb312.boar.utils.color.ColorRGB
import org.lwjgl.glfw.GLFW

class PauseScreen(val scene: Scene) : GuiScreen() {

    val buttons = mutableListOf<Button>()
    private val paused by "Paused".m("暂停", "暫停")

    override fun onRender(mouseX: Double, mouseY: Double) {
        val width = 320f
        val height = (buttons.size + 1) * 80f
        val startX = RS.centerX - 160f
        var startY = RS.centerY - height / 2f
        RenderUtils.drawRect(startX, startY, startX + width, startY + height, ColorRGB.BLACK.alpha(25))
        FontRendererBig.drawCenteredString(paused, RS.centerXF, startY + 40f)
        startY += 80f
        buttons.forEach {
            it.x = startX + 10
            it.y = startY
            startY += 80f
            it.onRender2D(mouseX, mouseY)
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
        override fun onRender2D(mouseX: Double, mouseY: Double) {
            val isHoovered = isHoovered(mouseX.toInt(), mouseY.toInt())
            RenderUtils.drawRect(
                x, y, x + width, y + height,
                if (isHoovered) ColorRGB.WHITE.alpha((255 * 0.25f).toInt())
                else ColorRGB.WHITE.alpha((255 * 0.1f).toInt())
            )
            FontRendererBig.drawCenteredString(string, x + width / 2f, y + height / 2f)
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