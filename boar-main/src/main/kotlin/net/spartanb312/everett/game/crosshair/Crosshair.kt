package net.spartanb312.everett.game.crosshair

import net.spartanb312.everett.game.Language
import net.spartanb312.everett.game.option.impls.CrosshairOption
import net.spartanb312.everett.game.option.impls.VideoOption
import net.spartanb312.everett.game.render.scene.SceneManager
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.matrix.MatrixLayerStack
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.config.setting.AbstractSetting
import net.spartanb312.everett.utils.config.setting.SettingRegister
import net.spartanb312.everett.utils.math.MathUtils.h2dFOV
import net.spartanb312.everett.utils.math.MathUtils.v2dFOV
import net.spartanb312.everett.utils.math.ceilToInt

abstract class Crosshair(
    resetTime: Float,
    val errorRate: Float = 0f,
    val errorAngle: Float = 0f
) : SettingRegister<Crosshair> {

    val settings = mutableListOf<AbstractSetting<*>>()
    val resetTime = resetTime.ceilToInt()
    protected abstract var clickTime: Long
    open val overrideErrorAngle = -1f

    abstract fun MatrixLayerStack.MatrixScope.onRender(
        centerX: Float,
        centerY: Float,
        fov: Float,
        shadow: Boolean,
        colorRGB: ColorRGB
    )

    open fun onClick(force: Boolean = false): Boolean {
        val current = System.currentTimeMillis()
        val offset = current - clickTime
        val resetTime = if (CrosshairOption.noCoolDown) 0 else this.resetTime
        return if (offset >= resetTime) {
            clickTime = current
            SceneManager.currentTraining?.onClick()
            true
        } else false
    }

    val Float.inDFov
        get() = when (VideoOption.fovMode.value) {
            VideoOption.FOVMode.DFOV -> this
            VideoOption.FOVMode.HFOV -> this.h2dFOV(RS.aspectD)
            VideoOption.FOVMode.VFOV -> this.v2dFOV(RS.aspectD)
        }

    override fun <S : AbstractSetting<*>> Crosshair.setting(setting: S): S {
        settings.add(setting)
        Language.add(setting.multiText)
        return setting
    }

}