package net.spartanb312.everett.utils.config.setting.number

import net.spartanb312.everett.utils.config.setting.MutableSetting
import java.text.DecimalFormat

abstract class NumberSetting<T>(
    name: String,
    value: T,
    val range: ClosedRange<T>,
    val step: T,
    description: String = "",
    visibility: (() -> Boolean) = { true }
) : MutableSetting<T>(name, value, description, visibility)
        where T : Number, T : Comparable<T> {

    abstract val width: T
    open var format = DecimalFormat("0")

    abstract fun getDisplay(percent: Float = 1.0f): String
    abstract fun getPercentBar(): Float
    abstract fun setByPercent(percent: Float)

    abstract val defaultPercentBar: Float

    fun getMin(): Double {
        return range.start.toDouble()
    }

    fun getMax(): Double {
        return range.endInclusive.toDouble()
    }

}

fun <T> NumberSetting<T>.format(format: String): NumberSetting<T> where T : Number, T : Comparable<T> {
    this.format = DecimalFormat(format)
    return this
}