package net.spartanb312.everett.game.render.gui.impls

import net.spartanb312.everett.game.render.Background
import net.spartanb312.everett.game.render.FontRendererMain
import net.spartanb312.everett.game.render.FontRendererROG
import net.spartanb312.everett.game.render.gui.GuiScreen
import net.spartanb312.everett.game.render.gui.Render2DManager
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.font.drawColoredString
import net.spartanb312.everett.utils.color.ColorRGB
import kotlin.math.min

object PrepareScreen : GuiScreen() {

    private var jumpTimer = Long.MAX_VALUE
    private var shouldJump = false

    override fun onInit() {
        shouldJump = false
        jumpTimer = Long.MAX_VALUE
    }

    override fun onRender(mouseX: Double, mouseY: Double) {
        val scale = min(RS.widthScale, RS.heightScale)
        Background.update(1f)
        Background.renderBackground(mouseX, mouseY)

        val width = FontRendererROG.getWidth("Aim Trainer", scale)
        val height = FontRendererROG.getHeight(scale)
        val alphaRate = 1f - ((System.currentTimeMillis() - jumpTimer) / 500.0).toFloat().coerceIn(0f, 1f)
        FontRendererROG.drawColoredString(
            "Aim Trainer",
            RS.centerXF - width / 2f,
            RS.centerYF * 0.9f - height / 2f,
            saturation = 0.85f,
            shadowDepth = 2f * scale,
            scale = scale,
            alpha = alphaRate
        )
        FontRendererMain.drawCenteredString(
            "Press any key to start",
            RS.centerXF,
            RS.centerYF * 1.3f,
            color = ColorRGB.GRAY.alpha((128 * alphaRate).toInt()),
            scale = scale
        )

        if (shouldJump && System.currentTimeMillis() - jumpTimer >= 500) {
            Render2DManager.displayScreen(MainMenuScreen)
        }
    }

    override fun onMouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (!shouldJump) jumpTimer = System.currentTimeMillis()
        shouldJump = true
        return true
    }

    override fun onKeyTyped(keyCode: Int, modifier: Int): Boolean {
        if (!shouldJump) jumpTimer = System.currentTimeMillis()
        shouldJump = true
        return true
    }

}