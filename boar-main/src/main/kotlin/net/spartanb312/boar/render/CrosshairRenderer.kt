package net.spartanb312.boar.render

import net.spartanb312.boar.graphics.RenderSystem
import net.spartanb312.boar.graphics.drawing.RenderUtils
import net.spartanb312.boar.utils.color.ColorRGB

object CrosshairRenderer {

    fun onRender(fov: Float) {
        crosshairBanditEvo(fov)
    }

    private val transparentColor = ColorRGB.WHITE.alpha(160)

    private fun crosshairBanditEvo(fov: Float) {
        val centerX = RenderSystem.displayWidthF / 2f
        val centerY = RenderSystem.displayHeightF / 2f

        // Outer circle 120 to 18.5 65 to 24.0
        val outerRadius = 18.5f + 6.5f * (120f - fov) / 55f
        RenderUtils.drawArcOutline(centerX, centerY, outerRadius, 0f..360f, 0, 2.5f, transparentColor)

        // Inner circle
        val innerRadius = 6.5f
        val gap = 8.5f
        RenderUtils.drawArcOutline(centerX, centerY, innerRadius, gap..90f - gap, 0, 2.5f, ColorRGB.WHITE)
        RenderUtils.drawArcOutline(centerX, centerY, innerRadius, 90f + gap..180f - gap, 0, 2.5f, ColorRGB.WHITE)
        RenderUtils.drawArcOutline(centerX, centerY, innerRadius, 180f + gap..270 - gap, 0, 2.5f, ColorRGB.WHITE)
        RenderUtils.drawArcOutline(centerX, centerY, innerRadius, 270 + gap..360 - gap, 0, 2.5f, ColorRGB.WHITE)
    }

    private fun crosshairBR75(fov: Float) {
        val centerX = RenderSystem.displayWidthF / 2f
        val centerY = RenderSystem.displayHeightF / 2f

        // Outer circle 120 to 14.5 65 to 22.0
        val outerRadius = 14.5f + 7.5f * (120f - fov) / 55f
        val gap = 11f
        RenderUtils.drawArcOutline(centerX, centerY, outerRadius, gap..90f - gap, 0, 2.5f, transparentColor)
        RenderUtils.drawArcOutline(centerX, centerY, outerRadius, 90f + gap..180f - gap, 0, 2.5f, transparentColor)
        RenderUtils.drawArcOutline(centerX, centerY, outerRadius, 180f + gap..270 - gap, 0, 2.5f, transparentColor)
        RenderUtils.drawArcOutline(centerX, centerY, outerRadius, 270 + gap..360 - gap, 0, 2.5f, transparentColor)

        // Cross
        val fromRadius = 7.5f
        val length = 20f
        RenderUtils.drawLine(
            centerX,
            centerY - fromRadius,
            centerX,
            centerY - fromRadius - length,
            3f,
            ColorRGB.WHITE
        )
        RenderUtils.drawLine(
            centerX,
            centerY + fromRadius,
            centerX,
            centerY + fromRadius + length,
            3f,
            ColorRGB.WHITE
        )
        RenderUtils.drawLine(
            centerX - fromRadius,
            centerY,
            centerX - fromRadius - length,
            centerY,
            3f,
            ColorRGB.WHITE
        )
        RenderUtils.drawLine(
            centerX + fromRadius,
            centerY,
            centerX + fromRadius + length,
            centerY,
            3f,
            ColorRGB.WHITE
        )
    }

    private fun crosshairMA40(fov: Float) {
        val centerX = RenderSystem.displayWidthF / 2f
        val centerY = RenderSystem.displayHeightF / 2f

        // Outer circle 120 to 22.0 65 to 50.0
        val outerRadius = 22f + 28f * (120f - fov) / 55f
        val gap = 13f
        RenderUtils.drawArcOutline(centerX, centerY, outerRadius, gap..90f - gap, 0, 2.5f, transparentColor)
        RenderUtils.drawArcOutline(centerX, centerY, outerRadius, 90f + gap..180f - gap, 0, 2.5f, transparentColor)
        RenderUtils.drawArcOutline(centerX, centerY, outerRadius, 180f + gap..270 - gap, 0, 2.5f, transparentColor)
        RenderUtils.drawArcOutline(centerX, centerY, outerRadius, 270 + gap..360 - gap, 0, 2.5f, transparentColor)

        // Cross 120 to 5.0 65 to 12.0
        val fromRadius = 5f + 7f * (120f - fov) / 55f
        val length = 11f
        RenderUtils.drawLine(
            centerX,
            centerY - fromRadius,
            centerX,
            centerY - fromRadius - length,
            3f,
            ColorRGB.WHITE
        )
        RenderUtils.drawLine(
            centerX,
            centerY + fromRadius,
            centerX,
            centerY + fromRadius + length,
            3f,
            ColorRGB.WHITE
        )
        RenderUtils.drawLine(
            centerX - fromRadius,
            centerY,
            centerX - fromRadius - length,
            centerY,
            3f,
            ColorRGB.WHITE
        )
        RenderUtils.drawLine(
            centerX + fromRadius,
            centerY,
            centerX + fromRadius + length,
            centerY,
            3f,
            ColorRGB.WHITE
        )
    }

    private fun crosshairMK50(fov: Float) {

    }

    private fun crosshairS7(fov: Float) {

    }

}