package net.spartanb312.everett.game.render.crosshair.impls

import net.spartanb312.everett.game.config.setting.alias
import net.spartanb312.everett.game.config.setting.m
import net.spartanb312.everett.game.render.crosshair.Crosshair
import net.spartanb312.everett.graphics.GLHelper
import net.spartanb312.everett.graphics.drawing.RenderUtils
import net.spartanb312.everett.graphics.matrix.MatrixLayerStack
import net.spartanb312.everett.utils.color.ColorRGB

object CrosshairDot : Crosshair(0f) {

    private val size by setting("Dot-Size", 10f, 1f..20f, 0.25f).alias("Size")
        .m("准星大小", "準星大小")

    override var clickTime = System.currentTimeMillis()

    override fun MatrixLayerStack.onRender(
        centerX: Float,
        centerY: Float,
        fov: Float,
        colorRGB: ColorRGB
    ) {
        GLHelper.pointSmooth = true
        RenderUtils.drawPoint(centerX, centerY, size, colorRGB)
    }

}