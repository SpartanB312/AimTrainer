package net.spartanb312.everett.game.training.custom

import net.spartanb312.everett.game.Language
import net.spartanb312.everett.game.training.TrainingInfo
import net.spartanb312.everett.utils.config.setting.AbstractSetting
import net.spartanb312.everett.utils.config.setting.SettingRegister
import net.spartanb312.everett.utils.language.LanguageManager

abstract class CustomTraining(
    trainingName: String,
    description: String,
    private val languageManager: LanguageManager = Language
) : TrainingInfo(trainingName, description), SettingRegister<CustomTraining> {

    val settings = mutableListOf<AbstractSetting<*>>()
    override fun <S : AbstractSetting<*>> CustomTraining.setting(setting: S): S {
        settings.add(setting)
        languageManager.add(setting.multiText)
        return setting
    }

}