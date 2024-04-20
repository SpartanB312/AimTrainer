package net.spartanb312.everett.language

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class MultiText(private val english: String) : ReadOnlyProperty<Any?, String> {

    private val map = mutableMapOf(Languages.English to english)
    private var currentLang = Languages.English
    private var displaying = map[currentLang] ?: english

    fun addLang(language: Languages, text: String) {
        map[language] = text
        if (currentLang == language) displaying = text
    }

    fun update(language: Languages) {
        if (currentLang != language) {
            displaying = map[language] ?: english
            currentLang = language
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return displaying
    }

    fun print() {
        map.forEach { println(it.value) }
        println()
    }

}