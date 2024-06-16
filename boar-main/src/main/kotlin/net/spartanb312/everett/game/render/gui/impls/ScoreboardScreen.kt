package net.spartanb312.everett.game.render.gui.impls

import net.spartanb312.everett.game.medal.MedalCounter
import net.spartanb312.everett.game.render.Background
import net.spartanb312.everett.game.render.FontRendererBig
import net.spartanb312.everett.game.render.FontRendererBold
import net.spartanb312.everett.game.render.FontRendererMain
import net.spartanb312.everett.game.render.gui.GuiScreen
import net.spartanb312.everett.game.render.gui.Render2DManager
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.drawing.RenderUtils
import net.spartanb312.everett.graphics.texture.drawTexture
import net.spartanb312.everett.utils.color.ColorRGB
import org.lwjgl.glfw.GLFW
import kotlin.math.max
import kotlin.math.min

open class ScoreboardScreen(
    private val score: Int,
    private val category: String,
    private val trainingName: String,
    private val duration: Int,
    private val scoreboard: Map<String, String>,
    private val medalCounter: MedalCounter
) : GuiScreen() {

    override fun onRender(mouseX: Double, mouseY: Double) {
        Background.renderBackground(mouseX, mouseY)
        RenderUtils.drawRect(0, 0, RS.width, RS.height, ColorRGB.BLACK.alpha(128))
        val scale = min(RS.widthScale, RS.heightScale)

        // Title
        val str = "${category.uppercase()}: &b${trainingName.uppercase()}"
        val titleWidth = FontRendererBig.getWidth(str, scale * 0.8f)
        FontRendererBig.drawString(str, RS.widthF * 0.95f - titleWidth, RS.heightF * 0.08f, scale = scale * 0.8f)
        val str2 = "TRAINING COMPLETED · DURATION ${duration.durationToString()}"
        val subtitleWidth = FontRendererMain.getWidth(str2, scale * 0.8f)
        FontRendererMain.drawString(
            str2,
            RS.widthF * 0.95f - subtitleWidth,
            RS.heightF * 0.08f + FontRendererBig.getHeight(scale * 0.8f),
            scale = scale * 0.8f
        )

        // Score
        val leftBound = RS.widthF * 0.05f + scale * 5f
        FontRendererBold.drawString("SCORE", leftBound, RS.heightF * 0.11f, scale = scale)
        FontRendererBig.drawString(
            score.toString(),
            leftBound,
            RS.heightF * 0.11f + FontRendererMain.getHeight(scale),
            scale = scale * 1.6f
        )

        // Types
        val blockWidth = 250f * scale
        val blockHeight = 350f * scale
        var startX = RS.widthF * 0.05f
        var startY = RS.heightF * 0.28f
        var typeCount = 0
        scoreboard.forEach { (type, score) ->
            typeCount++
            if (typeCount <= 5) {
                RenderUtils.drawRect(
                    startX,
                    startY,
                    startX + blockWidth,
                    startY + blockHeight,
                    ColorRGB.WHITE.alpha(32)
                )
                FontRendererBig.drawCenteredString(
                    type.uppercase(),
                    startX + blockWidth / 2f,
                    startY + blockHeight * 0.15f,
                    scale = scale * 0.6f
                )
                FontRendererBig.drawCenteredString(
                    score,
                    startX + blockWidth / 2f,
                    startY + blockHeight * 0.58f,
                    scale = scale * 0.9f
                )
                startX += blockWidth * 1.05f
            }
        }
        if (scoreboard.size < 5) {
            repeat(5 - scoreboard.size) {
                RenderUtils.drawRect(
                    startX,
                    startY,
                    startX + blockWidth,
                    startY + blockHeight,
                    ColorRGB.WHITE.alpha(32)
                )
                startX += blockWidth * 1.05f
            }
        }

        // Medal
        startX = RS.widthF * 0.05f
        startY += blockHeight * 1.2f
        startY = max(startY, RS.heightF * 0.65f)
        val medalWidth = 149f * scale
        val iconOffset = 40f * scale
        var count = 0
        if (medalCounter.medals.isEmpty()) {
            RenderUtils.drawRect(
                startX,
                startY,
                startX + medalWidth,
                startY + medalWidth,
                ColorRGB.DARK_GRAY.alpha(32)
            )
        }
        var renderCount = 0
        for ((medal, amount) in medalCounter.medals.toList().sortedByDescending { it.first.priority }) {
            renderCount++
            if (renderCount > 8) continue
            val topColor = ColorRGB.BLACK.alpha(32)
            val bottomColor = medal.level.color.alpha(64)
            RenderUtils.drawRect(
                startX,
                startY,
                startX + medalWidth,
                startY + medalWidth,
                ColorRGB.DARK_GRAY.alpha(64)
            )
            RenderUtils.drawGradientRect(
                startX,
                startY,
                startX + medalWidth,
                startY + medalWidth,
                topColor,
                topColor,
                bottomColor,
                bottomColor
            )
            medal.texture?.drawTexture(
                startX + iconOffset,
                startY + iconOffset * 0.9f,
                startX + medalWidth - iconOffset,
                startY + medalWidth - iconOffset * 1.1f
            )
            if (amount > 1) {
                FontRendererMain.drawCenteredStringWithShadow(
                    amount.toString(),
                    startX + medalWidth / 2f,
                    startY + medalWidth * 0.8f,
                    scale = scale * 0.8f
                )
            }
            startX += medalWidth * 1.1f
            count++
        }
    }

    override fun onKeyTyped(keyCode: Int, modifier: Int): Boolean {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            Render2DManager.popScreen()
            return true
        }
        return false
    }

    private fun Int.durationToString(): String {
        return when (this) {
            in 0 until 60 -> if (this > 9) "0:$this" else "0:0$this"
            in 60 until 3600 -> {
                val min = this / 60
                val sec = this - min * 60
                if (sec > 9) "$min:$sec" else "$min:0$sec"
            }

            else -> "0:00"
        }
    }

}