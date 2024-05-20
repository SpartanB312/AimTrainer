package net.spartanb312.everett.game.option.impls

import net.spartanb312.everett.game.Language.m
import net.spartanb312.everett.game.option.Option
import net.spartanb312.everett.game.render.scene.SceneManager
import net.spartanb312.everett.utils.config.setting.at
import net.spartanb312.everett.utils.config.setting.atMode
import net.spartanb312.everett.utils.config.setting.m
import net.spartanb312.everett.utils.config.setting.whenTrue
import net.spartanb312.everett.utils.language.MultiText
import net.spartanb312.everett.utils.misc.DisplayEnum

object AimAssistOption : Option("AimAssist") {

    // Adsorption
    val bulletAdsorption = setting("Bullet Adsorption", true)
        .m("子弹吸附", "子彈吸附")

    // Aim Assist
    val aimAssist = setting("Aim Assist", true)
        .m("辅助瞄准", "輔助瞄準")
    private val aaType = setting("Aim Assist Type", AAType.Magnetism)
        .m("辅助瞄准类型", "輔助瞄準類別")
        .whenTrue(aimAssist)

    // Move Compensation
    val moveCompensation by setting("Move Compensation", 0.21, 0.0..2.0, 0.01)
        .m("移动补偿率", "移動補償修正")
        .atMode(aaType, AAType.Magnetism)
        .whenTrue(aimAssist)
    val firingCompensation by setting("Firing Compensation", 0.7, 0.0..2.0, 0.01)
        .m("开火补偿率", "開火補償修正")
        .atMode(aaType, AAType.Magnetism)
        .whenTrue(aimAssist)
    val zeroAngleFriction by setting("Friction Aim Assist", true)
        .m("摩擦力补偿", "摩擦力補償")
        .atMode(aaType, AAType.Magnetism)
        .whenTrue(aimAssist)

    // Friction
    private val raytraceAA by setting("Raytracing AA Value", 0.21, 0.0..1.0, 0.01)
        .m("瞄准摩擦力", "瞄準摩擦係數")
        .at { aaType.value == AAType.Friction || zeroAngleFriction }
        .whenTrue(aimAssist)
    private val firingAA by setting("Firing AA Value", 0.4, 0.0..1.0, 0.01)
        .m("开火摩擦力", "開火摩擦係數")
        .at { aaType.value == AAType.Friction || zeroAngleFriction }
        .whenTrue(aimAssist)

    val errorAngle get() = SceneManager.errorAngle

    val firingCoefficient get() = 1.0 - firingAA
    val raytraceCoefficient get() = 1.0 - raytraceAA
    val mcEnabled get() = aaType.value == AAType.Magnetism && aimAssist.value
    val frEnabled get() = aaType.value == AAType.Friction && aimAssist.value

    enum class AAType(multiText: MultiText) : DisplayEnum {
        Friction("Friction".m("摩擦力", "摩擦力")),
        Magnetism("Magnetism".m("磁性补偿", "磁力補償"));

        override val displayName by multiText
    }

}