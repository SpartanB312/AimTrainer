package net.spartanb312.boar.game.render.crosshair.impls

import net.spartanb312.boar.game.config.setting.alias
import net.spartanb312.boar.game.render.crosshair.Crosshair
import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.graphics.drawing.RenderUtils
import net.spartanb312.boar.utils.color.ColorRGB
import kotlin.math.min

object CrosshairCross : Crosshair(0f) {

    private val size by setting("Cross-Size", 15f, 1f..50f, 0.5f).alias("Size")

    override var clickTime = System.currentTimeMillis()

    override fun onRender(fov: Float, test: Boolean, colorRGB: ColorRGB) {
        val centerX = if (test) 0f else RS.centerXF
        val centerY = if (test) 0f else RS.centerYF
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