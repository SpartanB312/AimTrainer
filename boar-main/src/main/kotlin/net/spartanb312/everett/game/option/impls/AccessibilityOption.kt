package net.spartanb312.everett.game.option.impls

import net.spartanb312.everett.game.option.Option
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.utils.config.setting.lang
import net.spartanb312.everett.utils.language.Languages

object AccessibilityOption : Option("Accessibility") {

    val waitTime by setting("Wait Time", 5, 0..10)
        .lang("等待时间", "等待時間")
    val language by setting("Language", Languages.English)
        .lang("显示语言", "語言")
    val chunkCache by setting("Font Cache Preload", true)
        .lang("字体缓存预加载", "字型緩存預載入")
    val threadsLimit by setting("Max Threads", RS.maxThreads, 1..RS.maxThreads, 1)
        .lang("最大线程数量", "最大線程數量")

}