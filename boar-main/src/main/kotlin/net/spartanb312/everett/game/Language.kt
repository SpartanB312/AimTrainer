package net.spartanb312.everett.game

import net.spartanb312.everett.game.option.impls.AccessibilityOption
import net.spartanb312.everett.game.render.gui.impls.OptionScreen
import net.spartanb312.everett.utils.language.Languages
import net.spartanb312.everett.utils.language.MultiText

object Language {

    private val textContainers = mutableListOf<MultiText>()
    private var currentLang = Languages.English

    fun add(multiText: MultiText) = textContainers.add(multiText)

    fun String.m(cn: String = this, tw: String = this): MultiText {
        val multiText = MultiText(this)
        multiText.addLang(Languages.ChineseCN, cn)
        multiText.addLang(Languages.ChineseTW, tw)
        //multiText.addLang()
        textContainers.add(multiText)
        return multiText
    }

    fun update(force: Boolean = false) {
        if (currentLang != AccessibilityOption.language || force) {
            currentLang = AccessibilityOption.language
            textContainers.forEach { it.update(currentLang) }
            OptionScreen.resetAnimation()
        }
    }

}