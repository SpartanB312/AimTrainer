package net.spartanb312.boar.game.config.setting.primitive

import com.google.gson.JsonObject
import net.spartanb312.boar.game.config.setting.MutableSetting

open class BooleanSetting(
    name: String,
    value: Boolean,
    description: String = "",
    visibility: (() -> Boolean) = { true }
) : MutableSetting<Boolean>(name, value, description, visibility) {
    override fun saveValue(jsonObject: JsonObject) = jsonObject.addProperty(nameString, value)
    override fun readValue(jsonObject: JsonObject) {
        value = jsonObject[nameString]?.asBoolean ?: value
    }
}