package net.spartanb312.boar.game.render.gui.impls

import net.spartanb312.boar.game.render.FontRendererBig
import net.spartanb312.boar.game.render.gui.GuiScreen
import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.graphics.drawing.RenderUtils
import net.spartanb312.boar.utils.color.ColorRGB
import kotlin.math.min

open class ScoreboardScreen : GuiScreen() {

    val scoreboard = mutableMapOf<String, String>()

    override fun onRender(mouseX: Double, mouseY: Double) {
        RenderUtils.drawRect(0, 0, RS.width, RS.height, ColorRGB.BLACK.alpha(128))
        val scale = min(RS.widthScale, RS.heightScale)
        val blockWidth = 350f * scale
        val blockHeight = 500f * scale
        val gap = 30f * scale
        val width = scoreboard.size * blockWidth + (scoreboard.size - 1).coerceAtLeast(0) * gap

        RenderUtils.drawRect(
            RS.centerX - width / 2f - gap,
            RS.centerY - blockHeight / 2f - gap,
            RS.centerX + width / 2f + gap,
            RS.centerY + blockHeight / 2f + gap,
            ColorRGB.BLACK.alpha(128)
        )

        var startX = RS.centerX - width / 2f
        val startY = RS.centerY - blockHeight / 2f
        scoreboard.forEach { (type, score) ->
            RenderUtils.drawRect(startX, startY, startX + blockWidth, startY + blockHeight, ColorRGB.WHITE.alpha(32))
            FontRendererBig.drawCenteredString(
                type,
                startX + blockWidth / 2f,
                startY + blockHeight * 0.15f,
                scale = scale * 0.9f
            )
            FontRendererBig.drawCenteredString(
                score,
                startX + blockWidth / 2f,
                startY + blockHeight * 0.58f,
                scale = scale * 1.7f
            )
            startX += gap
            startX += blockWidth
        }

    }

}