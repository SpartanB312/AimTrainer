package net.spartanb312.everett.game.crosshair.impls

import net.spartanb312.everett.game.crosshair.Crosshair
import net.spartanb312.everett.game.render.CrosshairRenderer
import net.spartanb312.everett.graphics.drawing.RenderUtils
import net.spartanb312.everett.graphics.matrix.MatrixLayerStack
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.config.setting.alias
import net.spartanb312.everett.utils.config.setting.lang
import net.spartanb312.everett.utils.math.ConvergeUtil.converge
import net.spartanb312.everett.utils.timing.Timer

object CrosshairDot : Crosshair(0f) {

    private val size by setting("Dot-Size", 10f, 1f..20f, 0.25f)
        .alias("Size").lang("准星大小", "準星大小")

    override var clickTime = System.currentTimeMillis()

    private val colorTimer = Timer()
    private var colorRate = 0F

    override fun MatrixLayerStack.MatrixScope.onRender(
        centerX: Float,
        centerY: Float,
        fov: Float,
        shadow: Boolean,
        colorRGB: ColorRGB
    ) {
        colorTimer.passedAndReset(10) {
            colorRate = colorRate.converge(if (CrosshairRenderer.raytraced) 100f else 0f, 0.25f)
        }
        val color = if (shadow) CrosshairRenderer.shadowColor
        else colorRGB.mix(ColorRGB(255, 20, 20), colorRate / 100f)
        val size = if (shadow) size * 1.03f else size
        RenderUtils.drawPoint(centerX, centerY, size, color)
    }

}