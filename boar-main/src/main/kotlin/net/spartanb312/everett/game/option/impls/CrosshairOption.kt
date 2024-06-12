package net.spartanb312.everett.game.option.impls

import net.spartanb312.everett.game.option.Option
import net.spartanb312.everett.game.crosshair.Crosshairs
import net.spartanb312.everett.utils.config.setting.atMode
import net.spartanb312.everett.utils.config.setting.lang

object CrosshairOption : Option("Crosshair") {

    val crosshairType = setting("Crosshair Type", Crosshairs.M392E)
        .lang("准星类型", "準星類別")
    val shadow by setting("Shadow", true)
        .lang("阴影", "陰影")
    val noCoolDown by setting("No Cool Down", false)
        .lang("无冷却", "無冷卻")

    init {
        Crosshairs.entries.forEach {
            it.crosshair.settings.forEach { s ->
                setting(s.atMode(crosshairType, it))
            }
        }
    }

}