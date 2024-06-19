package net.spartanb312.everett.utils.config.setting

import com.google.gson.JsonArray
import com.google.gson.JsonObject

class ListSetting(
    name: String,
    value: List<String>,
    description: String = "",
    visibility: (() -> Boolean) = { true }
) : MutableSetting<List<String>>(name, value, description, visibility) {
    override val displayValue: String get() = value.toString()

    override fun saveValue(jsonObject: JsonObject) = jsonObject.add(nameString, JsonArray().apply {
        value.forEach { add(it) }
    })

    override fun readValue(jsonObject: JsonObject) {
        value = jsonObject[nameString]?.asJsonArray?.map { it.asString } ?: value
    }

}