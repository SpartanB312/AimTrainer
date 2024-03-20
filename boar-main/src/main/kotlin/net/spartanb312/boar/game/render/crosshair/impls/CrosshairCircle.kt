package net.spartanb312.boar.game.render.crosshair.impls

import net.spartanb312.boar.game.config.setting.alias
import net.spartanb312.boar.game.config.setting.m
import net.spartanb312.boar.game.render.crosshair.Crosshair
import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.graphics.drawing.RenderUtils
import net.spartanb312.boar.utils.color.ColorRGB

object CrosshairCircle : Crosshair(0f) {

    private val size by setting("Circle-Size", 10f, 1f..20f, 0.25f).alias("Size")
        .m("准星大小", "準星大小")
    private val lineWidth by setting("Circle-LineWidth", 2f, 0.1f..5f, step = 0.05f).alias("Line Width")
        .m("线宽度", "綫寬度")

    override var clickTime = System.currentTimeMillis()

    override fun onRender(fov: Float, test: Boolean, colorRGB: ColorRGB) {
        val centerX = if (test) 0f else RS.centerXF
        val centerY = if (test) 0f else RS.centerYF
        RenderUtils.drawArcOutline(centerX, centerY, size, 0f..360f, 0, lineWidth, colorRGB)
    }

}