package net.spartanb312.everett.game.option.impls

import net.spartanb312.everett.utils.config.setting.lang
import net.spartanb312.everett.game.option.Option

object AudioOption : Option("AudioOption") {

    private val volume by setting("Volume", 100, 0..100, 1)
        .lang("全局", "全局")
    private val musicVolume by setting("Music", 100, 0..100, 1)
        .lang("音乐", "音樂")
    private val envVolume by setting("Environment", 100, 0..100, 1)
        .lang("环境", "環境")
    private val noteBoxVolume by setting("NoteBox", 100, 0..100, 1)
        .lang("合成音符", "合成音符")
    private val hitEffectVolume by setting("Hit Effect", 100, 0..100, 1)
        .lang("命中音效", "命中音效")
    val bgmInGame by setting("In Game BGM", false)
        .lang("游戏内音乐", "游戲内音樂")
    val tps by setting("Note Player TPS", 100, 1..500, 1)
        .lang("合成音符TPS", "合成音符TPS")

    val music get() = volume * musicVolume / 10000f
    val environment get() = volume * envVolume / 10000f
    val hitEffect get() = volume * hitEffectVolume / 10000f
    val noteBox get() = volume * noteBoxVolume / 10000f

}