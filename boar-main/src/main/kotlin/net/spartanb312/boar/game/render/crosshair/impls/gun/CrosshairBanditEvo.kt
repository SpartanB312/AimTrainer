package net.spartanb312.boar.game.render.crosshair.impls.gun

import net.spartanb312.boar.game.render.crosshair.Crosshair
import net.spartanb312.boar.game.render.crosshair.CrosshairRenderer
import net.spartanb312.boar.graphics.GLHelper
import net.spartanb312.boar.graphics.RenderSystem
import net.spartanb312.boar.graphics.drawing.RenderUtils
import net.spartanb312.boar.graphics.matrix.mulRotate
import net.spartanb312.boar.graphics.matrix.mulScale
import net.spartanb312.boar.graphics.matrix.mulToGL
import net.spartanb312.boar.graphics.matrix.translatef
import net.spartanb312.boar.utils.color.ColorRGB
import net.spartanb312.boar.utils.math.vector.Vec3f

object CrosshairBanditEvo : Crosshair(1000f / 2.9f) {

    override var clickTime = 0L

    override fun onRender(fov: Float) {
        val progress = ((System.currentTimeMillis() - clickTime) / resetTime.toFloat()).coerceAtMost(1f)
        GLHelper.glMatrixScope {
            // Outer circle 120 to 18.5 65 to 24.0
            val outerRadius = 18.5f + 6.5f * (120f - fov) / 55f
            RenderUtils.drawArcOutline(
                RenderSystem.centerXF,
                RenderSystem.centerYF,
                outerRadius,
                0f..360f,
                0,
                2.5f,
                CrosshairRenderer.transparentColor
            )

            // Inner circle animation
            val scale = if (progress < 0.5f) 1f + (progress / 0.5f) * 0.25f
            else 1f + ((1f - progress) / 0.5f) * 0.25f
            val angle = progress * 90f
            translatef(RenderSystem.centerXF, RenderSystem.centerYF, 0f)
                .mulRotate(angle, Vec3f(0f, 0f, 1f))
                .mulScale(scale, scale, scale)
                .mulToGL()

            // Inner circle
            val innerRadius = 6.5f
            val gap = 8.5f
            RenderUtils.drawArcOutline(0f, 0f, innerRadius, gap..90f - gap, 0, 2.5f, ColorRGB.WHITE)
            RenderUtils.drawArcOutline(0f, 0f, innerRadius, 90f + gap..180f - gap, 0, 2.5f, ColorRGB.WHITE)
            RenderUtils.drawArcOutline(0f, 0f, innerRadius, 180f + gap..270f - gap, 0, 2.5f, ColorRGB.WHITE)
            RenderUtils.drawArcOutline(0f, 0f, innerRadius, 270f + gap..360f - gap, 0, 2.5f, ColorRGB.WHITE)
        }
    }

}