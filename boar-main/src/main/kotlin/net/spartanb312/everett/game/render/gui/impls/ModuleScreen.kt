package net.spartanb312.everett.game.render.gui.impls

import net.spartanb312.everett.AimTrainer
import net.spartanb312.everett.game.render.Background
import net.spartanb312.everett.game.render.FontRendererMain
import net.spartanb312.everett.game.render.FontRendererROG
import net.spartanb312.everett.game.render.TextureManager
import net.spartanb312.everett.game.render.gui.GuiScreen
import net.spartanb312.everett.game.render.gui.Render2DManager
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.RenderSystem
import net.spartanb312.everett.graphics.drawing.RenderUtils
import net.spartanb312.everett.graphics.texture.drawTexture
import net.spartanb312.everett.launch.Main
import net.spartanb312.everett.launch.Module
import net.spartanb312.everett.utils.color.ColorRGB
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
        modules.add(Main::class.java.getAnnotation(Module::class.java))
        modules.add(RenderSystem.javaClass.getAnnotation(Module::class.java))
        modules.add(AimTrainer.javaClass.getAnnotation(Module::class.java))
    }

    private val width2 = FontRendererMain.getWidth("Description: ", 1.25f)
    private val width3 = FontRendererMain.getWidth("Author: ", 1.25f)

    override fun onRender(mouseX: Double, mouseY: Double) {
        Background.renderBackground(mouseX, mouseY)
        val scale = max(RS.widthF / 2560f, RS.heightF / 1369f)
        FontRendererROG.drawCenteredStringWithShadow("Modules", RS.width / 2f, RS.heightF * 0.05f, scale = scale)
        val clampYT = RS.heightF * 0.1f
        val clampYB = RS.heightF * 0.9f
        RenderUtils.drawRect(0f, 0f, RS.widthF, RS.heightF, ColorRGB.BLACK.alpha(32))
        RenderUtils.drawRect(0f, clampYT, RS.widthF, clampYB, ColorRGB.DARK_GRAY.alpha(84))

        // Spartan
        if (TextureManager.everett.available) {
            val scale1 = RS.generalScale * 2.5f
            TextureManager.everett.drawTexture(
                RS.centerXF - scale1 * 320f,
                RS.centerYF - scale1 * 180f,
                RS.centerXF + scale1 * 320f,
                RS.centerYF + scale1 * 180f,
                colorRGB = ColorRGB.WHITE.alpha(224)
            )
        }

        scrollOffset = 2
        val startX = RS.widthF * 0.2f
        val endX = RS.widthF * 0.8f
        var startY = clampYT + FontRendererMain.getHeight(0.3f * scale)
        var index = -1
        var count = 0
        val stepIn = scale * 10f
        modules.forEach { module ->
            val name = module.name
            val version = module.version
            val desc = module.description
            val author = module.author
            index++
            if ((modules.size - index <= 6 || index >= scrollOffset) && count < 6) {
                count++
                val endY = startY + FontRendererMain.getHeight(scale * 2.5f)
                val isHoovered = mouseX in startX..endX && mouseY in startY..endY
                RenderUtils.drawRect(
                    startX,
                    startY,
                    endX,
                    endY,
                    ColorRGB.WHITE.alpha(if (isHoovered) 128 else 64)
                )

                // Name
                val nameStr = "$name "
                val width1 = FontRendererMain.getWidth(nameStr, scale * 1.25f)
                FontRendererMain.drawStringWithShadow(nameStr, startX + stepIn, startY, scale = scale * 1.25f)
                FontRendererMain.drawStringWithShadow(
                    version,
                    startX + stepIn + width1,
                    startY,
                    ColorRGB.GREEN,
                    scale = scale * 1.25f
                )
                startY += FontRendererMain.getHeight(scale * 1.25f)

                // Description
                FontRendererMain.drawStringWithShadow(
                    "Description: ",
                    startX + stepIn,
                    startY,
                    scale = scale * 1.25f
                )
                FontRendererMain.drawStringWithShadow(
                    desc,
                    startX + stepIn + width2 * scale,
                    startY,
                    ColorRGB.AQUA,
                    scale = scale * 1.25f
                )

                // Author
                val authorInfo = if (author == "") " Unknown" else author
                val width4 = FontRendererMain.getWidth(authorInfo, 1.25f)
                FontRendererMain.drawStringWithShadow(
                    "Author: ",
                    endX - (10f + width3 + width4) * scale,
                    startY,
                    scale = scale * 1.25f
                )
                FontRendererMain.drawStringWithShadow(
                    if (author == "") " Unknown" else author,
                    endX - (10f + width4) * scale,
                    startY,
                    ColorRGB.AQUA,
                    scale = scale * 1.25f
                )
                startY += FontRendererMain.getHeight(1.3f * scale * 1.25f)
            }
        }

        // Correct offset
        if (scrollOffset + 6 > modules.size) scrollOffset = modules.size - 6
        else if (scrollOffset < 0) scrollOffset = 0

        var sX = RS.centerXF - 490 * scale
        buttons.forEach {
            it.update(sX, RS.centerYF * 1.85f, 300f * scale, 70f * scale)
            sX += 340 * scale
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
                scale = scale * 2f
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