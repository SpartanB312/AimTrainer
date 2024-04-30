package net.spartanb312.everett.game.render.crosshair.impls.gun

import net.spartanb312.everett.game.config.setting.alias
import net.spartanb312.everett.game.config.setting.m
import net.spartanb312.everett.game.config.setting.whenFalse
import net.spartanb312.everett.game.render.crosshair.Crosshair
import net.spartanb312.everett.game.render.crosshair.CrosshairRenderer
import net.spartanb312.everett.game.render.crosshair.CrosshairRenderer.transparentAlphaRate
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.drawing.RenderUtils
import net.spartanb312.everett.graphics.matrix.MatrixLayerStack
import net.spartanb312.everett.graphics.matrix.scalef
import net.spartanb312.everett.graphics.matrix.translatef
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.math.ConvergeUtil.converge
import net.spartanb312.everett.utils.timing.Timer
import kotlin.math.min

object CrosshairBR75 : GunCrosshair, Crosshair(1.55f / 4f) {

    private val followFOV = setting("BR75-Follow FOV", true)
        .alias("Follow FOV").m("准星大小跟随FOV", "準星隨FOV變化")
    private val size by setting("BR78-Specified FOV", 78f, 60f..120f)
        .alias("Specified FOV").m("指定FOV的准星大小", "指定FOV下的準星")
        .whenFalse(followFOV)

    override val syncFOV get() = followFOV.value
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
        var scale = min(RS.widthF / 2560f, RS.heightF / 1369f)
        colorTimer.passedAndReset(10) {
            colorRate = colorRate.converge(if (CrosshairRenderer.raytraced) 100f else 0f, 0.25f)
        }
        val color = if (shadow) CrosshairRenderer.shadowColor
        else colorRGB.mix(ColorRGB(255, 50, 60), colorRate / 100f)

        if (shadow) scale *= 1.03f
        translatef(centerX, centerY, 0.0f)
        scalef(scale, scale, 1.0f)
        translatef(-centerX, -centerY, 0.0f)

        val transparentColor = color.alpha((color.a * transparentAlphaRate).toInt())
        val actualFOV = if (followFOV.value) fov else size.inDFov
        // Outer circle 120 to 14.5 65 to 22.0
        val outerRadius = 14.5f + 7.5f * (120f - actualFOV) / 55f
        val gap = 11f
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

        // Cross
        val fromRadius = 7.5f
        val length = 20f
        RenderUtils.drawLine(
            centerX,
            centerY - fromRadius,
            centerX,
            centerY - fromRadius - length,
            3f * scale,
            color
        )
        RenderUtils.drawLine(
            centerX,
            centerY + fromRadius,
            centerX,
            centerY + fromRadius + length,
            3f * scale,
            color
        )
        RenderUtils.drawLine(
            centerX - fromRadius,
            centerY,
            centerX - fromRadius - length,
            centerY,
            3f * scale,
            color
        )
        RenderUtils.drawLine(
            centerX + fromRadius,
            centerY,
            centerX + fromRadius + length,
            centerY,
            3f * scale,
            color
        )
    }

}