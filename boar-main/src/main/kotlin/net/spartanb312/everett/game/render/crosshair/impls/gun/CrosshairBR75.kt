package net.spartanb312.everett.game.render.crosshair.impls.gun

import net.spartanb312.everett.game.render.crosshair.Crosshair
import net.spartanb312.everett.game.render.crosshair.CrosshairRenderer
import net.spartanb312.everett.game.render.crosshair.CrosshairRenderer.transparentAlphaRate
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

object CrosshairBR75 : GunCrosshair, Crosshair(1550f / 4f, errorAngle = 1f) {

    private val useSpecifiedAngle = setting("BR75-Specified Adsorption Angle", true)
        .alias("Specified Adsorption Angle").lang("指定吸附角", "指定吸附角")
    private val errorAngle2 by setting("BR75-Adsorption Angle", errorAngle, 0f..10f, 0.05f).format("0.00")
        .alias("Adsorption Angle").lang("吸附角", "吸附角")
        .whenTrue(useSpecifiedAngle)

    private val followFOV = setting("BR75-Follow FOV", true)
        .alias("Follow FOV").lang("准星大小跟随FOV", "準星隨FOV變化")
    private val size by setting("BR78-Specified FOV", 78f, 60f..150f)
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
        colorTimer.passedAndReset(10) {
            colorRate = colorRate.converge(if (CrosshairRenderer.raytraced) 100f else 0f, 0.25f)
        }
        val color = if (shadow) CrosshairRenderer.shadowColor
        else colorRGB.mix(ColorRGB(255, 20, 20), colorRate / 100f)

        if (shadow) scale *= 1.03f

        val transparentColor = color.alpha((color.a * transparentAlphaRate).toInt())
        var actualFOV = if (followFOV.value) fov else size.inDFov
        if (actualFOV < 100f) actualFOV = 100f

        // Outer circle
        val d = RS.diagonalF / 2f / tan((actualFOV / 2f).toRadian())
        val errorAngle: Float = if (useSpecifiedAngle.value) errorAngle2 else CrosshairRenderer.errorAngle
        val outerRadius = d * tan(errorAngle.toRadian())
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
        val fromRadius = 7.35f * scale
        val length = 20f * scale//20f * scale + (108 - actualFOV) * scale * 0.1f
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