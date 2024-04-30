package net.spartanb312.everett.game.option.impls

import net.spartanb312.everett.game.Language.m
import net.spartanb312.everett.utils.config.setting.atMode
import net.spartanb312.everett.utils.config.setting.m
import net.spartanb312.everett.game.option.Option
import net.spartanb312.everett.game.render.Background
import net.spartanb312.everett.game.render.Radar
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.utils.language.MultiText
import net.spartanb312.everett.utils.math.MathUtils.d2vFOV
import net.spartanb312.everett.utils.math.MathUtils.h2vFOV
import net.spartanb312.everett.utils.math.MathUtils.v2dFOV
import net.spartanb312.everett.utils.misc.AliasNameable
import net.spartanb312.everett.utils.misc.DisplayEnum

object VideoOption : Option("Video") {

    val videoMode = setting("Video Mode", VideoMode.VSync)
        .m("视频模式", "視訊模式")
    val fpsLimit by setting("FPS Limit", 120, 30..2000, 10)
        .m("帧数限制", "幀數上限")
        .atMode(videoMode, VideoMode.Custom)

    val fovMode = setting("FOV Mode", FOVMode.DFOV)
        .m("FOV模式", "FOV模式")
    private val dFOV by setting("Diagonal FOV", 78f, 60f..150f, 0.5f)
        .m("对角FOV", "對角FOV")
        .atMode(fovMode, FOVMode.DFOV)
    private val hFOV by setting("Horizontal FOV", 78f, 60f..150f, 0.5f)
        .m("水平FOV", "水平FOV")
        .atMode(fovMode, FOVMode.HFOV)
    private val vFOV by setting("Vertical FOV", 78f, 60f..150f, 0.5f)
        .m("垂直FOV", "垂直FOV")
        .atMode(fovMode, FOVMode.VFOV)

    val radar = setting("Radar", true).m("雷达", "雷達")

    init {
        Radar.settings.forEach { setting(it) }
    }

    val shield by setting("Energy Shield", true)
        .m("能量护盾", "能量護盾")

    val backgroundMode by setting("Background Mode", Background.Mode.Nebula)
        .m("背景模式", "背景模式")
    val particle by setting("Particle Background", true)
        .m("粒子效果", "粒子特效")

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

    enum class FOVMode(multiText: MultiText, override var aliasName: String) : DisplayEnum, AliasNameable {
        DFOV("D-FOV".m("对角FOV", "對角FOV"), "Diagonal"),
        HFOV("H-FOV".m("水平FOV", "水平FOV"), "Horizontal"),
        VFOV("V-FOV".m("垂直FOV", "垂直FOV"), "Vertical");

        override val displayName by multiText
    }

    enum class VideoMode(multiText: MultiText) : DisplayEnum {
        VSync("Vertical Sync".m("垂直同步", "垂直同步")),
        Custom("Custom".m("自定义", "自訂")),
        Unlimited("Unlimited".m("解锁", "無上限"));

        override val displayName by multiText
    }

}