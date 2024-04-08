package net.spartanb312.boar.game.render.gui.impls

import net.spartanb312.boar.AimTrainer
import net.spartanb312.boar.game.render.*
import net.spartanb312.boar.game.render.gui.GuiScreen
import net.spartanb312.boar.game.render.gui.Render2DManager
import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.graphics.RenderSystem
import net.spartanb312.boar.graphics.drawing.RenderUtils
import net.spartanb312.boar.graphics.texture.drawTexture
import net.spartanb312.boar.launch.Main
import net.spartanb312.boar.launch.Module
import net.spartanb312.boar.utils.color.ColorRGB
import org.lwjgl.glfw.GLFW
import kotlin.math.max

object ModuleScreen : GuiScreen() {

    private var scrollOffset = 0

    private val modules = mutableListOf<Module>()
    private val buttons = arrayOf(
        Button("Add", 0f, 0f, 150f, 40f) { }, // 150, 40, 20
        Button("Refresh", 170f, 0f, 320f, 40f) {},
        Button("Back", 340f, 0f, 490f, 40f) { Render2DManager.popScreen() })

    init {
        if (Main.fullMode) modules.add(Main::class.java.getAnnotation(Module::class.java))
        modules.add(RenderSystem.javaClass.getAnnotation(Module::class.java))
        modules.add(AimTrainer.javaClass.getAnnotation(Module::class.java))
    }

    override fun onRender(mouseX: Double, mouseY: Double) {
        Background.renderBackground(mouseX, mouseY)
        FontRendererROG.drawCenteredStringWithShadow("Modules", RS.width / 2f, RS.heightF * 0.05f, scale = 0.8f)
        val clampYT = RS.heightF * 0.1f
        val clampYB = RS.heightF * 0.9f
        RenderUtils.drawRect(0f, 0f, RS.widthF, RS.heightF, ColorRGB.BLACK.alpha(32))
        RenderUtils.drawRect(0f, clampYT, RS.widthF, clampYB, ColorRGB.DARK_GRAY.alpha(84))

        // Spartan
        if (TextureManager.everett?.available == true) {
            val scale = max(RS.widthScale, RS.heightScale) * 2.5f
            TextureManager.everett.drawTexture(
                RS.centerXF - scale * 320f,
                RS.centerYF - scale * 180f,
                RS.centerXF + scale * 320f,
                RS.centerYF + scale * 180f,
                colorRGB = ColorRGB.WHITE.alpha(224)
            )
        }

        scrollOffset = 2
        val startX = RS.widthF * 0.2f
        val endX = RS.widthF * 0.8f
        var startY = clampYT + FontRendererMain.getHeight(0.3f)
        var index = -1
        var count = 0
        modules.forEach { module ->
            val name = module.name
            val version = module.version
            val desc = module.description
            val author = module.author
            index++
            if ((modules.size - index <= 6 || index >= scrollOffset) && count < 6) {
                count++
                val isHoovered = mouseX in startX..endX && mouseY in startY..(startY + FontRendererMain.getHeight() * 2)
                RenderUtils.drawRect(
                    startX,
                    startY,
                    endX,
                    startY + FontRendererMain.getHeight() * 2,
                    ColorRGB.WHITE.alpha(if (isHoovered) 128 else 64)
                )
                FontRendererMain.drawStringWithShadow(
                    "&f$name &r$version",
                    startX + 10f,
                    startY,
                    ColorRGB.GREEN
                )
                startY += FontRendererMain.getHeight()
                FontRendererMain.drawStringWithShadow(
                    "&fDescription: &r$desc",
                    startX + 10f,
                    startY,
                    ColorRGB.AQUA
                )
                val authorInfo = "&fAuthor: &r" + if (author == "") " Unknown" else author
                FontRendererMain.drawStringWithShadow(
                    authorInfo,
                    endX - 10f - FontRendererMain.getWidth(authorInfo),
                    startY,
                    ColorRGB.AQUA
                )
                startY += FontRendererMain.getHeight(1.3f)
            }
        }

        RTO.drawCenteredString("RTO Test", RS.centerX, RS.centerY)

        // Correct offset
        if (scrollOffset + 6 > modules.size) scrollOffset = modules.size - 6
        else if (scrollOffset < 0) scrollOffset = 0

        val scale = max(RS.widthScale, RS.heightScale) * 1.5f
        var sX = RS.centerXF - 245 * scale
        buttons.forEach {
            it.update(sX, RS.centerYF * 1.85f, 150f * scale, 40f * scale)
            sX += 170 * scale
            it.onRender(mouseX, mouseY, scale)
        }
    }

    class Button(
        private val text: String,
        private var startX: Float,
        private var startY: Float,
        private var endX: Float,
        private var endY: Float,
        private val action: () -> Unit,
    ) {
        fun update(startX: Float, startY: Float, width: Float, height: Float) {
            this.startX = startX
            this.startY = startY
            this.endX = startX + width
            this.endY = startY + height
        }

        fun onRender(mouseX: Double, mouseY: Double, scale: Float) {
            RenderUtils.drawRect(
                startX,
                startY,
                endX,
                endY,
                ColorRGB.WHITE.alpha(if (isHoovered(mouseX, mouseY)) 128 else 64)
            )
            FontRendererMain.drawCenteredString(
                this.text,
                (endX + startX) / 2f,
                (endY + this.startY) / 2f,
                scale = scale
            )
        }

        fun onClick(mouseX: Double, mouseY: Double): Boolean {
            return if (isHoovered(mouseX, mouseY)) {
                action.invoke()
                true
            } else false
        }

        fun isHoovered(mouseX: Double, mouseY: Double): Boolean =
            mouseX in startX..this.endX && mouseY in this.startY..endY
    }

    override fun onMouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean {
        buttons.forEach {
            if (it.onClick(mouseX.toDouble(), mouseY.toDouble())) return true
        }
        return false
    }

    override fun onKeyTyped(keyCode: Int, modifier: Int): Boolean {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            Render2DManager.popScreen()
            return true
        }
        return false
    }

}