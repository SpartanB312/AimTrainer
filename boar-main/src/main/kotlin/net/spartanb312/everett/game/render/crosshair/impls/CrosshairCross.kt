package net.spartanb312.everett.game.render.crosshair.impls

import net.spartanb312.everett.game.config.setting.alias
import net.spartanb312.everett.game.config.setting.m
import net.spartanb312.everett.game.render.crosshair.Crosshair
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.drawing.RenderUtils
import net.spartanb312.everett.graphics.matrix.MatrixLayerStack
import net.spartanb312.everett.utils.color.ColorRGB
import kotlin.math.min

object CrosshairCross : Crosshair(0f) {

    private val size by setting("Cross-Size", 15f, 1f..50f, 0.5f).alias("Size")
        .m("准星大小", "準星大小")

    override var clickTime = System.currentTimeMillis()

    override fun MatrixLayerStack.onRender(
        centerX: Float,
        centerY: Float,
        fov: Float,
        colorRGB: ColorRGB
    ) {
        val scaledSize = min(RS.widthScale, RS.heightScale) * size
        RenderUtils.drawLine(
            centerX - scaledSize,
            centerY,
            centerX + scaledSize,
            centerY,
            scaledSize / 5f,
            colorRGB
        )
        RenderUtils.drawLine(
            centerX,
            centerY - scaledSize,
            centerX,
            centerY + scaledSize,
            scaledSize / 5f,
            colorRGB
        )
    }

}