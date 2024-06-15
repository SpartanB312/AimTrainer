package net.spartanb312.everett.game.option.impls

import net.spartanb312.everett.game.Language.lang
import net.spartanb312.everett.game.option.Option
import net.spartanb312.everett.game.render.Background
import net.spartanb312.everett.game.render.hud.impls.Radar
import net.spartanb312.everett.graphics.GLHelper
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.utils.config.setting.atMode
import net.spartanb312.everett.utils.config.setting.lang
import net.spartanb312.everett.utils.config.setting.whenTrue
import net.spartanb312.everett.utils.language.MultiText
import net.spartanb312.everett.utils.math.MathUtils.d2vFOV
import net.spartanb312.everett.utils.math.MathUtils.h2vFOV
import net.spartanb312.everett.utils.math.MathUtils.v2dFOV
import net.spartanb312.everett.utils.math.MathUtils.v2hFOV
import net.spartanb312.everett.utils.misc.AliasNameable
import net.spartanb312.everett.utils.misc.DisplayEnum

object VideoOption : Option("Video") {

    var fullScreen by setting("Full Screen", false)
        .lang("全屏", "全屏")
        .valueListen { prev, input ->
            if (input != prev) GLHelper.fullScreen = input
        }
    val useFramebuffer = setting("Use Framebuffer", true)
        .lang("使用帧缓冲", "使用幀緩衝")
        .valueListen { _, input -> if (!input) RS.setRenderScale(1f) }
    val renderRate = setting("Render Scale", 100, 50..400)
        .lang("渲染比例", "渲染比率")
        .whenTrue(useFramebuffer)
    val videoMode = setting("Video Mode", VideoMode.VSync)
        .lang("视频模式", "視訊模式")
    val fpsLimit by setting("FPS Limit", 120, 30..2000, 10)
        .lang("帧数限制", "幀數上限")
        .atMode(videoMode, VideoMode.Custom)

    val fovMode = setting("FOV Mode", FOVMode.DFOV)
        .lang("FOV模式", "FOV模式")
    private val dFOV by setting("Diagonal FOV", 78f, 60f..150f, 0.5f)
        .lang("对角FOV", "對角FOV")
        .atMode(fovMode, FOVMode.DFOV)
    private val hFOV by setting("Horizontal FOV", 78f, 60f..150f, 0.5f)
        .lang("水平FOV", "水平FOV")
        .atMode(fovMode, FOVMode.HFOV)
    private val vFOV by setting("Vertical FOV", 78f, 60f..150f, 0.5f)
        .lang("垂直FOV", "垂直FOV")
        .atMode(fovMode, FOVMode.VFOV)

    val ping by setting("Ping", true)
        .lang("延迟信息", "延遲顯示")
    val fps by setting("FPS", true)
        .lang("帧数信息", "幀數顯示")
    val info by setting("Stat Info", false)
        .lang("统计信息", "統計信息")

    val radar = setting("Radar", true).lang("雷达", "雷達")

    init {
        Radar.settings.forEach { setting(it) }
    }

    val shield by setting("Energy Shield", true)
        .lang("能量护盾", "能量護盾")

    val backgroundMode = setting("Background Mode", Background.Mode.Nebula)
        .lang("背景模式", "背景模式")
    val particle by setting("Particle Background", true)
        .lang("粒子效果", "粒子特效")
        .atMode(backgroundMode, Background.Mode.Halo)

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

    val framebuffer get() = useFramebuffer.value
    val renderScale get() = if (framebuffer) renderRate.value else 100
    inline val fovType get() = fovMode.value.displayName
    inline val dfov get() = fov.v2dFOV(RS.aspectD)
    inline val hfov get() = fov.v2hFOV(RS.aspectD)
    inline val vfov get() = fov

    enum class FOVMode(multiText: MultiText, override var aliasName: String) : DisplayEnum, AliasNameable {
        DFOV("D-FOV".lang("对角FOV", "對角FOV"), "Diagonal"),
        HFOV("H-FOV".lang("水平FOV", "水平FOV"), "Horizontal"),
        VFOV("V-FOV".lang("垂直FOV", "垂直FOV"), "Vertical");

        override val displayName by multiText
    }

    enum class VideoMode(multiText: MultiText) : DisplayEnum {
        VSync("Vertical Sync".lang("垂直同步", "垂直同步")),
        Custom("Custom".lang("自定义", "自訂")),
        Unlimited("Unlimited".lang("解锁", "無上限"));

        override val displayName by multiText
    }

}