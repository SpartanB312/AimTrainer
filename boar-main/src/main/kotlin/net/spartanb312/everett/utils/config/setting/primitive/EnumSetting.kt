package net.spartanb312.everett.utils.config.setting.primitive

import com.google.gson.JsonObject
import net.spartanb312.everett.utils.config.setting.MutableSetting
import net.spartanb312.everett.utils.misc.last
import net.spartanb312.everett.utils.misc.next

class EnumSetting<T : Enum<T>>(
    name: String,
    value: T,
    description: String = "",
    visibility: (() -> Boolean) = { true }
) : MutableSetting<T>(name, value, description, visibility) {

    private val enumClass: Class<T> = value.declaringJavaClass
    private val enumValues: Array<out T> = enumClass.enumConstants

    override fun saveValue(jsonObject: JsonObject) = jsonObject.addProperty(nameString, value.name)
    override fun readValue(jsonObject: JsonObject) {
        jsonObject[nameString]?.asString?.let { setWithName(it) }
    }

    fun nextValue() {
        value = value.next()
    }

    fun lastValue() {
        value = value.last()
    }

    fun currentName(): String {
        return value.name
    }

    fun setWithName(nameIn: String) {
        enumValues.firstOrNull { it.name.equals(nameIn, true) }?.let {
            value = it
        }
    }

}