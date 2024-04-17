package net.spartanb312.boar.game.option.impls

import net.spartanb312.boar.game.config.setting.m
import net.spartanb312.boar.game.option.Option
import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.graphics.RenderSystem
import net.spartanb312.boar.language.Languages

object AccessibilityOption : Option("Accessibility") {

    val language by setting("Language", Languages.English)
        .m("显示语言", "語言")
    val chunkCache by setting("Font Cache Preload", true)
        .m("字体缓存预加载", "字型緩存預載入")
    val threadsLimit by setting("Max Threads", RS.maxThreads, 1..RS.maxThreads, 1)
        .m("最大线程数量", "最大線程數量")

    private val ogl by setting("Graphics API", RenderSystem.GL.GL_450)

    fun getLaunchOGLVersion(): String {
        return ogl.displayName
    }

}