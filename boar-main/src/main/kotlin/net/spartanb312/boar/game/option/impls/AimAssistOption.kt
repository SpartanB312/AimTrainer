package net.spartanb312.boar.game.option.impls

import net.spartanb312.boar.game.config.setting.whenTrue
import net.spartanb312.boar.game.option.Option

object AimAssistOption : Option("AimAssist") {
    val bulletAdsorption by setting("Bullet Adsorption", true)
    private val aimAssist = setting("Aim Assist", false)
    private val firingAA by setting("Firing AA", 0.4, 0.0..1.0, 0.01).whenTrue(aimAssist)
    private val raytraceAA by setting("Raytrace AA", 0.21, 0.0..1.0, 0.01).whenTrue(aimAssist)
    val firingCoefficient get() = 1.0 - (if (aimAssist.value) firingAA else 0.0)
    val raytraceCoefficient get() = 1.0 - (if (aimAssist.value) raytraceAA else 0.0)
}