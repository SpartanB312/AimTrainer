package net.spartanb312.boar.game.config.setting.number

import com.google.gson.JsonObject

class LongSetting(
    name: String,
    value: Long,
    range: LongRange,
    step: Long,
    description: String = "",
    visibility: (() -> Boolean) = { true }
) : NumberSetting<Long>(name, value, range, step, description, visibility) {

    override fun saveValue(jsonObject: JsonObject) = jsonObject.addProperty(nameString, value)
    override fun readValue(jsonObject: JsonObject) {
        value = (jsonObject[nameString]?.asLong ?: value).coerceIn(range)
    }

    override val width = range.last - range.first

    override fun setByPercent(percent: Float) {
        value = (range.start + ((range.endInclusive - range.start) * percent / step).toInt() * step).coerceIn(range)
    }

    override fun getDisplay(percent: Float): String {
        return if (percent == 1f) value.toString()
        else (range.start + ((range.endInclusive - range.start) * percent / step).toInt() * step).toString()
    }

    override fun getPercentBar(): Float {
        return ((value - range.start) / (range.endInclusive - range.start).toDouble()).toFloat()
    }

}