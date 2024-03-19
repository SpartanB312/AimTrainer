package net.spartanb312.boar.game.option.impls

import net.spartanb312.boar.game.config.setting.at
import net.spartanb312.boar.game.config.setting.whenTrue
import net.spartanb312.boar.game.option.Option

object ControlOption : Option("Control") {
    private val preciseSensitivity = setting("Precise Sensitivity", false)
    private val sensitivityRough by setting("Sensitivity", 2.2f, 0.1f..10.0f, 0.1f)
    private val sensitivityDecimal by setting("Sensitivity Decimal", 0.000, 0.001..0.1, 0.001).whenTrue(preciseSensitivity)
    private val VHSeparate = setting("VH Separate", false)
    private val verticalRate by setting("Vertical Rate", 1.0, 0.1..5.0, 0.1).whenTrue(VHSeparate)
    private val horizontalRate by setting("Horizontal Rate", 1.0, 0.1..5.0, 0.1).whenTrue(VHSeparate)

    var defaultTraining by setting("DefaultTraining",0).at { false }
    val sensitivity get() = if (preciseSensitivity.value) sensitivityRough + sensitivityDecimal else sensitivityRough.toDouble()
    val vRate get() = if (VHSeparate.value) verticalRate else 1f
    val hRate get() = if (VHSeparate.value) horizontalRate else 1f
}