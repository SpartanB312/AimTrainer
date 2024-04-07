package net.spartanb312.boar.language

import net.spartanb312.boar.game.option.impls.AccessibilityOption
import net.spartanb312.boar.game.render.gui.impls.OptionScreen

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