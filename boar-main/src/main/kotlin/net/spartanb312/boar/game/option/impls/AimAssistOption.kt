package net.spartanb312.boar.game.option.impls

import net.spartanb312.boar.game.config.setting.atMode
import net.spartanb312.boar.game.config.setting.whenTrue
import net.spartanb312.boar.game.option.Option

object AimAssistOption : Option("AimAssist") {

    val bulletAdsorption by setting("Bullet Adsorption", true)
    val aimAssist = setting("Aim Assist", false)
    private val aaType = setting("Aim Assist Type", AAType.MoveComposition)

    // Friction
    private val firingAA by setting("Firing Aim Assist", 0.4, 0.0..1.0, 0.01)
        .atMode(aaType, AAType.Friction)
        .whenTrue(aimAssist)
    private val raytraceAA by setting("Raytrace Aim Assist", 0.21, 0.0..1.0, 0.01)
        .atMode(aaType, AAType.Friction)
        .whenTrue(aimAssist)

    // Move Composition
    val moveComposition by setting("Move Composition", 0.7f, 0f..10f, 0.1f)
        .atMode(aaType, AAType.MoveComposition)
        .whenTrue(aimAssist)
    val firingComposition by setting("Firing Composition", 1.4f, 0f..10f, 0.1f)
        .atMode(aaType, AAType.MoveComposition)
        .whenTrue(aimAssist)
    val zeroAngleFriction by setting("Zero Angle Friction", true)
        .atMode(aaType, AAType.MoveComposition)
        .whenTrue(aimAssist)

    val firingCoefficient get() = 1.0 - firingAA
    val raytraceCoefficient get() = 1.0 - raytraceAA
    val mcEnabled get() = aaType.value == AAType.MoveComposition && aimAssist.value
    val frEnabled get() = aaType.value == AAType.Friction && aimAssist.value

    enum class AAType {
        Friction,
        MoveComposition,
    }

}