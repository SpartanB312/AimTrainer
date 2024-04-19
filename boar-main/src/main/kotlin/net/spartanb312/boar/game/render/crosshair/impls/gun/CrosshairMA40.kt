package net.spartanb312.boar.game.render.crosshair.impls.gun

import net.spartanb312.boar.game.Player
import net.spartanb312.boar.game.config.setting.alias
import net.spartanb312.boar.game.config.setting.m
import net.spartanb312.boar.game.config.setting.whenFalse
import net.spartanb312.boar.game.render.crosshair.Crosshair
import net.spartanb312.boar.game.render.crosshair.CrosshairRenderer
import net.spartanb312.boar.game.render.scene.SceneManager
import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.graphics.drawing.RenderUtils
import net.spartanb312.boar.graphics.matrix.scope
import net.spartanb312.boar.utils.color.ColorRGB
import net.spartanb312.boar.utils.math.ConvergeUtil.converge
import net.spartanb312.boar.utils.timing.Timer
import kotlin.math.min

object CrosshairMA40 : GunCrosshair, Crosshair(1.3f / 16f) {

    private val followFOV = setting("MA40-Follow FOV", true).alias("Follow FOV")
        .m("准星大小跟随FOV", "準星隨FOV變化")
    private val size by setting("MA40-Specified FOV", 78f, 60f..120f).alias("Specified FOV").whenFalse(followFOV)
        .m("指定FOV的准星大小", "指定FOV下的準星")

    override val syncFOV get() = followFOV.value
    override var clickTime = System.currentTimeMillis()

    private val colorTimer = Timer()
    private var colorRate = 0F

    override fun onRender(fov: Float, test: Boolean, colorRGB: ColorRGB) {
        val centerX = if (test) 0f else RS.centerXF
        val centerY = if (test) 0f else RS.centerYF
        val scale = min(RS.widthF / 2560f, RS.heightF / 1369f)
        // Outer circle 120 to 22.0 65 to 50.0
        colorTimer.passedAndReset(10) {
            colorRate = colorRate.converge(if (Player.raytraced && SceneManager.inTraining) 100f else 0f, 0.25f)
        }
        val color = colorRGB.mix(ColorRGB(255, 50, 60), colorRate / 100f)
        RS.matrixLayer.scope {
            val transparentColor = color.alpha((color.a * CrosshairRenderer.transparentAlphaRate).toInt())
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

}