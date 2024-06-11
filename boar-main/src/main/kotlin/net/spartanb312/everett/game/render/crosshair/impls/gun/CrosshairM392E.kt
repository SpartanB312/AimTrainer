package net.spartanb312.everett.game.render.crosshair.impls.gun

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.spartanb312.everett.game.Player
import net.spartanb312.everett.game.audio.GunfireAudio
import net.spartanb312.everett.game.option.impls.CrosshairOption
import net.spartanb312.everett.game.render.crosshair.Crosshair
import net.spartanb312.everett.game.render.crosshair.CrosshairRenderer
import net.spartanb312.everett.game.render.scene.SceneManager
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.RenderSystem
import net.spartanb312.everett.graphics.drawing.RenderUtils
import net.spartanb312.everett.graphics.matrix.MatrixLayerStack
import net.spartanb312.everett.graphics.matrix.rotatef
import net.spartanb312.everett.graphics.matrix.scalef
import net.spartanb312.everett.graphics.matrix.translatef
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.config.setting.alias
import net.spartanb312.everett.utils.config.setting.lang
import net.spartanb312.everett.utils.config.setting.number.format
import net.spartanb312.everett.utils.config.setting.whenFalse
import net.spartanb312.everett.utils.config.setting.whenTrue
import net.spartanb312.everett.utils.math.ConvergeUtil.converge
import net.spartanb312.everett.utils.math.toRadian
import net.spartanb312.everett.utils.math.vector.Vec3f
import net.spartanb312.everett.utils.thread.MainScope
import net.spartanb312.everett.utils.timing.Timer
import kotlin.math.max
import kotlin.math.tan

object CrosshairM392E : GunCrosshair, Crosshair(1000f / 3f, 0.5f, 1.25f) {

    private val useSpecifiedAngle = setting("M392E-Specified Adsorption Angle", true)
        .alias("Specified Adsorption Angle").lang("指定吸附角", "指定吸附角")
    private val errorAngle2 by setting("M392E-Adsorption Angle", errorAngle, 0f..10f, 0.05f).format("0.00")
        .alias("Adsorption Angle").lang("吸附角", "吸附角")
        .whenTrue(useSpecifiedAngle)

    private val followFOV = setting("M392E-Follow FOV", true)
        .alias("Follow FOV").lang("准星大小跟随FOV", "準星隨FOV變化")
    private val size by setting("M392E-Specified FOV", 78f, 60f..150f)
        .alias("Specified FOV").lang("指定FOV的准星大小", "指定FOV下的準星")
        .whenFalse(followFOV)
    private val animation by setting("M392E-Animation", false)
        .alias("Animation").lang("旋转动画", "準星旋轉動畫")
    private val recoil by setting("M392E-Recoil", true)
        .alias("Recoil").lang("后坐力", "後坐力")

    override val syncFOV get() = followFOV.value
    override var clickTime = 0L

    private val colorTimer = Timer()
    private var colorRate = 0F

    override val overrideErrorAngle: Float
        get() = if (useSpecifiedAngle.value) errorAngle2 else -1f

    private var clickLocked = false

    private fun calcRecoilAngle() {
        if (!recoil || CrosshairOption.noCoolDown) Player.renderPitchOffset = 0f
        val currentTime = System.currentTimeMillis()
        if (currentTime - clickTime <= resetTime) {
            when (val rate = (currentTime - clickTime) / resetTime.toDouble()) {
                // stage 1
                in 0.0..0.45 -> {
                    val x = rate / 0.45
                    val y = x * x
                    Player.renderPitchOffset = (y * 0.4f).toFloat()
                }
                // stage 2
                in 0.45..0.9 -> {
                    val x = (rate - 0.45) / 0.45
                    val y = 1 - x
                    Player.renderPitchOffset = (y * 0.4f).toFloat()
                }
                // stage 3
                else -> Player.renderPitchOffset = 0f
            }
        }
    }

    override fun onClick(force: Boolean): Boolean {
        if (clickLocked && !force) return false
        clickLocked = false
        val current = System.currentTimeMillis()
        val resetTime = if (CrosshairOption.noCoolDown) 0 else this.resetTime
        val offset = current - clickTime
        if (offset >= resetTime || force) {
            clickTime = current
            SceneManager.currentTraining?.onClick()
            GunfireAudio.playBandit()
            return true
        } else if (offset / resetTime.toFloat() > 1f - errorRate) {
            clickLocked = true
            val triggerTime = clickTime + resetTime
            MainScope.launch(Dispatchers.Default) {
                while (true) {
                    val currentTime = System.currentTimeMillis()
                    val frameTime = 1000f / RenderSystem.averageFPS
                    val nextFrameTime = RenderSystem.lastJobTime + frameTime
                    val needTimeToTrigger = triggerTime - currentTime
                    if (needTimeToTrigger <= 0) {
                        // Late click
                        RenderSystem.addRenderThreadJob { onClick(true) }
                        break
                    } else if (needTimeToTrigger <= frameTime / 2 && nextFrameTime >= triggerTime) {
                        // Predict Late click to reduce input latency in low fps situation
                        RenderSystem.addRenderThreadJob { onClick(true) }
                        break
                    }
                }
            }
            return true
        }
        return false
    }

    override fun MatrixLayerStack.MatrixScope.onRender(
        centerX: Float,
        centerY: Float,
        fov: Float,
        shadow: Boolean,
        colorRGB: ColorRGB
    ) {
        calcRecoilAngle()
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
        var actualFOV = if (followFOV.value) fov else size.inDFov
        if (actualFOV < 100f) actualFOV = 100f
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
        val angle = progress * -90f + 45f
        translatef(centerX, centerY, 0f)
        rotatef(angle + 45f, Vec3f(0f, 0f, 1f))
        scalef(scale2, scale2, scale2)

        // Inner circle
        val innerRadius = 6.5f * scale
        val gap = 8.5f
        RenderUtils.drawArcOutline(0f, 0f, innerRadius, gap..90f - gap, 0, 2.5f * scale, color)
        RenderUtils.drawArcOutline(0f, 0f, innerRadius, 90f + gap..180f - gap, 0, 2.5f * scale, color)
        RenderUtils.drawArcOutline(0f, 0f, innerRadius, 180f + gap..270f - gap, 0, 2.5f * scale, color)
        RenderUtils.drawArcOutline(0f, 0f, innerRadius, 270f + gap..360f - gap, 0, 2.5f * scale, color)
    }

}