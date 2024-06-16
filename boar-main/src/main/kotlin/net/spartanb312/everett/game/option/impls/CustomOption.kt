package net.spartanb312.everett.game.option.impls

import net.spartanb312.everett.game.option.Option
import net.spartanb312.everett.game.training.custom.CustomBallHit

object CustomOption : Option("Custom") {
    init {
        CustomBallHit.settings.forEach { setting(it) }
    }
}