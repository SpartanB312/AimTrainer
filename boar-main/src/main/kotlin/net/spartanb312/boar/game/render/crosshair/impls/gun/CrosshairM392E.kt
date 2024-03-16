package net.spartanb312.boar.game.render.crosshair.impls.gun

import net.spartanb312.boar.game.Player
import net.spartanb312.boar.game.config.setting.alias
import net.spartanb312.boar.game.config.setting.whenFalse
import net.spartanb312.boar.game.render.crosshair.Crosshair
import net.spartanb312.boar.game.render.crosshair.CrosshairRenderer
import net.spartanb312.boar.graphics.GLHelper
import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.graphics.drawing.RenderUtils
import net.spartanb312.boar.graphics.matrix.*
import net.spartanb312.boar.utils.color.ColorRGB
import net.spartanb312.boar.utils.math.ConvergeUtil.converge
import net.spartanb312.boar.utils.math.vector.Vec3f
import net.spartanb312.boar.utils.timing.Timer
import kotlin.math.min

object CrosshairM392E : GunCrosshair, Crosshair(1000f / 3f, 0.1f) {

    private val followFOV = setting("M392E-Follow FOV", true).alias("Follow FOV")
    private val size by setting("M392E-Specified FOV", 78f, 60f..120f).alias("Specified FOV").whenFalse(followFOV)
    private val animation by setting("M392E-Animation", false).alias("Animation")

    override val syncFOV get() = followFOV.value
    override var clickTime = 0L

    private val colorTimer = Timer()
    private var colorRate = 0F

    override fun onRender(fov: Float, test: Boolean, colorRGB: ColorRGB) {
        val centerX = if (test) 0f else RS.centerXF
        val centerY = if (test) 0f else RS.centerYF
        val scale = min(RS.widthF / 2560f, RS.heightF / 1369f)
        val progress = if (animation) ((System.currentTimeMillis() - clickTime) / resetTime.toFloat()).coerceIn(0f..1f) else 0f
        colorTimer.passedAndReset(10) {
            colorRate = colorRate.converge(if (Player.raytraced) 100f else 0f, 0.25f)
        }
        val color = colorRGB.mix(ColorRGB(255, 50, 60), colorRate / 100f)
        GLHelper.glMatrixScope {
            translatef(centerX, centerY, 0.0f)
                .mulScale(scale, scale, 1.0f)
                .mulTranslate(-centerX, -centerY, 0.0f)
                .mulToGL()
            val actualFOV = if (followFOV.value) fov else size.inDFov
            // Outer circle 120 to 18.5 65 to 24.0
            val outerRadius = 18.5f + 6.5f * (120f - actualFOV) / 55f
            RenderUtils.drawArcOutline(
                centerX,
                centerY,
                outerRadius,
                0f..360f,
                0,
                2.5f * scale,
                color.alpha((color.a * CrosshairRenderer.transparentAlphaRate).toInt())
            )

            // Inner circle animation
            val scale2 = if (progress < 0.5f) 1f + (progress / 0.5f) * 0.25f
            else 1f + ((1f - progress) / 0.5f) * 0.25f
            val angle = progress * -90f
            translatef(centerX, centerY, 0f)
                .mulRotate(angle, Vec3f(0f, 0f, 1f))
                .mulScale(scale2, scale2, scale2)
                .mulToGL()

            // Inner circle
            val innerRadius = 7f
            val gap = 8.5f
            RenderUtils.drawArcOutline(0f, 0f, innerRadius, gap..90f - gap, 0, 2.5f * scale, color)
            RenderUtils.drawArcOutline(0f, 0f, innerRadius, 90f + gap..180f - gap, 0, 2.5f * scale, color)
            RenderUtils.drawArcOutline(0f, 0f, innerRadius, 180f + gap..270f - gap, 0, 2.5f * scale, color)
            RenderUtils.drawArcOutline(0f, 0f, innerRadius, 270f + gap..360f - gap, 0, 2.5f * scale, color)
        }
    }

}