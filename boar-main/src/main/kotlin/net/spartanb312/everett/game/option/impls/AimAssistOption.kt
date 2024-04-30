package net.spartanb312.everett.game.option.impls

import net.spartanb312.everett.game.Language.m
import net.spartanb312.everett.utils.config.setting.at
import net.spartanb312.everett.utils.config.setting.atMode
import net.spartanb312.everett.utils.config.setting.m
import net.spartanb312.everett.utils.config.setting.whenTrue
import net.spartanb312.everett.game.option.Option
import net.spartanb312.everett.game.render.scene.SceneManager
import net.spartanb312.everett.utils.language.MultiText
import net.spartanb312.everett.utils.misc.DisplayEnum

object AimAssistOption : Option("AimAssist") {

    // Adsorption
    val bulletAdsorption = setting("Bullet Adsorption", true)
        .m("子弹磁性吸附", "子彈磁性吸附")
    val errorAngleRate by setting("Error Angle Rate", 1f, 0f..2f, 0.1f)
        .m("吸附容错角比例", "容錯角比率")
        .whenTrue(bulletAdsorption)

    // Aim Assist
    val aimAssist = setting("Aim Assist", true)
        .m("辅助瞄准", "輔助瞄準")
    private val aaType = setting("Aim Assist Type", AAType.HaloInfinite)
        .m("辅助瞄准类型", "輔助瞄準類別")
        .whenTrue(aimAssist)

    // Move Compensation
    val moveCompensation by setting("Move Compensation", 0.21, 0.0..2.0, 0.01)
        .m("移动补偿率", "移動補償修正")
        .atMode(aaType, AAType.HaloInfinite)
        .whenTrue(aimAssist)
    val firingCompensation by setting("Firing Compensation", 0.7, 0.0..2.0, 0.01)
        .m("开火补偿率", "開火補償修正")
        .atMode(aaType, AAType.HaloInfinite)
        .whenTrue(aimAssist)
    val zeroAngleFriction by setting("Zero Angle Friction", true)
        .m("静止摩擦力", "靜滯摩擦力")
        .atMode(aaType, AAType.HaloInfinite)
        .whenTrue(aimAssist)

    // Friction
    private val raytraceAA by setting("Raytrace Aim Assist", 0.21, 0.0..1.0, 0.01)
        .m("瞄准摩擦力", "瞄準摩擦係數")
        .at { aaType.value == AAType.Friction || zeroAngleFriction }
        .whenTrue(aimAssist)
    private val firingAA by setting("Firing Aim Assist", 0.4, 0.0..1.0, 0.01)
        .m("开火摩擦力", "開火摩擦係數")
        .at { aaType.value == AAType.Friction || zeroAngleFriction }
        .whenTrue(aimAssist)

    val errorAngle get() = SceneManager.errorAngle

    val firingCoefficient get() = 1.0 - firingAA
    val raytraceCoefficient get() = 1.0 - raytraceAA
    val mcEnabled get() = aaType.value == AAType.HaloInfinite && aimAssist.value
    val frEnabled get() = aaType.value == AAType.Friction && aimAssist.value

    enum class AAType(multiText: MultiText) : DisplayEnum {
        Friction("Friction".m("摩擦力", "摩擦力")),
        HaloInfinite("Halo Infinite".m("光环无限", "光暈無限"));

        override val displayName by multiText
    }

}