package net.spartanb312.everett.game.render.crosshair.impls.gun

import net.spartanb312.everett.game.render.crosshair.Crosshair
import net.spartanb312.everett.game.render.crosshair.CrosshairRenderer
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.drawing.RenderUtils
import net.spartanb312.everett.graphics.matrix.MatrixLayerStack
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.config.setting.alias
import net.spartanb312.everett.utils.config.setting.lang
import net.spartanb312.everett.utils.config.setting.number.format
import net.spartanb312.everett.utils.config.setting.whenFalse
import net.spartanb312.everett.utils.config.setting.whenTrue
import net.spartanb312.everett.utils.math.ConvergeUtil.converge
import net.spartanb312.everett.utils.math.toRadian
import net.spartanb312.everett.utils.timing.Timer
import kotlin.math.max
import kotlin.math.tan

object CrosshairMA40 : GunCrosshair, Crosshair(1300f / 16f, errorAngle = 1.5f) {

    private val useSpecifiedAngle = setting("MA40-Specified Adsorption Angle", true)
        .alias("Specified Adsorption Angle").lang("指定吸附角", "指定吸附角")
    private val errorAngle2 by setting("MA40-Adsorption Angle", errorAngle, 0f..10f, 0.05f).format("0.00")
        .alias("Adsorption Angle").lang("吸附角", "吸附角")
        .whenTrue(useSpecifiedAngle)


    private val followFOV = setting("MA40-Follow FOV", true)
        .alias("Follow FOV").lang("准星大小跟随FOV", "準星隨FOV變化")
    private val size by setting("MA40-Specified FOV", 78f, 60f..150f)
        .alias("Specified FOV").lang("指定FOV的准星大小", "指定FOV下的準星")
        .whenFalse(followFOV)

    override val syncFOV get() = followFOV.value
    override var clickTime = System.currentTimeMillis()

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
        // Outer circle 120 to 22.0 65 to 50.0
        colorTimer.passedAndReset(10) {
            colorRate = colorRate.converge(if (CrosshairRenderer.raytraced) 100f else 0f, 0.25f)
        }
        val color = if (shadow) CrosshairRenderer.shadowColor
        else colorRGB.mix(ColorRGB(255, 20, 20), colorRate / 100f)

        if (shadow) scale *= 1.03f
        val transparentColor = color.alpha((color.a * CrosshairRenderer.transparentAlphaRate).toInt())

        val actualFOV = if (followFOV.value) fov else size.inDFov
        val d = RS.diagonalF / 2f / tan((actualFOV / 2f).toRadian())
        val errorAngle: Float = if (useSpecifiedAngle.value) errorAngle2 else CrosshairRenderer.errorAngle
        val outerRadius = d * tan(errorAngle.toRadian())
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
        val fromRadius = (5f + 7f * (120f - actualFOV) / 55f) * scale
        val length = 10.5f * scale //11f
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