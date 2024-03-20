package net.spartanb312.boar.game.option.impls

import net.spartanb312.boar.game.config.setting.m
import net.spartanb312.boar.game.option.Option
import net.spartanb312.boar.language.Languages

object AccessibilityOption : Option("Accessibility") {

    val language by setting("Language", Languages.English).m("显示语言", "語言")
    val chunkCache by setting("Font Cache Preload", true).m("字体缓存预加载", "字型緩存預載入")

}