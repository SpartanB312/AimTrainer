package net.spartanb312.everett.game.crosshair.impls.gun

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.spartanb312.everett.game.Language.lang
import net.spartanb312.everett.game.Player
import net.spartanb312.everett.game.audio.GunfireAudio
import net.spartanb312.everett.game.crosshair.Crosshair
import net.spartanb312.everett.game.option.impls.CrosshairOption
import net.spartanb312.everett.game.render.CrosshairRenderer
import net.spartanb312.everett.game.render.HitEffectsRenderer
import net.spartanb312.everett.game.render.scene.SceneManager
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.RenderSystem
import net.spartanb312.everett.graphics.drawing.RenderUtils
import net.spartanb312.everett.graphics.matrix.MatrixLayerStack
import net.spartanb312.everett.graphics.matrix.rotatef
import net.spartanb312.everett.graphics.matrix.scalef
import net.spartanb312.everett.graphics.matrix.translatef
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.config.setting.*
import net.spartanb312.everett.utils.config.setting.number.format
import net.spartanb312.everett.utils.language.MultiText
import net.spartanb312.everett.utils.math.ConvergeUtil.converge
import net.spartanb312.everett.utils.math.ceilToInt
import net.spartanb312.everett.utils.math.toRadian
import net.spartanb312.everett.utils.math.vector.Vec3f
import net.spartanb312.everett.utils.misc.DisplayEnum
import net.spartanb312.everett.utils.thread.MainScope
import net.spartanb312.everett.utils.timing.Timer
import kotlin.math.max
import kotlin.math.tan

object CrosshairM392E : GunCrosshair, Crosshair(1000f / 2.715f, 0.5f, 1.0f) {

    private val resetMode by setting("M392E-Firing Rate", M392ResetTime.NewBandit)
        .alias("Firing Rate").lang("开火速率", "開火速率")
    private val outerCircle = setting("M392E-Outer Circle", OuterCircle.SpecifiedAngle)
        .alias("Outer Circle").lang("准星外圈", "準星外圈")
    private val specifiedAngle by setting("M392E-Specified Render Angle", 1.25f, 0f..10f, 0.05f).format("0.00")
        .alias("Render Angle").lang("渲染角", "渲染角")
        .atMode(outerCircle, OuterCircle.SpecifiedAngle)

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

    private val recoil = setting("M392E-Recoil", true)
        .alias("Recoil").lang("后坐力", "後坐力")
    private val recoilAngle by setting("M392E-Recoil Angle", 0.5f, 0.0f..5.0f, 0.05f).format("0.00")
        .alias("Recoil Angle").lang("后坐力角度", "後坐力角度")
        .whenTrue(recoil)

    override val syncFOV get() = followFOV.value
    override var clickTime = 0L

    private val colorTimer = Timer()
    private var colorRate = 0F

    override val resetTime get() = resetMode.time

    override val overrideErrorAngle: Float
        get() = if (useSpecifiedAngle.value) errorAngle2 else -1f

    private var clickLocked = false

    private fun calcRecoilAngle() {
        if (!recoil.value || CrosshairOption.noCoolDown) {
            Player.renderPitchOffset = 0f
            return
        }
        val currentTime = System.currentTimeMillis()
        if (currentTime - clickTime <= resetTime) {
            when (val rate = (currentTime - clickTime) / resetTime.toDouble()) {
                // stage 1
                in 0.0..0.45 -> {
                    val x = rate / 0.45
                    val y = x * x
                    Player.renderPitchOffset = (y * recoilAngle).toFloat()
                }
                // stage 2
                in 0.45..0.9 -> {
                    val x = (rate - 0.45) / 0.45
                    val y = 1 - x
                    Player.renderPitchOffset = (y * recoilAngle).toFloat()
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
        val errorAngle = if (useSpecifiedAngle.value) errorAngle2 else CrosshairRenderer.errorAngle
        val actualRenderAngle = if (outerCircle.value == OuterCircle.SpecifiedAngle) specifiedAngle else errorAngle
        val outerRadius = d * tan(actualRenderAngle.toRadian())
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

        rotatef(45f, Vec3f(0f, 0f, 1f))
        HitEffectsRenderer.acceptCount(outerRadius, scale, 500)
        if (!shadow) HitEffectsRenderer.onRender(layer, color)

        rotatef(angle, Vec3f(0f, 0f, 1f))
        scalef(scale2, scale2, scale2)

        // Inner circle
        val innerRadius = 6.5f * scale
        val gap = 8.5f
        RenderUtils.drawArcOutline(0f, 0f, innerRadius, gap..90f - gap, 0, 2.5f * scale, color)
        RenderUtils.drawArcOutline(0f, 0f, innerRadius, 90f + gap..180f - gap, 0, 2.5f * scale, color)
        RenderUtils.drawArcOutline(0f, 0f, innerRadius, 180f + gap..270f - gap, 0, 2.5f * scale, color)
        RenderUtils.drawArcOutline(0f, 0f, innerRadius, 270f + gap..360f - gap, 0, 2.5f * scale, color)
    }

    enum class OuterCircle(multiText: MultiText) : DisplayEnum {
        SpecifiedAngle("Specified".lang("指定角度", "指定角度")),
        AdsorptionAngle("Adsorption".lang("吸附角度", "吸附角度"));

        override val displayName by multiText
    }

    enum class M392ResetTime(override val displayName: CharSequence, val time: Int) : DisplayEnum {
        OldBandit("Old M932(3.0)", (1000f / 3.0f).ceilToInt()),
        OldM392Evo("Old EVO(2.9)", (1000f / 2.9f).ceilToInt()),
        NewBandit("New EVO(2.715)", (1000f / 2.715f).ceilToInt())
    }

}