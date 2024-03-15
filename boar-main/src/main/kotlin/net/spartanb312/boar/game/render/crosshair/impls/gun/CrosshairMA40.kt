package net.spartanb312.boar.game.render.crosshair.impls.gun

import net.spartanb312.boar.game.config.setting.alias
import net.spartanb312.boar.game.config.setting.whenFalse
import net.spartanb312.boar.game.render.crosshair.Crosshair
import net.spartanb312.boar.game.render.crosshair.CrosshairRenderer
import net.spartanb312.boar.graphics.GLHelper.glMatrixScope
import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.graphics.drawing.RenderUtils
import net.spartanb312.boar.utils.color.ColorRGB
import kotlin.math.min

object CrosshairMA40 : GunCrosshair, Crosshair(1.3f / 16f) {

    private val followFOV = setting("MA40-Follow FOV", true).alias("Follow FOV")
    private val size by setting("MA40-Specified FOV", 78f, 60f..120f).alias("Specified FOV").whenFalse(followFOV)

    override val syncFOV get() = followFOV.value
    override var clickTime = System.currentTimeMillis()

    override fun onRender(fov: Float, test: Boolean, colorRGB: ColorRGB) {
        val centerX = if (test) 0f else RS.centerXF
        val centerY = if (test) 0f else RS.centerYF
        val scale = min(RS.widthF / 2560f, RS.heightF / 1369f)
        // Outer circle 120 to 22.0 65 to 50.0
        glMatrixScope {
            val transparentColor = colorRGB.alpha((colorRGB.a * CrosshairRenderer.transparentAlphaRate).toInt())
            val actualFOV = if (followFOV.value) fov else size.inDFov
            val outerRadius = 22f + 28f * (120f - actualFOV) / 55f
            val gap = 13f
            RenderUtils.drawArcOutline(
                centerX,
                centerY,
                outerRadius,
                gap..90f - gap,
                0,
                2.5f * scale,
                transparentColor
            )
            RenderUtils.drawArcOutline(
                centerX,
                centerY,
                outerRadius,
                90f + gap..180f - gap,
                0,
                2.5f * scale,
                transparentColor
            )
            RenderUtils.drawArcOutline(
                centerX,
                centerY,
                outerRadius,
                180f + gap..270f - gap,
                0,
                2.5f * scale,
                transparentColor
            )
            RenderUtils.drawArcOutline(
                centerX,
                centerY,
                outerRadius,
                270f + gap..360f - gap,
                0,
                2.5f * scale,
                transparentColor
            )

            // Cross 120 to 5.0 65 to 12.0
            val fromRadius = 5f + 7f * (120f - fov) / 55f
            val length = 10.5f //11f
            RenderUtils.drawLine(
                centerX,
                centerY - fromRadius,
                centerX,
                centerY - fromRadius - length,
                3f * scale,
                colorRGB
            )
            RenderUtils.drawLine(
                centerX,
                centerY + fromRadius,
                centerX,
                centerY + fromRadius + length,
                3f * scale,
                colorRGB
            )
            RenderUtils.drawLine(
                centerX - fromRadius,
                centerY,
                centerX - fromRadius - length,
                centerY,
                3f * scale,
                colorRGB
            )
            RenderUtils.drawLine(
                centerX + fromRadius,
                centerY,
                centerX + fromRadius + length,
                centerY,
                3f * scale,
                colorRGB
            )
        }
    }

}