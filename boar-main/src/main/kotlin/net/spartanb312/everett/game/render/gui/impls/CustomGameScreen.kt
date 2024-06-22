package net.spartanb312.everett.game.render.gui.impls

import net.spartanb312.everett.game.Configs
import net.spartanb312.everett.game.option.impls.CustomOption
import net.spartanb312.everett.game.render.Background
import net.spartanb312.everett.game.render.FontRendererBold
import net.spartanb312.everett.game.render.FontRendererMain
import net.spartanb312.everett.game.render.FontRendererROG
import net.spartanb312.everett.game.render.gui.GuiScreen
import net.spartanb312.everett.game.render.gui.Render2DManager
import net.spartanb312.everett.game.render.gui.impls.option.OptionsRenderer
import net.spartanb312.everett.game.render.scene.impls.AimTrainingScene
import net.spartanb312.everett.game.training.custom.CustomTraining
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.drawing.RenderUtils
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.config.setting.primitive.EnumSetting
import org.lwjgl.glfw.GLFW
import kotlin.math.max
import kotlin.math.min

object CustomGameScreen : GuiScreen() {

    private val optionsRenderer = OptionsRenderer(CustomOption)

    override fun onInit() {
        optionsRenderer.scrollOffset = 0
        optionsRenderer.scrollAccumulation = 0
        resetAnimation()
    }

    override fun onClosed() {
        Configs.saveConfig("configs.json")
    }

    override fun onRender(mouseX: Double, mouseY: Double) {
        Background.renderBackground(mouseX, mouseY)
        val scale = max(RS.widthF / 2560f, RS.heightF / 1369f)
        RenderUtils.drawRect(0f, 0f, RS.widthF, RS.heightF, ColorRGB.BLACK.alpha(128))
        val offset = ((System.currentTimeMillis() - 114514) % 5000) / 5000f
        val color = ColorRGB(0, 186, 253)
        FontRendererROG.drawGradientString(
            "Custom Training",
            RS.widthF * 0.07f,
            RS.heightF * 0.08f,
            arrayOf(
                ColorRGB.WHITE.circle(color, offset),
                ColorRGB.WHITE.circle(color, offset - 0.125f),
                ColorRGB.WHITE.circle(color, offset - 0.25f),
            ),
            scale = scale
        )

        optionsRenderer.onRender2D(mouseX, mouseY, min(RS.widthScale, RS.heightScale), 255f)

        // desc
        val rightStartX = RS.widthF * 0.62f
        val rightEndX = RS.widthF * 0.93f
        val rightStartY = RS.heightF * 0.22f
        val rightEndY = RS.heightF * 0.78f
        val bgColor = ColorRGB.WHITE.alpha(32)
        RenderUtils.drawRect(
            rightStartX,
            rightStartY,
            rightEndX,
            rightEndY,
            bgColor
        )
        val startX = rightStartX + 50f * scale
        val endX = rightEndX - 50f * scale
        var startY = rightStartY + 50f * scale
        FontRendererBold.drawString("BASE MODE: ", startX, startY, scale = scale * 1.2f)
        FontRendererMain.drawString(
            CustomTraining.type.value.name,
            startX + FontRendererBold.getWidth("BASE MODE:   ", scale * 1.2f),
            startY,
            scale = scale * 1.2f
        )
        startY += FontRendererBold.getHeight(scale * 2f)

        FontRendererBold.drawString("OVERRIDES: ", startX, startY, scale = scale * 1.2f)
        startY += FontRendererBold.getHeight(scale * 1.4f)
        var renderCount = 0
        val settingGroup = CustomTraining.type.value.settingGroup
        settingGroup.settings.forEach {
            if (it.isModified && it.isVisible) {
                if (renderCount < 5) {
                    FontRendererMain.drawString(it.displayName, startX + scale * 50f, startY, scale = scale)
                    FontRendererMain.drawString(
                        it.displayValue,
                        endX - FontRendererMain.getWidth(it.displayValue, scale) - scale * 50f,
                        startY,
                        scale = scale
                    )
                    startY += FontRendererMain.getHeight(scale * 1.1f)
                }
                renderCount++
            }
        }
        if (renderCount == 0) {
            FontRendererMain.drawString(
                "NO OVERRIDES, USING DEFAULT",
                startX + scale * 50f,
                startY,
                scale = scale
            )
            startY += FontRendererBold.getHeight(scale * 1.4f)
        } else if (renderCount > 5) {
            val more = renderCount - 5
            FontRendererMain.drawString(
                "AND $more MORE ${if (more == 1) "OVERRIDE..." else "OVERRIDES..."}",
                startX + scale * 50f,
                startY,
                scale = scale
            )
            startY += FontRendererBold.getHeight(scale * 1.4f)
        } else startY += FontRendererBold.getHeight(scale * 0.3f)

        FontRendererBold.drawString("SAFE CHECK: ", startX, startY, scale = scale * 1.2f)
        startY += FontRendererBold.getHeight(scale * 1.4f)
        val actuallyAmount = min(settingGroup.width * settingGroup.height / 2, settingGroup.amount)
        if (actuallyAmount != settingGroup.amount) {
            FontRendererMain.drawString(
                "Amount too large! Applied to $actuallyAmount.",
                startX + scale * 50f,
                startY,
                scale = scale
            )
            startY += FontRendererMain.getHeight(scale * 1.1f)
        } else {
            FontRendererMain.drawString(
                "NO CONFLICTS DETECTED",
                startX + scale * 50f,
                startY,
                scale = scale
            )
            startY += FontRendererMain.getHeight(scale * 1.1f)
        }


        // start
        val lrWidth = 130f * scale
        val xRange = (rightStartX + lrWidth * 1.2f)..(rightEndX - lrWidth * 1.2f)
        val yRange = (RS.heightF * 0.8f)..(RS.heightF * 0.9f)
        val isHoovered = mouseX in xRange && mouseY in yRange
        RenderUtils.drawRect(
            xRange.start,
            yRange.start,
            xRange.endInclusive,
            yRange.endInclusive,
            ColorRGB.WHITE.alpha(if (isHoovered) 64 else 32)
        )
        FontRendererBold.drawCenteredString("START", RS.widthF * 0.775f, RS.heightF * 0.85f, scale = scale * 2f)

        // prev
        val lRange = rightStartX..(rightStartX + lrWidth)
        val isLHoovered = mouseX in lRange && mouseY in yRange
        RenderUtils.drawRect(
            lRange.start,
            yRange.start,
            lRange.endInclusive,
            yRange.endInclusive,
            ColorRGB.WHITE.alpha(if (isLHoovered) 64 else 32)
        )
        FontRendererBold.drawCenteredString(
            "<",
            (lRange.start + lRange.endInclusive) / 2f,
            RS.heightF * 0.85f,
            scale = scale * 2f
        )

        // next
        val rRange = (rightEndX - lrWidth)..rightEndX
        val isRHoovered = mouseX in rRange && mouseY in yRange
        RenderUtils.drawRect(
            rRange.start,
            yRange.start,
            rRange.endInclusive,
            yRange.endInclusive,
            ColorRGB.WHITE.alpha(if (isRHoovered) 64 else 32)
        )
        FontRendererBold.drawCenteredString(
            ">",
            (rRange.start + rRange.endInclusive) / 2f,
            RS.heightF * 0.85f,
            scale = scale * 2f
        )
    }

