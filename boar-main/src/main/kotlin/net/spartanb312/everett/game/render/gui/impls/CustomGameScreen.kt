package net.spartanb312.everett.game.render.gui.impls

import net.spartanb312.everett.game.render.Background
import net.spartanb312.everett.game.render.FontRendererBig
import net.spartanb312.everett.game.render.FontRendererROG
import net.spartanb312.everett.game.render.gui.GuiScreen
import net.spartanb312.everett.game.render.gui.Render2DManager
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.drawing.RenderUtils
import net.spartanb312.everett.utils.color.ColorRGB
import org.lwjgl.glfw.GLFW
import kotlin.math.max

object CustomGameScreen : GuiScreen() {

    override fun onRender(mouseX: Double, mouseY: Double) {
        Background.renderBackground(mouseX, mouseY)
        val scale = max(RS.widthF / 2560f, RS.heightF / 1369f)
        FontRendererROG.drawCenteredStringWithShadow(
            "Custom Game",
            RS.width / 2f,
            RS.heightF * 0.05f,
            scale = scale
        )
        val clampYT = RS.heightF * 0.1f
        val clampYB = RS.heightF * 0.9f
        RenderUtils.drawRect(0f, 0f, RS.widthF, RS.heightF, ColorRGB.BLACK.alpha(32))
        RenderUtils.drawRect(0f, clampYT, RS.widthF, clampYB, ColorRGB.DARK_GRAY.alpha(84))

        FontRendererBig.drawCenteredString("Coming soon", RS.centerX, RS.centerY, scale = scale * 2f)
    }

    override fun onKeyTyped(keyCode: Int, modifier: Int): Boolean {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            Render2DManager.popScreen()
            return true
        }
        return false
    }

}