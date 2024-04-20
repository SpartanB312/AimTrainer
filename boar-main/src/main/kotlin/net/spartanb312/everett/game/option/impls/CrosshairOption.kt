package net.spartanb312.everett.game.option.impls

import net.spartanb312.everett.game.config.setting.atMode
import net.spartanb312.everett.game.config.setting.m
import net.spartanb312.everett.game.option.Option
import net.spartanb312.everett.game.render.crosshair.Crosshairs

object CrosshairOption : Option("Crosshair") {

    val crosshairType = setting("Crosshair Type", Crosshairs.Cross)
        .m("准星类型", "準星類別")

    init {
        Crosshairs.entries.forEach {
            it.crosshair.settings.forEach { s ->
                setting(s.atMode(crosshairType, it))
            }
        }
    }

}