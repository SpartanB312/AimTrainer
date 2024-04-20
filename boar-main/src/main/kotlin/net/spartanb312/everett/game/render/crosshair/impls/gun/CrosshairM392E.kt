package net.spartanb312.everett.game.render.crosshair.impls.gun

import net.spartanb312.everett.game.Player
import net.spartanb312.everett.game.config.setting.alias
import net.spartanb312.everett.game.config.setting.m
import net.spartanb312.everett.game.config.setting.whenFalse
import net.spartanb312.everett.game.render.crosshair.Crosshair
import net.spartanb312.everett.game.render.crosshair.CrosshairRenderer
import net.spartanb312.everett.game.render.scene.SceneManager
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.drawing.RenderUtils
import net.spartanb312.everett.graphics.matrix.MatrixLayerStack
import net.spartanb312.everett.graphics.matrix.rotatef
import net.spartanb312.everett.graphics.matrix.scalef
import net.spartanb312.everett.graphics.matrix.translatef
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.math.ConvergeUtil.converge
import net.spartanb312.everett.utils.math.vector.Vec3f
import net.spartanb312.everett.utils.timing.Timer
import kotlin.math.min

object CrosshairM392E : GunCrosshair, Crosshair(1000f / 3f, 0.1f) {

    private val followFOV = setting("M392E-Follow FOV", true).alias("Follow FOV")
        .m("准星大小跟随FOV", "準星隨FOV變化")
    private val size by setting("M392E-Specified FOV", 78f, 60f..120f).alias("Specified FOV").whenFalse(followFOV)
        .m("指定FOV的准星大小", "指定FOV下的準星")
    private val animation by setting("M392E-Animation", false).alias("Animation")
        .m("旋转动画", "準星旋轉動畫")

    override val syncFOV get() = followFOV.value
    override var clickTime = 0L

    private val colorTimer = Timer()
    private var colorRate = 0F

    override fun MatrixLayerStack.onRender(
        centerX: Float,
        centerY: Float,
        fov: Float,
        colorRGB: ColorRGB
    ) {
        val scale = min(RS.widthF / 2560f, RS.heightF / 1369f)
        val progress =
            if (animation) ((System.currentTimeMillis() - clickTime) / resetTime.toFloat()).coerceIn(0f..1f) else 0f
        colorTimer.passedAndReset(10) {
            colorRate = colorRate.converge(if (Player.raytraced && SceneManager.inTraining) 100f else 0f, 0.25f)
        }
        val color = colorRGB.mix(ColorRGB(255, 50, 60), colorRate / 100f)

        translatef(centerX, centerY, 0.0f)
        scalef(scale, scale, 1.0f)
        translatef(-centerX, -centerY, 0.0f)

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
        rotatef(angle, Vec3f(0f, 0f, 1f))
        scalef(scale2, scale2, scale2)

        // Inner circle
        val innerRadius = 7f
        val gap = 8.5f
        RenderUtils.drawArcOutline(0f, 0f, innerRadius, gap..90f - gap, 0, 2.5f * scale, color)
        RenderUtils.drawArcOutline(0f, 0f, innerRadius, 90f + gap..180f - gap, 0, 2.5f * scale, color)
        RenderUtils.drawArcOutline(0f, 0f, innerRadius, 180f + gap..270f - gap, 0, 2.5f * scale, color)
        RenderUtils.drawArcOutline(0f, 0f, innerRadius, 270f + gap..360f - gap, 0, 2.5f * scale, color)
    }

}