    fun resetAnimation() {
        optionsRenderer.buttons.forEach { it.reset() }
        optionsRenderer.scrollOffset = 0
        optionsRenderer.scrollAccumulation = 0
    }

    override fun onMouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean {
        val yRange = (RS.heightF * 0.8f)..(RS.heightF * 0.9f)
        val mx = mouseX.toFloat()
        val my = mouseY.toFloat()
        if (my in yRange) {
            val scale = max(RS.widthF / 2560f, RS.heightF / 1369f)
            val lrWidth = 130f * scale
            val rightStartX = RS.widthF * 0.62f
            val rightEndX = RS.widthF * 0.93f
            val xRange = (rightStartX + lrWidth * 1.2f)..(rightEndX - lrWidth * 1.2f)
            if (mx in xRange && my in yRange) {
                TrainingScreen.startTraining(CustomTraining.new(AimTrainingScene), CustomGameScreen)
                return true
            }
            val lRange = rightStartX..(rightStartX + lrWidth)
            val rRange = (rightEndX - lrWidth)..rightEndX
            if (mx in lRange) {
                (CustomTraining.type as EnumSetting<*>).lastValue()
                return true
            } else if (mx in rRange) {
                (CustomTraining.type as EnumSetting<*>).nextValue()
                return true
            }
        }
        return optionsRenderer.onMouseClicked(mouseX, mouseY, button)
    }


    override fun onMouseReleased(mouseX: Int, mouseY: Int, button: Int) {
        optionsRenderer.onMouseReleased(mouseX, mouseY, button)
    }

    override fun onKeyTyped(keyCode: Int, modifier: Int): Boolean {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            Render2DManager.popScreen()
            if (Render2DManager.currentScreen == null) Render2DManager.displayScreen(MainMenuScreen)
            return true
        }
        return false
    }

}