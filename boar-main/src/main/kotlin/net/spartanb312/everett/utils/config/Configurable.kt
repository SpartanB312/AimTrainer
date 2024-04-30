package net.spartanb312.everett.utils.config

import com.google.gson.JsonObject
import net.spartanb312.everett.utils.config.setting.AbstractSetting
import net.spartanb312.everett.utils.config.setting.SettingRegister
import net.spartanb312.everett.utils.language.LanguageManager

open class Configurable(
    var name: String,
    private val languageManager: LanguageManager
) : SettingRegister<Configurable> {

    val settings = mutableListOf<AbstractSetting<*>>()

    fun saveValue(): JsonObject {
        return JsonObject().apply {
            settings.forEach { it.saveValue(this) }
        }
    }

    fun readValue(jsonObject: JsonObject) {
        settings.forEach { it.readValue(jsonObject) }
    }

    override fun <S : AbstractSetting<*>> Configurable.setting(setting: S): S {
        settings.add(setting)
        languageManager.add(setting.multiText)
        return setting
    }

}