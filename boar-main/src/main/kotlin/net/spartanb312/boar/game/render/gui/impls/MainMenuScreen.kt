package net.spartanb312.boar.game.render.gui.impls

import net.spartanb312.boar.AimTrainer.AIM_TRAINER_VERSION
import net.spartanb312.boar.game.Academy
import net.spartanb312.boar.game.render.Background
import net.spartanb312.boar.game.render.FontRendererMain
import net.spartanb312.boar.game.render.FontRendererROG
import net.spartanb312.boar.game.render.gui.GuiScreen
import net.spartanb312.boar.game.render.gui.Render2DManager
import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.graphics.font.drawColoredString
import net.spartanb312.boar.language.Language
import net.spartanb312.boar.language.Language.m
import net.spartanb312.boar.language.MultiText
import net.spartanb312.boar.utils.color.ColorRGB
import net.spartanb312.boar.utils.math.ConvergeUtil.converge
import net.spartanb312.boar.utils.timing.Timer
import org.lwjgl.glfw.GLFW
import kotlin.math.min
import kotlin.system.exitProcess

object MainMenuScreen : GuiScreen() {

    private val sideButtons = listOf(
        SideBar.SideButton("Quick Play".m("快速训练", "快速訓練")) { Academy.start() },
        SideBar.SideButton("Trainings".m("训练场", "訓練場")) { Render2DManager.displayScreen(TrainingScreen) },
        SideBar.SideButton("Modules".m("模块", "模塊")) { Render2DManager.displayScreen(ModuleScreen) },
        SideBar.SideButton("Options".m("设定", "設定")) { Render2DManager.displayScreen(OptionScreen) },
        SideBar.SideButton("Exit".m("退出", "離開")) { exitProcess(0) }
    )

    override fun onInit() {
        Language.update(true)
    }

    override fun onRender(mouseX: Double, mouseY: Double) {
        val scale = min(RS.widthScale, RS.heightScale)
        Background.renderBackground(mouseX, mouseY)

        val leftX = 0.07f * RS.widthF
        var startY = 0.08f * RS.heightF

        // Title
        FontRendererROG.drawColoredString("Aim Trainer", leftX, startY, saturation = 0.8f, shadowDepth = 2f * scale)

        startY += FontRendererROG.getHeight()
        startY += 100 * scale

        // Select Button
        sideButtons.forEach {
            it.onRender(mouseX, mouseY, leftX + 5f, startY)
            startY += (it.height + FontRendererMain.getHeight(0.3f))
        }

        // Version
        val width = FontRendererMain.getWidth("Version: $AIM_TRAINER_VERSION") + 10
        val height = FontRendererMain.getHeight()
        FontRendererMain.drawString("Version: $AIM_TRAINER_VERSION", RS.widthF - width, RS.heightF - height)
    }

    override fun onMouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean {
        for (b in sideButtons) {
            if (b.onMouseClicked(mouseX.toDouble(), mouseY.toDouble(), button)) return true
        }
        return false
    }

    object SideBar {
        class SideButton(
            private val multiText: MultiText,
            var x: Float = 0f,
            var y: Float = 0f,
            private val action: () -> Unit,
        ) {

            val text by multiText
            var scale = 2f; private set

            val width get() = FontRendererMain.getWidth(text, scale)
            val height get() = FontRendererMain.getHeight(scale)

            private val startTime = System.currentTimeMillis()
            private val timer = Timer()

            fun onRender(mouseX: Double, mouseY: Double, x: Float = this.x, y: Float = this.y) {
                this.x = x
                this.y = y
                timer.passedAndReset(17) {
                    scale = (scale * 100f).converge(if (isHoovered(mouseX, mouseY)) 250f else 200f, 0.2f) / 100f
                }
                //RenderUtils.drawRect(x, y, x + width, y + height, ColorRGB.BLUE)
                val offset = ((System.currentTimeMillis() - startTime) % 5000) / 5000f
                val color = ColorRGB(0, 186, 253)
                FontRendererMain.drawGradientStringWithShadow(
                    text,
                    x,
                    y,
                    arrayOf(
                        ColorRGB.WHITE.circle(color, offset),
                        ColorRGB.WHITE.circle(color, offset - 0.125f),
                        ColorRGB.WHITE.circle(color, offset - 0.25f),
                    ),
                    scale = scale
                )
            }

            fun onMouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
                if (button == GLFW.GLFW_MOUSE_BUTTON_1 && isHoovered(mouseX, mouseY)) {
                    action.invoke()
                    return true
                }
                return false
            }

            fun isHoovered(mouseX: Double, mouseY: Double): Boolean {
                return mouseX in x..(x + width) && mouseY in y..(y + height)
            }
        }
    }

}