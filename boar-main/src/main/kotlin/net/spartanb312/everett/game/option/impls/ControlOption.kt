package net.spartanb312.everett.game.option.impls

import net.spartanb312.everett.game.Language.m
import net.spartanb312.everett.utils.config.setting.at
import net.spartanb312.everett.utils.config.setting.m
import net.spartanb312.everett.utils.config.setting.whenTrue
import net.spartanb312.everett.game.option.Option
import net.spartanb312.everett.physics.PhysicsSystem
import net.spartanb312.everett.utils.language.MultiText
import net.spartanb312.everett.utils.misc.DisplayEnum

object ControlOption : Option("Control") {

    // Aiming
    private val preciseSensitivity = setting("Precise Sensitivity", false)
        .m("精准灵敏度", "精確靈敏度")
    private val sensitivityRough by setting("Sensitivity", 2.2f, 0.1f..10.0f, 0.1f)
        .m("灵敏度", "靈敏度")
    private val sensitivityDecimal by setting("Sensitivity Decimal", 0.000, 0.001..0.1, 0.001)
        .m("灵敏度小数位", "靈敏度小數位")
        .whenTrue(preciseSensitivity)
    private val dpiRate by setting("DPI Rate", 100, 10..500)
        .m("DPI 百分比", "DPI 百分比 ")
    private val VHSeparate = setting("VH Separate", false)
        .m("分离水平与垂直灵敏度", "分離水平垂直")
    private val verticalRate by setting("Vertical Rate", 1f, 0.1f..5f, 0.1f)
        .m("垂直比率", "垂直比率")
        .whenTrue(VHSeparate)
    private val horizontalRate by setting("Horizontal Rate", 1f, 0.1f..5f, 0.1f)
        .m("水平比率", "水平比率")
        .whenTrue(VHSeparate)

    // Movement
    val moveSpeed by setting("Move Speed", 0.3, 0.0..5.0, 0.05)
        .m("移动速度", "移動速度")
    val ySpeed by setting("Y Speed Rate", 1.0, 0.0..2.0, 0.01)
        .m("纵向速度倍率", "垂直速度倍率")
    private val physicUpdate by setting("Physics Thread", ThreadType.Dedicate)
        .m("物理线程", "物理線程")
    private val physicsTPS by setting("Physics TPS", 60, 1..300, 1)
        .m("物理TPS", "物理TPS")
    private val gravity by setting("Gravity", 9.8f, 0.1f..100f, 0.1f)
        .m("重力加速度", "重力加速度")

    private var lastState = physicUpdate
    fun checkPhysicsThread() {
        PhysicsSystem.setTPS(physicsTPS)
        if (lastState != physicUpdate) {
            lastState = physicUpdate
            when (lastState) {
                ThreadType.Dedicate -> {
                    if (PhysicsSystem.isOnThread(Thread.currentThread())) {
                        PhysicsSystem.changeThread(PhysicsSystem.PhysicsThread())
                    }
                }

                ThreadType.Main -> {
                    if (PhysicsSystem.isOnDedicate) {
                        PhysicsSystem.changeThread(Thread.currentThread())
                    }
                }
            }
        }
        if (physicUpdate == ThreadType.Main) PhysicsSystem.update()
    }

    var defaultTraining by setting("DefaultTraining", 0).at { false }
    val sensitivity get() = (if (preciseSensitivity.value) sensitivityRough + sensitivityDecimal else sensitivityRough.toDouble())
    val dpiModifyRate get() = 100.0 / dpiRate
    val vRate get() = if (VHSeparate.value) verticalRate else 1f
    val hRate get() = if (VHSeparate.value) horizontalRate else 1f

    enum class ThreadType(multiText: MultiText) : DisplayEnum {
        Dedicate("Dedicate".m("独立线程", "專用線程")),
        Main("Main".m("主线程", "主線程"));

        override val displayName by multiText
    }

}