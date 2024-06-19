package net.spartanb312.everett.game.option.impls

import net.spartanb312.everett.game.option.Option
import net.spartanb312.everett.game.training.custom.CustomTraining

object CustomOption : Option("Custom") {
    init {
        CustomTraining.settings.forEach { setting(it) }
        CustomTraining.Modes.entries.forEach {
            it.settingGroup.settings.forEach { setting ->
                setting(setting)
            }
        }
    }
}