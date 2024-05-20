package net.spartanb312.everett.game.render.crosshair.impls.gun

import net.spartanb312.everett.game.render.crosshair.Crosshair
import net.spartanb312.everett.game.render.crosshair.CrosshairRenderer
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.drawing.RenderUtils
import net.spartanb312.everett.graphics.matrix.MatrixLayerStack
import net.spartanb312.everett.graphics.matrix.rotatef
import net.spartanb312.everett.graphics.matrix.scalef
import net.spartanb312.everett.graphics.matrix.translatef
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.config.setting.alias
import net.spartanb312.everett.utils.config.setting.m
import net.spartanb312.everett.utils.config.setting.number.format
import net.spartanb312.everett.utils.config.setting.whenFalse
import net.spartanb312.everett.utils.config.setting.whenTrue
import net.spartanb312.everett.utils.math.ConvergeUtil.converge
import net.spartanb312.everett.utils.math.toRadian
import net.spartanb312.everett.utils.math.vector.Vec3f
import net.spartanb312.everett.utils.timing.Timer
import kotlin.math.max
import kotlin.math.tan

object CrosshairM392E : GunCrosshair, Crosshair(1000f / 3f, 0.1f, 1.3f) {

    private val useSpecifiedAngle = setting("M392E-Specified Adsorption Angle", true)
        .alias("Specified Adsorption Angle").m("指定吸附角", "指定吸附角")
    private val errorAngle2 by setting("M392E-Adsorption Angle", errorAngle, 0f..10f, 0.05f).format("0.00")
        .alias("Adsorption Angle").m("吸附角", "吸附角")
        .whenTrue(useSpecifiedAngle)

    private val followFOV = setting("M392E-Follow FOV", true)
        .alias("Follow FOV").m("准星大小跟随FOV", "準星隨FOV變化")
    private val size by setting("M392E-Specified FOV", 78f, 60f..150f)
        .alias("Specified FOV").m("指定FOV的准星大小", "指定FOV下的準星")
        .whenFalse(followFOV)
    private val animation by setting("M392E-Animation", false)
        .alias("Animation").m("旋转动画", "準星旋轉動畫")

    override val syncFOV get() = followFOV.value
    override var clickTime = 0L

    private val colorTimer = Timer()
    private var colorRate = 0F

    override val overrideErrorAngle: Float
        get() = if (useSpecifiedAngle.value) errorAngle2 else -1f

    override fun MatrixLayerStack.MatrixScope.onRender(
        centerX: Float,
        centerY: Float,
        fov: Float,
        shadow: Boolean,
        colorRGB: ColorRGB
    ) {
        var scale = max(RS.widthF / 2560f, RS.heightF / 1369f)
        val progress =
            if (animation) ((System.currentTimeMillis() - clickTime) / resetTime.toFloat()).coerceIn(0f..1f)
            else 0f
        colorTimer.passedAndReset(10) {
            colorRate = colorRate.converge(if (CrosshairRenderer.raytraced) 100f else 0f, 0.25f)
        }
        val color = if (shadow) CrosshairRenderer.shadowColor
        else colorRGB.mix(ColorRGB(255, 20, 20), colorRate / 100f)

        if (shadow) scale *= 1.03f

        // Outer circle
        val actualFOV = if (followFOV.value) fov else size.inDFov
        val d = RS.diagonalF / 2f / tan((actualFOV / 2f).toRadian())
        val errorAngle: Float = if (useSpecifiedAngle.value) errorAngle2 else CrosshairRenderer.errorAngle
        val outerRadius = d * tan(errorAngle.toRadian())
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
        rotatef(angle + 45f, Vec3f(0f, 0f, 1f))
        scalef(scale2, scale2, scale2)

        // Inner circle
        val innerRadius = 7f * scale
        val gap = 8.5f
        RenderUtils.drawArcOutline(0f, 0f, innerRadius, gap..90f - gap, 0, 2.5f * scale, color)
        RenderUtils.drawArcOutline(0f, 0f, innerRadius, 90f + gap..180f - gap, 0, 2.5f * scale, color)
        RenderUtils.drawArcOutline(0f, 0f, innerRadius, 180f + gap..270f - gap, 0, 2.5f * scale, color)
        RenderUtils.drawArcOutline(0f, 0f, innerRadius, 270f + gap..360f - gap, 0, 2.5f * scale, color)
    }

}