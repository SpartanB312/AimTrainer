package net.spartanb312.everett.utils.config.setting.number

import com.google.gson.JsonObject
import java.text.DecimalFormat
import kotlin.math.roundToInt

class FloatSetting(
    name: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    step: Float,
    description: String = "",
    visibility: (() -> Boolean) = { true }
) : NumberSetting<Float>(name, value, range, step, description, visibility) {

    override var format = DecimalFormat("0.0")
    override val displayValue: String get() = format.format(value)

    override fun saveValue(jsonObject: JsonObject) = jsonObject.addProperty(nameString, value)
    override fun readValue(jsonObject: JsonObject) {
        value = (jsonObject[nameString]?.asFloat ?: value).coerceIn(range)
    }

    override val width = range.endInclusive - range.start

    override fun setByPercent(percent: Float) {
        value = (range.start + ((range.endInclusive - range.start) * percent / step).roundToInt() * step).coerceIn(range)
    }

    override fun getDisplay(percent: Float): String {
        return if (percent == 1f) format.format(value)
        else format.format(range.start + ((range.endInclusive - range.start) * percent / step).roundToInt() * step)
    }

    override fun getPercentBar(): Float {
        return ((value - range.start) / (range.endInclusive - range.start))
    }

    override val defaultPercentBar: Float = (defaultValue - range.start) / (range.endInclusive - range.start)

}