package net.spartanb312.boar.game.render.gui.impls

import net.spartanb312.boar.game.render.Background
import net.spartanb312.boar.game.render.FontRendererBig
import net.spartanb312.boar.game.render.gui.GuiScreen
import net.spartanb312.boar.game.render.gui.Render2DManager
import net.spartanb312.boar.graphics.RS
import org.lwjgl.glfw.GLFW

object ModuleScreen : GuiScreen() {

    override fun onRender(mouseX: Double, mouseY: Double) {
        Background.renderBackground(mouseX, mouseY)
        FontRendererBig.drawCenteredString("Not yet completed", RS.centerX, RS.centerY)
    }

    override fun onKeyTyped(keyCode: Int, modifier: Int): Boolean {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            Render2DManager.popScreen()
            return true
        }
        return false
    }

}