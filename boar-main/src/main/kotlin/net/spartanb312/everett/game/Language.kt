package net.spartanb312.everett.game

import net.spartanb312.everett.game.option.impls.AccessibilityOption
import net.spartanb312.everett.game.render.gui.impls.OptionScreen
import net.spartanb312.everett.utils.language.LanguageManager
import net.spartanb312.everett.utils.language.Languages
import net.spartanb312.everett.utils.language.MultiText

object Language : LanguageManager {

    private val textContainers = mutableListOf<MultiText>()
    private var currentLang = Languages.English

    override fun add(multiText: MultiText): Boolean = textContainers.add(multiText)

    override fun String.lang(cn: String, tw: String): MultiText {
        val multiText = MultiText(this)
        multiText.addLang(Languages.ChineseCN, cn)
        multiText.addLang(Languages.ChineseTW, tw)
        //multiText.addLang()
        textContainers.add(multiText)
        multiText.update(currentLang)
        return multiText
    }

    override fun update(force: Boolean) {
        if (currentLang != AccessibilityOption.language || force) {
            currentLang = AccessibilityOption.language
            textContainers.forEach { it.update(currentLang) }
            OptionScreen.resetAnimation()
        }
    }

}