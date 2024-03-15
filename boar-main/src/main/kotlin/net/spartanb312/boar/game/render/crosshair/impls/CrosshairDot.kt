package net.spartanb312.boar.game.render.crosshair.impls

import net.spartanb312.boar.game.config.setting.alias
import net.spartanb312.boar.game.render.crosshair.Crosshair
import net.spartanb312.boar.graphics.GLHelper
import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.graphics.drawing.RenderUtils
import net.spartanb312.boar.utils.color.ColorRGB

object CrosshairDot : Crosshair(0f) {

    private val size by setting("Dot-Size", 10f, 1f..20f, 0.25f).alias("Size")

    override var clickTime = System.currentTimeMillis()

    override fun onRender(fov: Float, test: Boolean, colorRGB: ColorRGB) {
        val centerX = if (test) 0f else RS.centerXF
        val centerY = if (test) 0f else RS.centerYF
        GLHelper.pointSmooth = true
        RenderUtils.drawPoint(centerX, centerY, size, colorRGB)
    }

}