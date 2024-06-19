package net.spartanb312.everett.game.training.custom

import net.spartanb312.everett.game.Language
import net.spartanb312.everett.game.training.TrainingInfo
import net.spartanb312.everett.utils.config.IConfigurable
import net.spartanb312.everett.utils.config.setting.AbstractSetting
import net.spartanb312.everett.utils.language.LanguageManager

abstract class AbstractCustomTraining(
    trainingName: String,
    description: String,
    override val category: String,
    private val languageManager: LanguageManager = Language
) : TrainingInfo(trainingName, description), IConfigurable<AbstractCustomTraining> {

    override val settings = mutableListOf<AbstractSetting<*>>()
    override fun <S : AbstractSetting<*>> AbstractCustomTraining.setting(setting: S): S {
        settings.add(setting)
        languageManager.add(setting.multiText)
        return setting
    }

}