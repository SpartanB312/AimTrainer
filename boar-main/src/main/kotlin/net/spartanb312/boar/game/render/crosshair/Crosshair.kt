package net.spartanb312.boar.game.render.crosshair

import net.spartanb312.boar.game.config.setting.AbstractSetting
import net.spartanb312.boar.game.config.setting.SettingRegister
import net.spartanb312.boar.game.option.impls.VideoOption
import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.language.Language
import net.spartanb312.boar.utils.color.ColorRGB
import net.spartanb312.boar.utils.math.MathUtils.h2dFOV
import net.spartanb312.boar.utils.math.MathUtils.v2dFOV
import net.spartanb312.boar.utils.math.ceilToInt

abstract class Crosshair(resetTime: Float, private val errorRate: Float = 0f) : SettingRegister<Crosshair> {
    val settings = mutableListOf<AbstractSetting<*>>()
    val resetTime = resetTime.ceilToInt()
    protected abstract var clickTime: Long
    abstract fun onRender(fov: Float, test: Boolean, colorRGB: ColorRGB)
    open fun onClick(): Boolean {
        val current = System.currentTimeMillis()
        val offset = current - clickTime
        return if (offset >= resetTime) {
            clickTime = current
            true
        } else if (offset / resetTime.toFloat() > 1f - errorRate) {
            clickTime += resetTime
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