package net.spartanb312.everett.game.render.gui.impls

import net.spartanb312.everett.game.Configs
import net.spartanb312.everett.game.option.impls.CustomOption
import net.spartanb312.everett.game.render.Background
import net.spartanb312.everett.game.render.FontRendererBold
import net.spartanb312.everett.game.render.FontRendererROG
import net.spartanb312.everett.game.render.gui.GuiScreen
import net.spartanb312.everett.game.render.gui.Render2DManager
import net.spartanb312.everett.game.render.gui.impls.option.OptionsRenderer
import net.spartanb312.everett.game.render.scene.impls.AimTrainingScene
import net.spartanb312.everett.game.training.custom.CustomBallHit
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.drawing.RenderUtils
import net.spartanb312.everett.utils.color.ColorRGB
import org.lwjgl.glfw.GLFW
import kotlin.math.max
import kotlin.math.min

object CustomGameScreen : GuiScreen() {

    private val optionsRenderer = OptionsRenderer(CustomOption)

    override fun onInit() {
        optionsRenderer.scrollOffset = 0
        optionsRenderer.scrollAccumulation = 0
        optionsRenderer.buttons.forEach { it.reset() }
    }

    override fun onClosed() {
        Configs.saveConfig("configs.json")
    }

    override fun onRender(mouseX: Double, mouseY: Double) {
        Background.renderBackground(mouseX, mouseY)
        val scale = max(RS.widthF / 2560f, RS.heightF / 1369f)
        RenderUtils.drawRect(0f, 0f, RS.widthF, RS.heightF, ColorRGB.BLACK.alpha(128))
        FontRendererROG.drawString(
            "Custom Training",
            RS.widthF * 0.07f,
            RS.heightF * 0.08f,
            scale = scale
        )

        optionsRenderer.onRender2D(mouseX, mouseY, min(RS.widthScale, RS.heightScale), 255f)

        // start
        val xRange = (RS.widthF * 0.75f)..(RS.widthF * 0.9f)
        val yRange = (RS.heightF * 0.8f)..(RS.heightF * 0.9f)
        val isHoovered = mouseX in xRange && mouseY in yRange
        RenderUtils.drawRect(
            xRange.start,
            yRange.start,
            xRange.endInclusive,
            yRange.endInclusive,
            ColorRGB.WHITE.alpha(if (isHoovered) 64 else 32)
        )
        FontRendererBold.drawCenteredString("START", RS.widthF * 0.825f, RS.heightF * 0.85f, scale = scale * 2f)
    }

    override fun onMouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean {
        val xRange = (RS.widthF * 0.75f)..(RS.widthF * 0.9f)
        val yRange = (RS.heightF * 0.8f)..(RS.heightF * 0.9f)
        if (mouseX.toFloat() in xRange && mouseY.toFloat() in yRange) {
            TrainingScreen.startTraining(CustomBallHit.new(AimTrainingScene))
            return true
        }
        return optionsRenderer.onMouseClicked(mouseX, mouseY, button)
    }


    override fun onMouseReleased(mouseX: Int, mouseY: Int, button: Int) {
        optionsRenderer.onMouseReleased(mouseX, mouseY, button)
    }


    override fun onKeyTyped(keyCode: Int, modifier: Int): Boolean {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            Render2DManager.popScreen()
            return true
        }
        return false
    }

}