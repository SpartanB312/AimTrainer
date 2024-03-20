package net.spartanb312.boar.game.option.impls

import net.spartanb312.boar.game.config.setting.at
import net.spartanb312.boar.game.config.setting.m
import net.spartanb312.boar.game.config.setting.whenTrue
import net.spartanb312.boar.game.option.Option

object ControlOption : Option("Control") {

    private val preciseSensitivity = setting("Precise Sensitivity", false)
        .m("精准灵敏度", "精確靈敏度")
    private val sensitivityRough by setting("Sensitivity", 2.2f, 0.1f..10.0f, 0.1f)
        .m("灵敏度", "靈敏度")
    private val sensitivityDecimal by setting("Sensitivity Decimal", 0.000, 0.001..0.1, 0.001).whenTrue(preciseSensitivity)
        .m("灵敏度小数位", "靈敏度小數位")
    private val VHSeparate = setting("VH Separate", false)
        .m("分离水平与垂直灵敏度", "分離水平垂直")
    private val verticalRate by setting("Vertical Rate", 1.0, 0.1..5.0, 0.1).whenTrue(VHSeparate)
        .m("垂直比率", "垂直比率")
    private val horizontalRate by setting("Horizontal Rate", 1.0, 0.1..5.0, 0.1).whenTrue(VHSeparate)
        .m("水平比率", "水平比率")

    var defaultTraining by setting("DefaultTraining", 0).at { false }
    val sensitivity get() = if (preciseSensitivity.value) sensitivityRough + sensitivityDecimal else sensitivityRough.toDouble()
    val vRate get() = if (VHSeparate.value) verticalRate else 1f
    val hRate get() = if (VHSeparate.value) horizontalRate else 1f

}