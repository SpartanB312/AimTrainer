package net.spartanb312.boar.game.render.gui.impls

import net.spartanb312.boar.game.render.Background
import net.spartanb312.boar.game.render.FontRendererBig
import net.spartanb312.boar.game.render.FontRendererROG
import net.spartanb312.boar.game.render.gui.GuiScreen
import net.spartanb312.boar.game.render.gui.Render2DManager
import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.graphics.drawing.RenderUtils
import net.spartanb312.boar.utils.color.ColorRGB
import org.lwjgl.glfw.GLFW

object TrainingScreen : GuiScreen() {

    override fun onRender(mouseX: Double, mouseY: Double) {
        Background.renderBackground(mouseX, mouseY)
        FontRendererROG.drawCenteredStringWithShadow("Training", RS.width / 2f, RS.heightF * 0.05f, scale = 0.8f)
        val clampYT = RS.heightF * 0.1f
        val clampYB = RS.heightF * 0.9f
        RenderUtils.drawRect(0f, 0f, RS.widthF, RS.heightF, ColorRGB.BLACK.alpha(32))
        RenderUtils.drawRect(0f, clampYT, RS.widthF, clampYB, ColorRGB.DARK_GRAY.alpha(84))
        FontRendererBig.drawCenteredString("Coming soon", RS.centerX, RS.centerY)
    }

    override fun onKeyTyped(keyCode: Int, modifier: Int): Boolean {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            Render2DManager.popScreen()
            return true
        }
        return false
    }

}