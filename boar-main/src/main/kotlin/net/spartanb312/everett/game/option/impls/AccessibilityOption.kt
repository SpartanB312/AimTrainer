package net.spartanb312.everett.game.option.impls

import net.spartanb312.everett.AimTrainer
import net.spartanb312.everett.game.Language.lang
import net.spartanb312.everett.game.event.TickEvent
import net.spartanb312.everett.game.option.Option
import net.spartanb312.everett.game.render.FontCacheManager
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.utils.config.setting.lang
import net.spartanb312.everett.utils.config.setting.whenTrue
import net.spartanb312.everett.utils.event.listener
import net.spartanb312.everett.utils.language.Languages
import net.spartanb312.everett.utils.language.MultiText
import net.spartanb312.everett.utils.misc.DisplayEnum
import net.spartanb312.everett.utils.timing.Timer
import kotlin.random.Random

object AccessibilityOption : Option("Accessibility") {

    val language by setting("Language", Languages.English)
        .lang("显示语言", "語言")
    var flushFont: ResetProgress by setting("Flush Font", ResetProgress.Ready)
        .lang("重置字体", "重置字型")
        .valueListen { prev, input ->
            if (input == ResetProgress.Cooling && !flushLocked) {
                flushLocked = true
                FontCacheManager.reset()
                AimTrainer.taskManager.runLater(1000) {
                    flushLocked = false
                    flushFont = ResetProgress.Ready
                }
            }
            if (input == ResetProgress.Ready && prev == ResetProgress.Cooling && flushLocked) {
                flushFont = ResetProgress.Cooling
            }
        }
    val chunkCache by setting("Font Cache Preload", true)
        .lang("字体缓存预加载", "字型緩存預載入")
    val asyncFontLoad by setting("Async Glyph Loading", true)
        .lang("异步字体加载", "異步字型載入")
    val threadsLimit by setting("Max Threads", RS.maxThreads, 1..RS.maxThreads, 1)
        .lang("最大线程数量", "最大線程數量")
    val waitTime by setting("Wait Time", 5, 0..10)
        .lang("等待时间", "等待時間")

    val pingSimulate = setting("Ping Simulate", false)
        .lang("延迟模拟", "延遲模擬")
    val simulatedPing by setting("Simulated Ping", 0, 0..500, 5)
        .lang("目标延迟", "目標延遲")
        .whenTrue(pingSimulate)
    val jitter = setting("Jitter Simulate", false)
        .lang("抖动模拟", "顫動模擬")
        .whenTrue(pingSimulate)
    val jitterRange by setting("Jitter Range", 10, 0..100)
        .lang("抖动幅度", "顫動幅度")
        .whenTrue(pingSimulate)
        .whenTrue(jitter)
    val pktLossSimulate = setting("Packet Loss Simulate", false)
        .lang("封包丢失模拟", "封包丟失模擬")
    val pktLossRate by setting("Packet Loss Rate", 0, 0..100)
        .lang("丟包率", "丟包率")
        .whenTrue(pktLossSimulate)

    val shouldPktLoss get() = Random.nextInt(0, 99) < if (pktLossSimulate.value) pktLossRate else 0

    private var flushLocked = false
    var ping = 0; private set
    private val jitterTimer = Timer()

    init {
        listener<TickEvent.Post> {
            jitterTimer.tps(10) {
                ping = if (!pingSimulate.value) 0
                else {
                    if (!jitter.value) simulatedPing
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

    enum class ResetProgress(multiText: MultiText) : DisplayEnum {
        Ready("Ready".lang("就绪", "就緒")),
        Cooling("Cooling".lang("冷却中", "冷卻中"));

        override val displayName by multiText
    }

}