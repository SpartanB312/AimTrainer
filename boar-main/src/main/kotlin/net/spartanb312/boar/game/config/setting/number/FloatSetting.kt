package net.spartanb312.boar.game.config.setting.number

import com.google.gson.JsonObject
import kotlin.math.roundToInt

class FloatSetting(
    name: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    step: Float,
    description: String = "",
    visibility: (() -> Boolean) = { true }
) : NumberSetting<Float>(name, value, range, step, description, visibility) {

    override fun saveValue(jsonObject: JsonObject) = jsonObject.addProperty(nameString, value)
    override fun readValue(jsonObject: JsonObject) {
        value = jsonObject[nameString]?.asFloat ?: value
    }

    override val width = range.endInclusive - range.start

    override fun setByPercent(percent: Float) {
        value = range.start + ((range.endInclusive - range.start) * percent / step).roundToInt() * step
    }

    override fun getDisplay(percent: Float): String {
        return if (percent == 1f) String.format("%.1f", value)
        else String.format("%.1f", range.start + ((range.endInclusive - range.start) * percent / step).roundToInt() * step)
    }

    override fun getPercentBar(): Float {
        return ((value - range.start) / (range.endInclusive - range.start))
    }

}