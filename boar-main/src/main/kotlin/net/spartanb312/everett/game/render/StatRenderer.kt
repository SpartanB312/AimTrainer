package net.spartanb312.everett.game.render

import net.spartanb312.everett.game.option.impls.AccessibilityOption
import net.spartanb312.everett.game.option.impls.VideoOption
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.drawing.RenderUtils
import net.spartanb312.everett.utils.color.ColorRGB
import kotlin.math.max

object StatRenderer {

    fun onRender() {
        val scale = max(RS.widthScale, RS.heightScale)
        var startX = RS.widthF * 0.96f
        val startY = RS.heightF * 0.02f
        val width = 90f * scale
        val height = 30f * scale

        if (VideoOption.fps) {
            RenderUtils.drawRect(startX - width, startY, startX, startY + height, ColorRGB.BLACK.alpha(32))
            FontRendererBold.drawCenteredString(
                "${RS.averageFPS.toInt()} FPS",
                startX - width / 2f,
                startY + height / 2f,
                scale = scale * 1.2f
            )
            startX -= width * 1.1f
        }

        if (VideoOption.ping) {
            RenderUtils.drawRect(startX - width, startY, startX, startY + height, ColorRGB.BLACK.alpha(32))
            val ping = AccessibilityOption.ping
            FontRendererBold.drawCenteredString(
                "$ping ms",
                startX - width / 2f,
                startY + height / 2f,
                scale = scale * 1.2f
            )
            startX -= width * 1.1f
            if (ping >= 250) {
                RenderUtils.drawRect(startX - width * 2, startY, startX, startY + height, ColorRGB.BLACK.alpha(32))
                FontRendererBold.drawCenteredString(
                    "UNSTABLE: LATENCY",
                    startX - width,
                    startY + height / 2f,
                    scale = scale * 1.2f,
                    color = ColorRGB.RED
                )
            } else if (AccessibilityOption.pingSimulate.value && AccessibilityOption.jitter && AccessibilityOption.jitterRange >= 30) {
                RenderUtils.drawRect(startX - width * 1.8f, startY, startX, startY + height, ColorRGB.BLACK.alpha(32))
                FontRendererBold.drawCenteredString(
                    "UNSTABLE: JITTER",
                    startX - width * 0.9f,
                    startY + height / 2f,
                    scale = scale * 1.2f,
                    color = ColorRGB.RED
                )
            }
        }
    }

}