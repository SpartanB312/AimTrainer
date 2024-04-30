package net.spartanb312.everett.game.render.gui.impls

import net.spartanb312.everett.game.Configs
import net.spartanb312.everett.game.option.impls.*
import net.spartanb312.everett.game.render.Background
import net.spartanb312.everett.game.render.Component2D
import net.spartanb312.everett.game.render.FontRendererBig
import net.spartanb312.everett.game.render.gui.GuiScreen
import net.spartanb312.everett.game.render.gui.Render2DManager
import net.spartanb312.everett.game.render.gui.impls.option.OptionsRenderer
import net.spartanb312.everett.game.render.scene.SceneManager
import net.spartanb312.everett.game.render.scene.impls.DummyScene
import net.spartanb312.everett.graphics.GLHelper
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.drawing.RenderUtils
import net.spartanb312.everett.game.Language
import net.spartanb312.everett.game.Language.m
import net.spartanb312.everett.utils.language.MultiText
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.math.ConvergeUtil.converge
import net.spartanb312.everett.utils.misc.Limiter
import net.spartanb312.everett.utils.timing.Timer
import org.lwjgl.glfw.GLFW
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
        val lineEndY = lineStartY + RS.heightF * 0.006f * scale
        RenderUtils.drawRect(leftSideX, lineStartY, rightSideX, lineEndY, ColorRGB.WHITE.alpha(alpha.toInt()))
        currentSelected.optionsRenderer.onRender2D(mouseX, mouseY, scale, alpha)
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
        Control(SelectButton("Control".m("控制设置", "控制設定"), OptionsRenderer(ControlOption))),
        Video(SelectButton("Video".m("视频设置", "視訊設定"), OptionsRenderer(VideoOption))),
        Audio(SelectButton("Audio".m("音频设置", "音頻設定"), OptionsRenderer(AudioOption))),
        Crosshair(SelectButton("Crosshair".m("准星调整", "準星設定"), OptionsRenderer(CrosshairOption))),
        AimAssist(SelectButton("AimAssist".m("辅助瞄准", "輔助瞄準"), OptionsRenderer(AimAssistOption))),
        Accessibility(SelectButton("Accessibility".m("辅助功能", "輔助功能"), OptionsRenderer(AccessibilityOption)))
    }

}