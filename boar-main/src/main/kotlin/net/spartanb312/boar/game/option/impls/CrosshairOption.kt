package net.spartanb312.boar.game.option.impls

import net.spartanb312.boar.game.config.setting.atMode
import net.spartanb312.boar.game.option.Option
import net.spartanb312.boar.game.render.crosshair.Crosshairs

object CrosshairOption : Option("Crosshair") {
    val crosshairType = setting("Crosshair Type", Crosshairs.Cross)

    init {
        Crosshairs.entries.forEach {
            it.crosshair.settings.forEach { s ->
                setting(s.atMode(crosshairType, it))
            }
        }
    }
}