package net.spartanb312.boar.game.option.impls

import net.spartanb312.boar.game.config.setting.m
import net.spartanb312.boar.game.option.Option

object AudioOption : Option("AudioOption") {

    private val volume by setting("Volume", 100, 0..100, 1).m("全局", "全局")
    private val musicVolume by setting("Music", 100, 0..100, 1).m("音乐", "音樂")
    private val envVolume by setting("Environment", 100, 0..100, 1).m("环境", "環境")
    private val hitEffectVolume by setting("Hit Effect", 100, 0..100, 1).m("命中音效", "命中音效")

    val music get() = volume * musicVolume / 10000f
    val environment get() = volume * envVolume / 10000f
    val hitEffect get() = volume * hitEffectVolume / 10000f

}