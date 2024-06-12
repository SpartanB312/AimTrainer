package net.spartanb312.everett.game.option.impls

import net.spartanb312.everett.game.event.TickEvent
import net.spartanb312.everett.game.option.Option
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.utils.config.setting.lang
import net.spartanb312.everett.utils.config.setting.whenTrue
import net.spartanb312.everett.utils.event.listener
import net.spartanb312.everett.utils.language.Languages
import net.spartanb312.everett.utils.timing.Timer
import kotlin.random.Random

object AccessibilityOption : Option("Accessibility") {

    val waitTime by setting("Wait Time", 5, 0..10)
        .lang("等待时间", "等待時間")
    val pingSimulate = setting("Ping Simulate", false)
        .lang("延迟模拟", "延遲模擬")
    val simulatedPing by setting("Simulated Ping", 0, 0..500, 5)
        .lang("目标延迟", "目標延遲")
        .whenTrue(pingSimulate)
    val jitter by setting("Jitter Simulate", false)
        .lang("抖动模拟", "顫動模擬")
        .whenTrue(pingSimulate)
    val jitterRange by setting("Jitter Range", 0, 0..100)
        .lang("抖动幅度", "顫動幅度")
    val language by setting("Language", Languages.English)
        .lang("显示语言", "語言")
    val chunkCache by setting("Font Cache Preload", true)
        .lang("字体缓存预加载", "字型緩存預載入")
    val threadsLimit by setting("Max Threads", RS.maxThreads, 1..RS.maxThreads, 1)
        .lang("最大线程数量", "最大線程數量")

    var ping = 0; private set
    private val jitterTimer = Timer()

    init {
        listener<TickEvent.Post> {
            jitterTimer.tps(10) {
                ping = if (!pingSimulate.value) 0
                else {
                    if (!jitter) simulatedPing
                    else {
                        val range = (simulatedPing - jitterRange)..(simulatedPing + jitterRange)
                        val base = ping.coerceIn(range)
                        val delta = if (jitterRange > 0) Random.nextInt(0, jitterRange) else 0
                        if (delta != 0) base + Random.nextInt(-delta / 3, delta)
                        else ping
                    }
                }
                ping = ping.coerceAtLeast(0)
            }
        }
        subscribe()
    }
}