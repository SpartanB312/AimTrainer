package net.spartanb312.everett.game.render.gui.impls

import net.spartanb312.everett.game.Configs
import net.spartanb312.everett.game.Language
import net.spartanb312.everett.game.Language.lang
import net.spartanb312.everett.game.crosshair.Crosshairs
import net.spartanb312.everett.game.crosshair.impls.CrosshairCustom
import net.spartanb312.everett.game.crosshair.impls.gun.GunCrosshair
import net.spartanb312.everett.game.option.impls.*
import net.spartanb312.everett.game.render.*
import net.spartanb312.everett.game.render.gui.GuiScreen
import net.spartanb312.everett.game.render.gui.Render2DManager
import net.spartanb312.everett.game.render.gui.impls.option.OptionsRenderer
import net.spartanb312.everett.game.render.scene.SceneManager
import net.spartanb312.everett.game.render.scene.impls.DummyScene
import net.spartanb312.everett.graphics.GLHelper
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.drawing.RenderUtils
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.language.MultiText
import net.spartanb312.everett.utils.math.ConvergeUtil.converge
import net.spartanb312.everett.utils.misc.Limiter
import net.spartanb312.everett.utils.timing.Timer
import org.lwjgl.glfw.GLFW
import java.text.DecimalFormat
import kotlin.math.min

object OptionScreen : GuiScreen() {

    private var currentSelected = SubTitle.Control.selectButton
    private var alpha = 255f
    private var enabled = false
    private var leftSideX = 0f
    private var rightSideX = 0f

    private val updater = Limiter(33) {
        if (enabled) alpha = alpha.converge(255f, 0.5f)
        else alpha -= 50
        if (alpha < 0) alpha = 0f
    }
    private val slideTimer = Timer()
    private val format = DecimalFormat("0.0")

    override fun onInit() {
        Language.update(true)
        enabled = true
        if (alpha == 1f) alpha++
    }

    override fun onRender(mouseX: Double, mouseY: Double) {
        Background.renderBackground(mouseX, mouseY)
        val scale = min(RS.widthScale, RS.heightScale)
        updater.invoke()
        if (alpha == 0f) Render2DManager.popScreen()
        if (!enabled && Render2DManager.currentScreen == this && Render2DManager.size == 1 && SceneManager.currentScene != DummyScene)
            GLHelper.mouseMode(GLFW.GLFW_CURSOR_DISABLED)
        RenderUtils.drawRect(0, 0, RS.width, RS.height, ColorRGB.BLACK.alpha((alpha / 2).toInt()))
        var startX = RS.widthF * 0.07f
        val startY = RS.heightF * 0.08f
        SubTitle.entries.forEach {
            with(it.selectButton) {
                x = startX
                y = startY
                startX += (this.width + 35 * scale)
                onRender2D(mouseX, mouseY, alpha)
            }
        }
        slideTimer.passedAndReset(17) {
            val targetLeft = currentSelected.x
            val targetRight = currentSelected.x + currentSelected.width
            if (targetLeft < leftSideX) {
                //On the left side
                leftSideX = leftSideX.converge(targetLeft, 0.3f)
                rightSideX = rightSideX.converge(targetRight, 0.15f)
            } else if (targetRight > rightSideX) {
                //On the right side
                rightSideX = rightSideX.converge(targetRight, 0.3f)
                leftSideX = leftSideX.converge(targetLeft, 0.15f)
            }
        }
        val lineStartY = startY + FontRendererBig.getHeight(scale) * 1.05f
        val lineEndY = lineStartY + 8f * scale
        RenderUtils.drawRect(leftSideX, lineStartY, rightSideX, lineEndY, ColorRGB.WHITE.alpha(alpha.toInt()))
        currentSelected.optionsRenderer.onRender2D(mouseX, mouseY, scale, alpha)

        // Right
        val rightStartX = RS.widthF * 0.62f
        val rightEndX = RS.widthF * 0.93f
        val rightStartY = RS.heightF * 0.22f
        val rightEndY = RS.heightF * 0.9f
        // Description
        val bgColor = ColorRGB.WHITE.alpha((alpha * 0.1f).toInt())
        RenderUtils.drawRect(
            rightStartX, rightStartY,
            rightEndX, RS.heightF * 0.635f,
            bgColor
        )

        // Info
        FontRendererBig.drawString(
            "${VideoOption.fovType}: ",
            rightStartX + scale * 16f,
            RS.heightF * 0.685f + scale * 10f,
            scale = scale * 0.7f,
            color = ColorRGB.WHITE.alpha((alpha * 0.8f).toInt())
        )
        val currentCrosshair = CrosshairRenderer.currentCrosshair.crosshair
        val addon = if (currentCrosshair is GunCrosshair && currentCrosshair.syncFOV) "[Synced]" else ""
        FontRendererBig.drawString(
            "${String.format("%.1f", VideoOption.actualFovValue)}$addon",
            rightStartX + scale * 16f + FontRendererBig.getWidth("${VideoOption.fovType}: ", scale * 0.7f),
            RS.heightF * 0.685f + scale * 15f,
            scale = scale * 0.5f,
            color = ColorRGB.WHITE.alpha((alpha * 0.6f).toInt())
        )
        RenderUtils.drawRect(
            rightStartX, RS.heightF * 0.685f,
            rightEndX, rightEndY,
            bgColor
        )

        // Crosshair
        val width = RS.heightF * 0.1075f
        val centerX = RS.widthF * 0.93f - width
        val centerY = RS.heightF * 0.7925f - width * 0.25f
        if (CrosshairCustom.calibrate.value && CrosshairOption.crosshairType.value == Crosshairs.Custom) {
            RenderUtils.drawLine(
                centerX,
                RS.heightF * 0.685f,
                centerX,
                RS.heightF * 0.9f,
                2f * scale,
                ColorRGB.RED.alpha(alpha.toInt())
            )
            RenderUtils.drawLine(
                centerX - width,
                centerY,
                RS.widthF * 0.93f,
                centerY,
                2f * scale,
                ColorRGB.RED.alpha(alpha.toInt())
            )
        }
        CrosshairRenderer.onRender(false, centerX, centerY, VideoOption.dfov, ColorRGB.WHITE.alpha(alpha.toInt()))

        // V-RAM
        val framebufferScale = (RS.widthF * RS.heightF) / (1920 * 1080) // 128MB
        val displayScale = (RS.displayWidthF * RS.displayHeightF) / (1920 * 1080) // 512MB
        val estimatedVRAMmb = framebufferScale * 128L + displayScale * 256L
        val vStartX = rightStartX + scale * 15f
        val vEndX = rightEndX - scale * 15f
        var vStartY = rightEndY - 35f * scale
        val rate = estimatedVRAMmb / RS.totalVRam.toFloat()
        RenderUtils.drawRect(
            vStartX,
            rightEndY - 35f * scale,
            rightEndX - scale * 15f,
            rightEndY - 15f * scale,
            ColorRGB.WHITE.alpha((0.4f * alpha).toInt())
        )
        val color = if (rate <= 0.7f) ColorRGB.WHITE else if (rate <= 0.9f) ColorRGB.YELLOW else ColorRGB.RED
        RenderUtils.drawRect(
            vStartX,
            rightEndY - 35f * scale,
            vStartX + (vEndX - vStartX) * rate.coerceIn(0f, 1f),
            rightEndY - 15f * scale,
            color.alpha((0.8f * alpha).toInt())
        )
        vStartY -= FontRendererMain.getHeight(scale)
        FontRendererMain.drawString(
            "Estimated VRAM",
            vStartX,
            vStartY,
            scale = scale * 0.8f,
            color = ColorRGB.WHITE.alpha((alpha * 0.8f).toInt())
        )
        val str = "${format.format(estimatedVRAMmb / 1024f)} GB / ${format.format(RS.totalVRam / 1024f)} GB"
        FontRendererMain.drawString(
            str,
            vEndX - FontRendererMain.getWidth(str, scale * 0.8f),
            vStartY,
            scale = scale * 0.8f,
            color = ColorRGB.WHITE.alpha((alpha * 0.8f).toInt())
        )
    }

