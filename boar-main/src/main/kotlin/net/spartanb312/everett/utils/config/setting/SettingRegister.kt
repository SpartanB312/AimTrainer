package net.spartanb312.everett.utils.config.setting

import net.spartanb312.everett.utils.config.setting.number.DoubleSetting
import net.spartanb312.everett.utils.config.setting.number.FloatSetting
import net.spartanb312.everett.utils.config.setting.number.IntegerSetting
import net.spartanb312.everett.utils.config.setting.number.LongSetting
import net.spartanb312.everett.utils.config.setting.primitive.BooleanSetting
import net.spartanb312.everett.utils.config.setting.primitive.EnumSetting
import net.spartanb312.everett.utils.config.setting.primitive.StringSetting
import net.spartanb312.everett.utils.language.Languages

interface SettingRegister<T> {

    fun T.setting(
        name: String,
        value: Double,
        range: ClosedFloatingPointRange<Double> = Double.MIN_VALUE..Double.MAX_VALUE,
        step: Double = 0.1,
        description: String = "",
        visibility: () -> Boolean = { true },
    ) = setting(DoubleSetting(name, value, range, step, description, visibility))

    fun T.setting(
        name: String,
        value: Float,
        range: ClosedFloatingPointRange<Float> = Float.MIN_VALUE..Float.MAX_VALUE,
        step: Float = 0.1F,
        description: String = "",
        visibility: () -> Boolean = { true },
    ) = setting(FloatSetting(name, value, range, step, description, visibility))

    fun T.setting(
        name: String,
        value: Int,
        range: IntRange = Int.MIN_VALUE..Int.MAX_VALUE,
        step: Int = 1,
        description: String = "",
        visibility: () -> Boolean = { true },
    ) = setting(IntegerSetting(name, value, range, step, description, visibility))

    fun T.setting(
        name: String,
        value: Long,
        range: LongRange = Long.MIN_VALUE..Long.MAX_VALUE,
        step: Long = 1L,
        description: String = "",
        visibility: () -> Boolean = { true },
    ) = setting(LongSetting(name, value, range, step, description, visibility))

    fun T.setting(
        name: String,
        value: Boolean,
        description: String = "",
        visibility: () -> Boolean = { true },
    ) = setting(BooleanSetting(name, value, description, visibility))

    fun <E : Enum<E>> T.setting(
        name: String,
        value: E,
        description: String = "",
        visibility: () -> Boolean = { true },
    ) = setting(EnumSetting(name, value, description, visibility))

    fun T.setting(
        name: String,
        value: String,
        description: String = "",
        visibility: () -> Boolean = { true },
    ) = setting(StringSetting(name, value, description, visibility))

    fun T.setting(
        name: String,
        value: List<String>,
        description: String = "",
        visibility: () -> Boolean = { true },
    ) = setting(ListSetting(name, value, description, visibility))

    fun <S : AbstractSetting<*>> T.setting(setting: S): S

}

fun <T> AbstractSetting<T>.invisible(): AbstractSetting<T> = this.apply {
    visibilities.clear()
    visibilities.add { false }
}

infix fun <T> AbstractSetting<T>.des(description: String): AbstractSetting<T> = this.apply {
    this.description = description
}

inline infix fun <T> AbstractSetting<T>.des(description: () -> String): AbstractSetting<T> = this.apply {
    this.description = description()
}

inline infix fun <T> AbstractSetting<T>.at(crossinline block: (T) -> Boolean): AbstractSetting<T> = this.apply {
    visibilities.add { block(this.value) }
}

fun <T> AbstractSetting<T>.atValue(value: T): AbstractSetting<T> = this.apply {
    visibilities.add { this.value == value }
}

fun <T> AbstractSetting<T>.whenTrue(setting: AbstractSetting<Boolean>): AbstractSetting<T> = this.apply {
    visibilities.add { setting.value }
}

fun <T> AbstractSetting<T>.whenFalse(setting: AbstractSetting<Boolean>): AbstractSetting<T> = this.apply {
    visibilities.add { !setting.value }
}

fun <U, T : Enum<T>> AbstractSetting<U>.atMode(setting: AbstractSetting<T>, value: Enum<T>) = this.apply {
    visibilities.add { setting.value == value }
}

fun <T : Comparable<T>> AbstractSetting<T>.inRange(range: ClosedRange<T>) = this.apply {
    visibilities.add { this.value in range }
}

fun <T> AbstractSetting<T>.alias(aliasName: String): AbstractSetting<T> = this.apply {
    this.aliasName = aliasName
    multiText.addLang(Languages.English, aliasName)
}

fun <T> AbstractSetting<T>.m(
    cn: String = aliasName,
    tw: String = aliasName
): AbstractSetting<T> = this.apply {
    multiText.addLang(Languages.ChineseCN, cn)
    multiText.addLang(Languages.ChineseTW, tw)
}