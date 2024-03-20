package net.spartanb312.boar.game.option.impls

import net.spartanb312.boar.game.config.setting.atMode
import net.spartanb312.boar.game.config.setting.m
import net.spartanb312.boar.game.config.setting.whenTrue
import net.spartanb312.boar.game.option.Option
import net.spartanb312.boar.game.render.scene.SceneManager
import net.spartanb312.boar.language.Language.m
import net.spartanb312.boar.language.MultiText
import net.spartanb312.boar.utils.misc.DisplayEnum

object AimAssistOption : Option("AimAssist") {

    // Adsorption
    val bulletAdsorption = setting("Bullet Adsorption", true)
        .m("子弹磁性吸附", "子彈磁性吸附")
    val errorAngleRate by setting("Error Angle Rate", 1f, 0f..2f, 0.1f).whenTrue(bulletAdsorption)
        .m("吸附容错角比例", "容錯角比率")

    // Aim Assist
    val aimAssist = setting("Aim Assist", false)
        .m("辅助瞄准", "輔助瞄準")
    private val aaType = setting("Aim Assist Type", AAType.HaloInfinite)
        .m("辅助瞄准类型", "輔助瞄準類別")

    // Friction
    private val raytraceAA by setting("Raytrace Aim Assist", 0.21, 0.0..1.0, 0.01)
        .atMode(aaType, AAType.Friction)
        .whenTrue(aimAssist)
        .m("瞄准摩擦力", "瞄準摩擦係數")
    private val firingAA by setting("Firing Aim Assist", 0.4, 0.0..1.0, 0.01)
        .atMode(aaType, AAType.Friction)
        .whenTrue(aimAssist)
        .m("开火摩擦力", "開火摩擦係數")

    // Move Compensation
    val moveCompensation by setting("Move Compensation", 0.7f, 0f..10f, 0.1f)
        .atMode(aaType, AAType.HaloInfinite)
        .whenTrue(aimAssist)
        .m("移动补偿率", "移動補償修正")
    val firingCompensation by setting("Firing Compensation", 1.4f, 0f..10f, 0.1f)
        .atMode(aaType, AAType.HaloInfinite)
        .whenTrue(aimAssist)
        .m("开火补偿率", "開火補償修正")
    val zeroAngleFriction by setting("Zero Angle Friction", true)
        .atMode(aaType, AAType.HaloInfinite)
        .whenTrue(aimAssist)
        .m("静止摩擦力", "靜滯摩擦力")
    val zeroAngleFrictionRate by setting("Friction Rate", 1f, 0f..10f, 0.1f)
        .atMode(aaType, AAType.HaloInfinite)
        .whenTrue(aimAssist)
        .m("摩擦力系数", "摩擦力係數")

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