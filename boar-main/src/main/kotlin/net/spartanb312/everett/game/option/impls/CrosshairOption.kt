package net.spartanb312.everett.game.option.impls

import net.spartanb312.everett.utils.config.setting.atMode
import net.spartanb312.everett.utils.config.setting.m
import net.spartanb312.everett.game.option.Option
import net.spartanb312.everett.game.render.crosshair.Crosshairs

object CrosshairOption : Option("Crosshair") {

    val crosshairType = setting("Crosshair Type", Crosshairs.M392E)
        .m("准星类型", "準星類別")
    val shadow by setting("Shadow", true)
        .m("阴影", "陰影")

    init {
        Crosshairs.entries.forEach {
            it.crosshair.settings.forEach { s ->
                setting(s.atMode(crosshairType, it))
            }
        }
    }

}