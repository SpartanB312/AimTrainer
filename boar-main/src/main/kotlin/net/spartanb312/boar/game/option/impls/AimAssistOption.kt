package net.spartanb312.boar.game.option.impls

import net.spartanb312.boar.game.config.setting.atMode
import net.spartanb312.boar.game.config.setting.whenTrue
import net.spartanb312.boar.game.option.Option
import net.spartanb312.boar.game.render.scene.SceneManager

object AimAssistOption : Option("AimAssist") {

    // Adsorption
    val bulletAdsorption = setting("Bullet Adsorption", true)
    val errorAngleRate by setting("Error Angle Rate", 1f, 0f..2f, 0.1f).whenTrue(bulletAdsorption)

    // Aim Assist
    val aimAssist = setting("Aim Assist", false)
    private val aaType = setting("Aim Assist Type", AAType.HaloInfinite)

    // Friction
    private val firingAA by setting("Firing Aim Assist", 0.4, 0.0..1.0, 0.01)
        .atMode(aaType, AAType.Friction)
        .whenTrue(aimAssist)
    private val raytraceAA by setting("Raytrace Aim Assist", 0.21, 0.0..1.0, 0.01)
        .atMode(aaType, AAType.Friction)
        .whenTrue(aimAssist)

    // Move Compensation
    val moveCompensation by setting("Move Compensation", 0.7f, 0f..10f, 0.1f)
        .atMode(aaType, AAType.HaloInfinite)
        .whenTrue(aimAssist)
    val firingCompensation by setting("Firing Compensation", 1.4f, 0f..10f, 0.1f)
        .atMode(aaType, AAType.HaloInfinite)
        .whenTrue(aimAssist)
    val zeroAngleFriction by setting("Zero Angle Friction", true)
        .atMode(aaType, AAType.HaloInfinite)
        .whenTrue(aimAssist)
    val zeroAngleFrictionRate by setting("Friction Rate", 1f, 0f..10f, 0.1f)
        .atMode(aaType, AAType.HaloInfinite)
        .whenTrue(aimAssist)

    val errorAngle get() = SceneManager.errorAngle

    val firingCoefficient get() = 1.0 - firingAA
    val raytraceCoefficient get() = 1.0 - raytraceAA
    val mcEnabled get() = aaType.value == AAType.HaloInfinite && aimAssist.value
    val frEnabled get() = aaType.value == AAType.Friction && aimAssist.value

    enum class AAType {
        Friction,
        HaloInfinite,
    }

}