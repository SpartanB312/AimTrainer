package net.spartanb312.boar.game.option.impls

import net.spartanb312.boar.game.config.setting.atMode
import net.spartanb312.boar.game.option.Option
import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.utils.math.MathUtils.d2vFOV
import net.spartanb312.boar.utils.math.MathUtils.h2vFOV
import net.spartanb312.boar.utils.math.MathUtils.v2dFOV
import net.spartanb312.boar.utils.misc.AliasNameable
import net.spartanb312.boar.utils.misc.DisplayEnum

object VideoOption : Option("Video") {

    val videoMode = setting("Video Mode", VideoMode.VSync)
    val fpsLimit by setting("FPS Limit", 120, 30..2000,10).atMode(videoMode, VideoMode.Custom)

    val fovMode = setting("FOV Mode", FOVMode.DFOV)
    private val dFOV by setting("Diagonal FOV", 78f, 60f..150f, 0.5f).atMode(fovMode, FOVMode.DFOV)
    private val hFOV by setting("Horizontal FOV", 78f, 60f..150f, 0.5f).atMode(fovMode, FOVMode.HFOV)
    private val vFOV by setting("Vertical FOV", 78f, 60f..150f, 0.5f).atMode(fovMode, FOVMode.VFOV)
    val fov
        get() = when (fovMode.value) {
            FOVMode.DFOV -> dFOV.d2vFOV(RS.aspectD)
            FOVMode.HFOV -> hFOV.h2vFOV(RS.aspectD)
            FOVMode.VFOV -> vFOV
        }

    val actualFovValue
        get() = when (fovMode.value) {
            FOVMode.DFOV -> dFOV
            FOVMode.HFOV -> hFOV
            FOVMode.VFOV -> vFOV
        }

    val fovType get() = fovMode.value.displayName
    val dfov get() = fov.v2dFOV(RS.aspectD)

    enum class FOVMode(override val displayName: String, override var aliasName: String) : DisplayEnum, AliasNameable {
        DFOV("D-FOV", "Diagonal"),
        HFOV("H-FOV", "Horizontal"),
        VFOV("V-FOV", "Vertical")
    }

    enum class VideoMode(override val displayName: CharSequence) : DisplayEnum {
        VSync("Vertical Sync"),
        Custom("Custom"),
        Unlimited("Unlimited")
    }

}