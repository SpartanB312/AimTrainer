package net.spartanb312.boar.game.config.setting.number

import com.google.gson.JsonObject
import kotlin.math.roundToInt

class DoubleSetting(
    name: String,
    value: Double,
    range: ClosedFloatingPointRange<Double>,
    step: Double,
    description: String = "",
    visibility: (() -> Boolean) = { true }
) : NumberSetting<Double>(name, value, range, step, description, visibility) {

    override fun saveValue(jsonObject: JsonObject) = jsonObject.addProperty(nameString, value)
    override fun readValue(jsonObject: JsonObject) {
        value = (jsonObject[nameString]?.asDouble ?: value).coerceIn(range)
    }

    override val width = range.endInclusive - range.start

    override fun setByPercent(percent: Float) {
        value = (range.start + ((range.endInclusive - range.start) * percent / step).roundToInt() * step).coerceIn(range)
    }

    override fun getDisplay(percent: Float): String {
        return if (percent == 1f) String.format("%.3f", value)
        else String.format("%.3f", range.start + ((range.endInclusive - range.start) * percent / step).roundToInt() * step)
    }

    override fun getPercentBar(): Float {
        return ((value - range.start) / (range.endInclusive - range.start)).toFloat()
    }

}