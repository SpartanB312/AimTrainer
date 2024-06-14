package net.spartanb312.everett.game.render

import net.spartanb312.everett.AimTrainer
import net.spartanb312.everett.game.Language.lang
import net.spartanb312.everett.game.option.impls.AccessibilityOption.jitter
import net.spartanb312.everett.game.option.impls.AccessibilityOption.jitterRange
import net.spartanb312.everett.game.option.impls.AccessibilityOption.language
import net.spartanb312.everett.game.option.impls.AccessibilityOption.ping
import net.spartanb312.everett.game.option.impls.AccessibilityOption.pingSimulate
import net.spartanb312.everett.game.option.impls.AccessibilityOption.pktLossRate
import net.spartanb312.everett.game.option.impls.AccessibilityOption.pktLossSimulate
import net.spartanb312.everett.game.option.impls.VideoOption
import net.spartanb312.everett.game.render.scene.SceneManager
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.drawing.RenderUtils
import net.spartanb312.everett.graphics.texture.drawTexture
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.language.Languages

object StatRenderer {

    private val pktLossStr by "UNSTABLE : PACKET LOSS".lang("不稳定 : 封包丢失", "不穩定 : 封包丟失")
    private val latencyStr by "UNSTABLE : LATENCY".lang("不稳定 : 延迟", "不穩定 : 延遲")
    private val jitterStr by "UNSTABLE : JITTER".lang("不稳定 : 抖动", "不穩定 : 顫動")

    fun onRender() {
        if (!AimTrainer.isReady) return
        val scale = RS.generalScale
        var startX = if (SceneManager.inTraining) RS.widthF else RS.widthF * 0.96f
        val startY = if (SceneManager.inTraining) 0f else RS.heightF * 0.02f
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
            startX -= width * 1.05f
        }

        val ping = ping

        val pktLoss = pktLossSimulate.value && pktLossRate >= 10
        val latency = ping >= 250
        val jitter = pingSimulate.value && jitter.value && jitterRange >= 30

        if (VideoOption.ping || pktLoss || latency || jitter) {
            RenderUtils.drawRect(startX - width, startY, startX, startY + height, ColorRGB.BLACK.alpha(32))
            FontRendererBold.drawCenteredString(
                "$ping ms",
                startX - width / 2f,
                startY + height / 2f,
                scale = scale * 1.2f
            )
            startX -= width * 1.05f

            fun drawWarning(str: String) {
                val bold = language != Languages.ChineseCN && language != Languages.ChineseTW
                val renderer = if (bold) FontRendererBold else FontRendererMain
                val texW = scale * 8f
                val centerY = startY + height / 2f
                val barWidth = renderer.getWidth(str, scale * if (bold) 1.2f else 0.6f) + texW * 4.5f
                RenderUtils.drawRect(startX - barWidth, startY, startX, startY + height, ColorRGB.BLACK.alpha(32))
                TextureManager.warning.drawTexture(
                    startX - barWidth + texW,
                    centerY - texW * 0.9f,
                    startX - barWidth + texW * 3f,
                    centerY + texW * 1.1f,
                    colorRGB = ColorRGB.RED
                )
                startX += texW * 3.5f
                renderer.drawString(
                    str,
                    startX - barWidth,
                    centerY - FontRendererBold.getHeight(scale * 0.6f),
                    scale = scale * if (bold) 1.2f else 0.6f,
                    color = ColorRGB.RED
                )
            }

            if (pktLoss) drawWarning(pktLossStr)
            else if (latency) drawWarning(latencyStr)
            else if (jitter) drawWarning(jitterStr)
        }
    }

}