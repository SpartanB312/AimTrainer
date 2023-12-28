package net.spartanb312.boar.game.render.crosshair.impls.gun

import net.spartanb312.boar.game.render.crosshair.Crosshair
import net.spartanb312.boar.game.render.crosshair.CrosshairRenderer.transparentColor
import net.spartanb312.boar.graphics.RenderSystem
import net.spartanb312.boar.graphics.drawing.RenderUtils
import net.spartanb312.boar.utils.color.ColorRGB

object CrosshairMA40 : Crosshair(1.3f / 16f) {

    override var clickTime = System.currentTimeMillis()

    override fun onRender(fov: Float) {
        // Outer circle 120 to 22.0 65 to 50.0
        val outerRadius = 22f + 28f * (120f - fov) / 55f
        val gap = 13f
        RenderUtils.drawArcOutline(
            RenderSystem.centerXF,
            RenderSystem.centerYF,
            outerRadius,
            gap..90f - gap,
            0,
            2.5f,
            transparentColor
        )
        RenderUtils.drawArcOutline(
            RenderSystem.centerXF,
            RenderSystem.centerYF,
            outerRadius,
            90f + gap..180f - gap,
            0,
            2.5f,
            transparentColor
        )
        RenderUtils.drawArcOutline(
            RenderSystem.centerXF,
            RenderSystem.centerYF,
            outerRadius,
            180f + gap..270f - gap,
            0,
            2.5f,
            transparentColor
        )
        RenderUtils.drawArcOutline(
            RenderSystem.centerXF,
            RenderSystem.centerYF,
            outerRadius,
            270f + gap..360f - gap,
            0,
            2.5f,
            transparentColor
        )

        // Cross 120 to 5.0 65 to 12.0
        val fromRadius = 5f + 7f * (120f - fov) / 55f
        val length = 10.5f //11f
        RenderUtils.drawLine(
            RenderSystem.centerXF,
            RenderSystem.centerYF - fromRadius,
            RenderSystem.centerXF,
            RenderSystem.centerYF - fromRadius - length,
            3f,
            ColorRGB.WHITE
        )
        RenderUtils.drawLine(
            RenderSystem.centerXF,
            RenderSystem.centerYF + fromRadius,
            RenderSystem.centerXF,
            RenderSystem.centerYF + fromRadius + length,
            3f,
            ColorRGB.WHITE
        )
        RenderUtils.drawLine(
            RenderSystem.centerXF - fromRadius,
            RenderSystem.centerYF,
            RenderSystem.centerXF - fromRadius - length,
            RenderSystem.centerYF,
            3f,
            ColorRGB.WHITE
        )
        RenderUtils.drawLine(
            RenderSystem.centerXF + fromRadius,
            RenderSystem.centerYF,
            RenderSystem.centerXF + fromRadius + length,
            RenderSystem.centerYF,
            3f,
            ColorRGB.WHITE
        )
    }

}