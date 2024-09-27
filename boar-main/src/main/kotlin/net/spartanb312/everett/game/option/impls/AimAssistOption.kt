package net.spartanb312.everett.game.option.impls

import net.spartanb312.everett.game.Language.lang
import net.spartanb312.everett.game.option.Option
import net.spartanb312.everett.game.render.CrosshairRenderer
import net.spartanb312.everett.game.render.scene.SceneManager
import net.spartanb312.everett.utils.config.setting.*
import net.spartanb312.everett.utils.language.MultiText
import net.spartanb312.everett.utils.misc.DisplayEnum

object AimAssistOption : Option("AimAssist") {

    // Adsorption
    val bulletAdsorption = setting("Bullet Adsorption", true)
        .lang("子弹吸附", "子彈吸附")

    // Aim Assist
    val aimAssist = setting("Aim Assist", true)
        .lang("辅助瞄准", "輔助瞄準")
    private val aaType = setting("Aim Assist Type", AAType.Magnetism)
        .lang("辅助瞄准类型", "輔助瞄準類別")
        .whenTrue(aimAssist)

    // AA tick
    val noAATickLimit = setting("AA Tick Per Frame", false)
        .lang("辅瞄Tick与帧数同步", "輔瞄Tick和幀數同步")
        .whenTrue(aimAssist)
    val aaTPS by setting("Aim Assist TPS", 240, 60..5000, 10)
        .lang("辅助瞄准TPS", "輔助瞄準TPS")
        .whenTrue(aimAssist)
        .whenFalse(noAATickLimit)

    // Move Compensation
    val moveCompensation by setting("Move Compensation", 0.14, 0.0..1.0, 0.01)
        .lang("移动补偿率", "移動補償修正")
        .atMode(aaType, AAType.Magnetism)
        .whenTrue(aimAssist)
    val firingCompensation by setting("Firing Compensation", 0.4, 0.0..1.0, 0.01)
        .lang("开火补偿率", "開火補償修正")
        .atMode(aaType, AAType.Magnetism)
        .whenTrue(aimAssist)
    val pitchOptimization by setting("Pitch Optimization", true)
        .lang("俯仰角修正", "俯仰角修正")
        .atMode(aaType, AAType.Magnetism)
        .whenTrue(aimAssist)
    val zeroAngleFriction by setting("Friction Aim Assist", true)
        .lang("摩擦力补偿", "摩擦力補償")
        .atMode(aaType, AAType.Magnetism)
        .whenTrue(aimAssist)

    // Friction
    private val raytraceAA by setting("Raytracing AA Value", 0.14, 0.0..1.0, 0.01)
        .lang("瞄准摩擦力", "瞄準摩擦係數")
        .at { aaType.value == AAType.Friction || zeroAngleFriction }
        .whenTrue(aimAssist)
    private val firingAA by setting("Firing AA Value", 0.4, 0.0..1.0, 0.01)
        .lang("开火摩擦力", "開火摩擦係數")
        .at { aaType.value == AAType.Friction || zeroAngleFriction }
        .whenTrue(aimAssist)

    val actualErrorAngle
        get() = if (CrosshairRenderer.overrideErrorAngle != -1f) CrosshairRenderer.overrideErrorAngle
        else SceneManager.errorAngle

    val firingCoefficient get() = 1.0 - firingAA
    val raytraceCoefficient get() = 1.0 - raytraceAA
    val mcEnabled get() = aaType.value == AAType.Magnetism && aimAssist.value
    val frEnabled get() = aaType.value == AAType.Friction && aimAssist.value

    enum class AAType(multiText: MultiText) : DisplayEnum {
        Friction("Friction".lang("摩擦力", "摩擦力")),
        Magnetism("Magnetism".lang("磁性补偿", "磁力補償"));

        override val displayName by multiText
    }

}