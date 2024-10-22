package net.spartanb312.everett.utils.config.setting.primitive

import com.google.gson.JsonObject
import net.spartanb312.everett.utils.config.setting.MutableSetting

open class BooleanSetting(
    name: String,
    value: Boolean,
    description: String = "",
    visibility: (() -> Boolean) = { true }
) : MutableSetting<Boolean>(name, value, description, visibility) {
    override val displayValue: String get() = value.toString()
    override fun saveValue(jsonObject: JsonObject) = jsonObject.addProperty(nameString, value)
    override fun readValue(jsonObject: JsonObject) {
        value = jsonObject[nameString]?.asBoolean ?: value
    }
}