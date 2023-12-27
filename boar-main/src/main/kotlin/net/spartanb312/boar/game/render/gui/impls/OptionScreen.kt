package net.spartanb312.boar.game.render.gui.impls

import net.spartanb312.boar.game.render.FontRendererMain
import net.spartanb312.boar.game.render.gui.GuiScreen
import net.spartanb312.boar.game.render.gui.Render2DManager
import net.spartanb312.boar.graphics.RenderSystem
import net.spartanb312.boar.graphics.drawing.RenderUtils
import net.spartanb312.boar.utils.color.ColorRGB
import org.lwjgl.glfw.GLFW

object OptionScreen : GuiScreen() {

    override fun onRender(mouseX: Double, mouseY: Double) {
        RenderUtils.drawRect(0f, 0f, RenderSystem.widthF, RenderSystem.heightF, ColorRGB.GRAY.alpha(64))
        FontRendererMain.drawCenteredString("你先别急，设置界面还没写", RenderSystem.centerXF, RenderSystem.centerYF)
    }

    override fun onMouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean {
        return true
    }

    override fun onKeyTyped(keyCode: Int, modifier: Int): Boolean {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            Render2DManager.popScreen()
            return true
        }
        return false
    }

}