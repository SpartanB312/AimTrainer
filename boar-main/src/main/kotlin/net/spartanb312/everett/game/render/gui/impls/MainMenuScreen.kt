package net.spartanb312.everett.game.render.gui.impls

import net.spartanb312.everett.AimTrainer.AIM_TRAINER_VERSION
import net.spartanb312.everett.game.Language
import net.spartanb312.everett.game.Language.lang
import net.spartanb312.everett.game.forge.Forge
import net.spartanb312.everett.game.render.*
import net.spartanb312.everett.game.render.gui.GuiScreen
import net.spartanb312.everett.game.render.gui.Render2DManager
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.drawing.RenderUtils
import net.spartanb312.everett.graphics.font.drawColoredString
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.language.MultiText
import net.spartanb312.everett.utils.math.ConvergeUtil.converge
import net.spartanb312.everett.utils.timing.Timer
import org.lwjgl.glfw.GLFW
import kotlin.math.max
import kotlin.math.min
import kotlin.system.exitProcess

object MainMenuScreen : GuiScreen() {

    private val sideButtons = listOf(
        SideButton("Quick Start".lang("快速训练", "快速訓練")) { TrainingScreen.quickStart() },
        SideButton("Customize".lang("自定训练", "自訂訓練")) { Render2DManager.displayScreen(CustomGameScreen) },
        SideButton("Trainings".lang("训练场", "訓練場")) { Render2DManager.displayScreen(TrainingScreen) },
        SideButton("Options".lang("设定", "設定")) { Render2DManager.displayScreen(OptionScreen) },
        SideButton("Exit".lang("退出", "離開")) { exitProcess(0) }
    )
    private val extendedButtons = listOf(
        ExtendedButton("s") { Forge.start() },
        ExtendedButton("E") { Render2DManager.displayScreen(OptionScreen) },
        ExtendedButton("p") { Render2DManager.displayScreen(ModuleScreen) }
    )

    override fun onInit() {
        NotificationRenderer.clearAll()
        Language.update(true)
        sideButtons.forEach { it.scale = 0f }
    }

    override fun onRender(mouseX: Double, mouseY: Double) {
        val scale = min(RS.widthScale, RS.heightScale)
        Background.renderBackground(mouseX, mouseY)

        val leftX = 0.07f * RS.widthF
        var startY = 0.08f * RS.heightF

        // Title
        FontRendererROG.drawColoredString(
            "Aim Trainer",
            leftX,
            startY,
            saturation = 0.8f,
            shadowDepth = 2f * scale,
            scale = scale
        )

        startY += FontRendererROG.getHeight()
        startY += 100 * scale
        startY = max(startY, RS.heightF * 0.25f)
        if (startY >= RS.heightF * 0.3f) startY = RS.heightF * 0.3f

        // Select Button
        sideButtons.forEach {
            it.onRender(mouseX, mouseY, leftX + 5f, startY, scale)
            startY += it.height + FontRendererMain.getHeight(0.25f * scale)
        }

        // Extended Button
        var startX = leftX + 5f
        extendedButtons.forEach {
            it.onRender(mouseX, mouseY, startX, RS.heightF * 0.85f, scale)
            startX += it.width * 1.3f * scale
        }

        // Nameplate
        val nameplateWidth = 360f * scale * 1.35f
        val nameplateHeight = 52f * scale * 1.35f
        NameplatesRenderer.currentNameplate.onRender(
            RS.widthF * 0.93f - nameplateWidth,
            0.115f * RS.heightF,
            RS.widthF * 0.93f,
            0.115f * RS.heightF + nameplateHeight
        )
        val nameStartXOffset = 300f * scale * 1.35f
        FontRendererMain.drawColoredString(
            //"A&bg&fe&bn&rt4&b1&f6&bS&rHD",
            "FluixCarvin1186",
            RS.widthF * 0.93f - nameStartXOffset,
            RS.heightF * 0.115f + 34f * scale,
            //ColorRGB(255, 105, 180).alpha(224),
            scale = scale,
            shadowDepth = 1f
        )
        FontRendererMain.drawString(
            "[UNSC]",
            RS.widthF * 0.93f - nameStartXOffset,
            RS.heightF * 0.115f + 5f * scale,
            ColorRGB.WHITE.alpha(160),
            scale = scale * 0.9f
        )

        // Version
        val width = FontRendererMain.getWidth("Version: $AIM_TRAINER_VERSION") + 10
        val height = FontRendererMain.getHeight()
        FontRendererMain.drawString("Version: $AIM_TRAINER_VERSION", RS.widthF - width, RS.heightF - height)
    }

    override fun onMouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean {
        for (b in sideButtons) {
            if (b.onMouseClicked(mouseX.toDouble(), mouseY.toDouble(), button)) return true
        }
        for (b in extendedButtons) {
            if (b.onMouseClicked(mouseX.toDouble(), mouseY.toDouble(), button)) return true
        }
        return false
    }

    class SideButton(
        multiText: MultiText,
        var x: Float = 0f,
        var y: Float = 0f,
        private val action: () -> Unit,
    ) {

        val text by multiText
        var scale = 2f

        private var generalScale = 1f
        val width get() = FontRendererMain.getWidth(text, scale * generalScale)
        val height get() = FontRendererMain.getHeight(scale * generalScale)

        private val startTime = System.currentTimeMillis()
        private val timer = Timer()

        fun onRender(mouseX: Double, mouseY: Double, x: Float = this.x, y: Float = this.y, generalScale: Float) {
            this.x = x
            this.y = y
            this.generalScale = generalScale
            timer.passedAndReset(17) {
                scale = (scale * 100f).converge(if (isHoovered(mouseX, mouseY)) 250f else 200f, 0.2f) / 100f
            }
            //RenderUtils.drawRect(x, y, x + width, y + height, ColorRGB.BLUE)
            val offset = ((System.currentTimeMillis() - startTime) % 5000) / 5000f
            val color = ColorRGB(0, 186, 253)
            FontRendererBig.drawGradientStringWithShadow(
                text,
                x,
                y,
                arrayOf(
                    ColorRGB.WHITE.circle(color, offset),
                    ColorRGB.WHITE.circle(color, offset - 0.125f),
                    ColorRGB.WHITE.circle(color, offset - 0.25f),
                ),
                scale = scale * generalScale * 0.5f
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

    class ExtendedButton(
        val text: String,
        var x: Float = 0f,
        var y: Float = 0f,
        val width: Float = 55f,
        val height: Float = 55f,
        private val action: () -> Unit,
    ) {

        private var generalScale = 1f

        private val generalColor = ColorRGB(101, 176, 210)
        private val lightColor = ColorRGB(194, 247, 254)

        fun onRender(mouseX: Double, mouseY: Double, x: Float = this.x, y: Float = this.y, generalScale: Float) {
            this.x = x
            this.y = y
            this.generalScale = generalScale
            val isHoovered = isHoovered(mouseX, mouseY)
            val width = width * generalScale
            val height = height * generalScale
            RenderUtils.drawRect(x, y, x + width, y + height, generalColor.alpha(if (isHoovered) 128 else 64))
            RenderUtils.drawRectOutline(x, y, x + width, y + height, 2f * generalScale, lightColor.alpha(64))
            FontRendererIcon.drawCenteredString(
                text,
                x + width / 2f,
                y + height / 2f,
                ColorRGB.WHITE.alpha(if (isHoovered) 255 else 224),
                scale = generalScale * 0.75f
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
            return mouseX in x..(x + width * generalScale) && mouseY in y..(y + height * generalScale)
        }

    }

}