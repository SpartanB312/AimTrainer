package net.spartanb312.everett.game.option.impls

import net.spartanb312.everett.game.Language.lang
import net.spartanb312.everett.game.option.Option
import net.spartanb312.everett.game.render.Background
import net.spartanb312.everett.game.render.hud.Radar
import net.spartanb312.everett.graphics.GLHelper
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.antialias.ScreenAntiAlias
import net.spartanb312.everett.graphics.event.EngineLoopEvent
import net.spartanb312.everett.utils.Logger
import net.spartanb312.everett.utils.config.setting.atMode
import net.spartanb312.everett.utils.config.setting.lang
import net.spartanb312.everett.utils.config.setting.whenTrue
import net.spartanb312.everett.utils.event.listener
import net.spartanb312.everett.utils.language.Languages
import net.spartanb312.everett.utils.language.MultiText
import net.spartanb312.everett.utils.math.MathUtils.d2vFOV
import net.spartanb312.everett.utils.math.MathUtils.h2vFOV
import net.spartanb312.everett.utils.math.MathUtils.v2dFOV
import net.spartanb312.everett.utils.math.MathUtils.v2hFOV
import net.spartanb312.everett.utils.misc.AliasNameable
import net.spartanb312.everett.utils.misc.DisplayEnum
import org.lwjgl.glfw.GLFW

object VideoOption : Option("Video") {

    var displayMode by setting("Display Mode", DisplayMode.Windowed)
        .lang("显示模式", "顯示模式")
        .valueListen { prev, input ->
            if (input != prev) GLHelper.setDisplayMode(input)
        }

    val fullScreenMode by setting("Resolution", Resolution.Dummy1)
        .lang("分辨率", "解析度")
        .valueListen { prev, input ->
            if (input != prev) {
                when (prev) {
                    Resolution.Dummy1 -> if (input == Resolution.Dummy2) nextMode() else prevMode()
                    Resolution.Dummy2 -> if (input == Resolution.Dummy3) nextMode() else prevMode()
                    Resolution.Dummy3 -> if (input == Resolution.Dummy1) nextMode() else prevMode()
                }
            }
        }

    val useFramebuffer = setting("Use Framebuffer", false)
        .lang("使用帧缓冲", "使用幀緩衝")
        .valueListen { _, input -> if (!input) RS.setRenderScale(1f) }
    val renderRate = setting("Render Scale", 100, 10..400, 1)
        .lang("渲染比例", "渲染比率")
        .whenTrue(useFramebuffer)
    val antiAlias by setting("Anti Alias", ScreenAntiAlias.Mode.MSAA4X)
        .lang("抗锯齿", "反走樣")
        .whenTrue(useFramebuffer)
    val ffxCAS = setting("AMD FidelityFX CAS", true)
        .lang("AMD FFX CAS 锐化", "AMD FFX CAS 銳化")
        .whenTrue(useFramebuffer)
    val casMode by setting("CAS Mode", CASMode.RCAS)
        .lang("CAS 模式", "CAS 模式")
        .whenTrue(useFramebuffer)
        .whenTrue(ffxCAS)
    val sharpness by setting("CAS Sharpness", 1f, 0f..5f, 0.1f)
        .lang("CAS 锐度", "CAS 銳度")
        .whenTrue(useFramebuffer)
        .whenTrue(ffxCAS)
    val videoMode = setting("Video Mode", VideoMode.Unlimited)
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

    val lighting by setting("Lightning", true)
        .lang("光照", "光照")

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

    val backgroundMode = setting("Background Mode", Background.Mode.Sandbox)
        .lang("背景模式", "背景模式")
    val particle by setting("Particle Background", false)
        .lang("粒子效果", "粒子特效")
        .atMode(backgroundMode, Background.Mode.Default)
    val sandbox by setting("Sandbox", Background.ShaderMode.BlackHole)
        .lang("沙盒渲染", "沙盒渲染")
        .atMode(backgroundMode, Background.Mode.Sandbox)

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

    enum class CASMode(override val displayName: CharSequence) : DisplayEnum {
        FAST("Fast"),
        RCAS("RCAS")
    }

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

    init {
        listener<EngineLoopEvent.Loop.Pre> {
            if (displayMode == DisplayMode.FullScreen && shouldUpdate) {
                shouldUpdate = false
                val mode = resolutions[index]
                GLFW.glfwSetWindowMonitor(RS.window, monitor, 0, 0, mode.first, mode.second, mode.third)
            }
        }
        subscribe()
    }

    private val resStr = MultiText("")
    private val monitor = GLFW.glfwGetPrimaryMonitor()
    private val resolutions = kotlin.run {
        val modes = GLFW.glfwGetVideoModes(monitor)
        modes!!.map { Triple(it.width(), it.height(), it.refreshRate()) }
    }
    private var index = resolutions.size - 1
    private var shouldUpdate = false

    private fun updateMode(triple: Triple<Int, Int, Int>) {
        val str = "${triple.first}x${triple.second}@${triple.third}Hz"
        resStr.addLang(Languages.English, str)
        Logger.info("Fullscreen mode: $str")
        shouldUpdate = true
    }

    private fun nextMode() {
        if (index == resolutions.size - 1) index = 0 else index++
        updateMode(resolutions[index])
    }

    private fun prevMode() {
        if (index == 0) index = resolutions.size - 1 else index--
        updateMode(resolutions[index])
    }

    enum class Resolution : DisplayEnum {
        Dummy1,
        Dummy2,
        Dummy3;

        override val displayName by resStr
    }

    enum class DisplayMode(multiText: MultiText) : DisplayEnum {
        Windowed("Windowed".lang("窗口化", "窗口化")),

        //Borderless("Borderless".lang("无边框", "無邊框"));
        FullScreen("FullScreen".lang("全屏", "全屏"));

        override val displayName by multiText
    }

}