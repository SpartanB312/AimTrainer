package net.spartanb312.everett.game.training.custom

import com.google.common.primitives.Ints.min
import net.spartanb312.everett.game.Language
import net.spartanb312.everett.game.render.gui.impls.CustomGameScreen
import net.spartanb312.everett.game.render.scene.Scene
import net.spartanb312.everett.game.training.Training
import net.spartanb312.everett.game.training.custom.impls.CustomDMR
import net.spartanb312.everett.game.training.custom.impls.CustomFollowing
import net.spartanb312.everett.game.training.custom.impls.CustomNormal
import net.spartanb312.everett.utils.config.Configurable
import net.spartanb312.everett.utils.config.setting.AbstractSetting
import net.spartanb312.everett.utils.config.setting.at
import net.spartanb312.everett.utils.config.setting.lang
import net.spartanb312.everett.utils.config.setting.whenTrue

object CustomTraining : AbstractCustomTraining("Custom Game", "Customized Training", "Custom Training") {

    // Type
    val type = setting("Training Type", Modes.Normal)
        .lang("基础模式", "基礎模式")
        .listen { CustomGameScreen.resetAnimation() }
    private val settingGroups = arrayOf(
        SettingGroup(Modes.Normal, "Custom-Normal"),
        SettingGroup(Modes.DMR, "Custom-DMR"),
        SettingGroup(Modes.Following, "Custom-Following")
    )

    class SettingGroup(private val mode: Modes, classifier: String) : Configurable(classifier, Language) {

        // Shared setting
        val amount by setting("Amount", 6, 0..50, 1)
            .lang("目标数量", "目標總數")
            .limit(Modes.Normal, Modes.DMR, Modes.Following)
        val size by setting("Size", 1f, 0.1f..5f, 0.1f)
            .lang("目标大小", "目標大小")
            .limit(Modes.Normal, Modes.DMR, Modes.Following)
        val gap by setting("Gap", 5f, 1f..20f, 0.5f)
            .lang("间隔距离", "間隔距離")
            .limit(Modes.Normal, Modes.DMR, Modes.Following)
        val width by setting("Width", 5, 2..20, 1)
            .lang("生成宽度", "生成寬度")
            .limit(Modes.Normal, Modes.DMR, Modes.Following)
        val height by setting("Height", 5, 2..20, 1)
            .lang("生成高度", "生成高度")
            .limit(Modes.Normal, Modes.DMR, Modes.Following)
        val offset = setting("Generate Offset", false)
            .lang("生成偏移", "生成偏移")
            .limit(Modes.Normal, Modes.DMR, Modes.Following)
        val xOffsetS by setting("Depth Offset", 0f, 0f..10f, 0.1f)
            .lang("生成深度偏移", "生成深度偏移")
            .limit(Modes.Normal, Modes.DMR, Modes.Following)
            .whenTrue(offset)
        val yOffsetS by setting("Vertical Offset", 0f, 0f..10f, 0.1f)
            .lang("生成垂直偏移", "生成垂直偏移")
            .limit(Modes.Normal, Modes.DMR, Modes.Following)
            .whenTrue(offset)
        val zOffsetS by setting("Horizontal Offset", 0f, 0f..10f, 0.1f)
            .lang("生成水平偏移", "生成水平偏移")
            .limit(Modes.Normal, Modes.DMR, Modes.Following)
            .whenTrue(offset)
        val defaultErrorAngle by setting("Default Error Angle", 1f, 0f..10f, 0.1f)
            .lang("默认吸附角", "默認吸附角")
            .limit(Modes.Normal, Modes.DMR, Modes.Following)
        val distance by setting("Distance", 50f, 10f..150f, 0.5f)
            .lang("生成距离", "生成距離")
            .limit(Modes.Normal, Modes.DMR, Modes.Following)
        val moveSpeed by setting("Move Speed", if (mode == Modes.DMR) 2f else 2.5f, 0f..5f, 0.1f)
            .lang("移动速度", "移動速度")
            .limit(Modes.DMR, Modes.Following)
        val killResetTime by setting("Continuous Kill Reset", if (mode == Modes.Normal) 300 else 2500, 0..10000, 50)
            .lang("连杀重置时间", "最大連殺間隔")
            .limit(Modes.Normal, Modes.DMR)
        val scoreBase by setting("Score Base", 1f, 0.1f..10f, 0.1f)
            .lang("得分倍率", "得分基準")
            .limit(Modes.Normal, Modes.DMR, Modes.Following)
        val punishmentBase by setting("Punishment Base", 1f, 0.1f..10f, 0.1f)
            .lang("惩罚倍率", "懲罰基準")
            .limit(Modes.Normal, Modes.Following)
        val minKillTime by setting("Min Kill Time", 50, 50..500, 10)
            .lang("最短击杀时间", "最短擊殺時間")
            .limit(Modes.Normal, Modes.DMR)
        val maxKillTime by setting("Max Kill Time", 2000, 1000..10000, 50)
            .lang("最长击杀时间", "最長擊殺時間")
            .limit(Modes.Normal, Modes.DMR)

        init {
            settings.forEach { it.at { type.value.settingGroup == this } }
        }

        val xOffset get() = if (offset.value) xOffsetS else 0f
        val yOffset get() = if (offset.value) yOffsetS else 0f
        val zOffset get() = if (offset.value) zOffsetS else 0f

        private fun <U, T : Enum<T>> AbstractSetting<U>.limit(vararg modes: Enum<T>) = this.apply {
            if (modes.any { it == mode }) visibilities.add { modes.any { it == type.value } }
            else this@SettingGroup.settings.remove(this)
        }

    }

    override var name = "Custom Game"

    override fun new(scene: Scene): Training {
        with(type.value.settingGroup) {
            val maxAmount = min(width * height / 2, amount).coerceAtLeast(0)
            return when (type.value) {
                Modes.Normal -> CustomNormal(
                    scene,
                    maxAmount,
                    size,
                    gap,
                    width,
                    height,
                    defaultErrorAngle,
                    distance,
                    xOffset,
                    yOffset,
                    zOffset,
                    killResetTime,
                    scoreBase,
                    punishmentBase,
                    minKillTime,
                    maxKillTime
                )

                Modes.DMR -> CustomDMR(
                    scene,
                    maxAmount,
                    size,
                    gap,
                    width,
                    height,
                    xOffset,
                    yOffset,
                    zOffset,
                    defaultErrorAngle,
                    distance,
                    killResetTime,
                    moveSpeed,
                    scoreBase,
                    minKillTime,
                    maxKillTime
                )

                Modes.Following -> CustomFollowing(
                    scene,
                    maxAmount,
                    size,
                    gap,
                    width,
                    height,
                    xOffset,
                    yOffset,
                    zOffset,
                    defaultErrorAngle,
                    distance,
                    moveSpeed,
                    scoreBase,
                    punishmentBase
                )

            }.reset()
        }
    }

    // Use index to avoid init null
    enum class Modes(private val settingGroupIndex: Int = 0) {
        Normal(0),
        DMR(1),
        Following(2);

        val settingGroup: SettingGroup get() = settingGroups[settingGroupIndex]
    }

}