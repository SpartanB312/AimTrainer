package net.spartanb312.boar.game.config

import com.google.gson.JsonObject
import net.spartanb312.boar.game.config.setting.AbstractSetting
import net.spartanb312.boar.game.config.setting.SettingRegister
import net.spartanb312.boar.language.Language

open class Configurable(var name: String) : SettingRegister<Configurable> {

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
        Language.add(setting.multiText)
        return setting
    }

}