    override fun onMouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean {
        SubTitle.entries.forEach {
            if (it.selectButton.onMouseClicked(mouseX, mouseY, button)) return true
        }
        return currentSelected.optionsRenderer.onMouseClicked(mouseX, mouseY, button)
    }

    override fun onMouseReleased(mouseX: Int, mouseY: Int, button: Int) {
        currentSelected.optionsRenderer.onMouseReleased(mouseX, mouseY, button)
    }

    fun resetAnimation() {
        leftSideX = 0f
        rightSideX = 0f
        SubTitle.entries.forEach { title ->
            title.selectButton.optionsRenderer.buttons.forEach {
                it.reset()
            }
            title.selectButton.optionsRenderer.scrollOffset = 0
            title.selectButton.optionsRenderer.scrollAccumulation = 0
        }
    }

    override fun onClosed() {
        resetAnimation()
        Configs.saveConfig("configs.json")
    }

    override fun onKeyTyped(keyCode: Int, modifier: Int): Boolean {
        if (keyCode == GLFW.GLFW_KEY_F2 || keyCode == GLFW.GLFW_KEY_ESCAPE) {
            enabled = false
            return true
        }
        return false
    }

    private class SelectButton(
        multiText: MultiText,
        val optionsRenderer: OptionsRenderer
    ) : Component2D {
        override var x = 0f
        override var y = 0f
        override var width = 0f
        override var height = 0f

        private var alphaRate = 0f
        private val updateTimer = Timer()
        private val text by multiText

        override fun onRender2D(mouseX: Double, mouseY: Double, alpha: Float) {
            val scale = min(RS.widthScale, RS.heightScale)
            updateTimer.passedAndReset(33) {
                this.width = FontRendererBig.getWidth(text, scale)
                this.height = FontRendererBig.getHeight(scale)
                alphaRate = alphaRate.converge(
                    when {
                        currentSelected == this -> 100f
                        isHoovered(mouseX.toInt(), mouseY.toInt()) -> 80f
                        else -> 50f
                    },
                    0.5f
                )
            }
            FontRendererBig.drawString(
                text,
                x,
                y,
                ColorRGB.WHITE.alpha((alpha * alphaRate / 100f).toInt()),
                scale
            )
        }

        override fun onMouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int): Boolean {
            if (isHoovered(mouseX, mouseY)) {
                currentSelected = this
                return true
            }
            return false
        }

    }

    private enum class SubTitle(val selectButton: SelectButton) {
        Control(SelectButton("Control".lang("控制设置", "控制設定"), OptionsRenderer(ControlOption))),
        Video(SelectButton("Video".lang("视频设置", "視訊設定"), OptionsRenderer(VideoOption))),
        Audio(SelectButton("Audio".lang("音频设置", "音頻設定"), OptionsRenderer(AudioOption))),
        Crosshair(SelectButton("Crosshair".lang("准星调整", "準星設定"), OptionsRenderer(CrosshairOption))),
        AimAssist(SelectButton("AimAssist".lang("辅助瞄准", "輔助瞄準"), OptionsRenderer(AimAssistOption))),
        Accessibility(SelectButton("Accessibility".lang("辅助功能", "輔助功能"), OptionsRenderer(AccessibilityOption)))
    }

}