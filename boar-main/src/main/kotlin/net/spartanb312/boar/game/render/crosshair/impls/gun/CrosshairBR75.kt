package net.spartanb312.boar.game.render.crosshair.impls.gun

import net.spartanb312.boar.game.render.crosshair.Crosshair
import net.spartanb312.boar.game.render.crosshair.CrosshairRenderer.transparentColor
import net.spartanb312.boar.graphics.RenderSystem
import net.spartanb312.boar.graphics.drawing.RenderUtils
import net.spartanb312.boar.utils.color.ColorRGB

object CrosshairBR75 : Crosshair(1.55f / 4f) {

    override var clickTime = System.currentTimeMillis()

    override fun onRender(fov: Float) {
        // Outer circle 120 to 14.5 65 to 22.0
        val outerRadius = 14.5f + 7.5f * (120f - fov) / 55f
        val gap = 11f
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

        // Cross
        val fromRadius = 7.5f
        val length = 20f
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