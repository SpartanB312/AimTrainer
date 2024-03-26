package net.spartanb312.boar.game.render.gui.impls

import net.spartanb312.boar.AimTrainer
import net.spartanb312.boar.game.render.Background
import net.spartanb312.boar.game.render.FontRendererMain
import net.spartanb312.boar.game.render.FontRendererROG
import net.spartanb312.boar.game.render.TextureManager
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

    init {
        modules.add(Main::class.java.getAnnotation(Module::class.java))
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
        val scale = max(RS.widthScale, RS.heightScale) * 2.5f
        TextureManager.everett.drawTexture(
            RS.centerXF - scale * 320f,
            RS.centerYF - scale * 180f,
            RS.centerXF + scale * 320f,
            RS.centerYF + scale * 180f,
            colorRGB = ColorRGB.WHITE.alpha(224)
        )

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
        // Correct offset
        if (scrollOffset + 6 > modules.size) scrollOffset = modules.size - 6
        else if (scrollOffset < 0) scrollOffset = 0
    }

    class Button(
        private val text: String,
        private val startX: Float,
        private val startY: Float,
        private val endX: Float,
        private val endY: Float,
        val action: () -> Unit,
    ) {
        fun onRender(mouseX: Double, mouseY: Double) {
            RenderUtils.drawRect(
                startX,
                startY,
                endX,
                endY,
                ColorRGB.WHITE.alpha(if (isHoovered(mouseX, mouseY)) 128 else 64)
            )
            FontRendererMain.drawCenteredString(this.text, (endX + startX) / 2f, (endY + this.startY) / 2f)
        }

        fun isHoovered(mouseX: Double, mouseY: Double): Boolean =
            mouseX in startX..this.endX && mouseY in this.startY..endY
    }

    private val buttons = arrayOf(
        Button("Add", 200f, 420f, 340f, 460f) { },
        Button("Refresh", 357f, 420f, 497f, 460f) {},
        Button("Back", 514f, 420f, 654f, 460f) {})


    override fun onKeyTyped(keyCode: Int, modifier: Int): Boolean {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            Render2DManager.popScreen()
            return true
        }
        return false
    }

}