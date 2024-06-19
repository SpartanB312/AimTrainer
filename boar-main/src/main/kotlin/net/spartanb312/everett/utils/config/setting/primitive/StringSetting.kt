package net.spartanb312.everett.utils.config.setting.primitive

import com.google.gson.JsonObject
import net.spartanb312.everett.utils.config.setting.MutableSetting

class StringSetting(
    name: String,
    value: String,
    description: String = "",
    visibility: (() -> Boolean) = { true }
) : MutableSetting<String>(name, value, description, visibility) {
    override val displayValue get(): String = value
    override fun saveValue(jsonObject: JsonObject) = jsonObject.addProperty(nameString, value)
    override fun readValue(jsonObject: JsonObject) {
        value = jsonObject[nameString]?.asString ?: value
    }
}