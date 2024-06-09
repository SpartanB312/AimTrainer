package net.spartanb312.everett.utils.language

interface LanguageManager {
    fun add(multiText: MultiText): Boolean
    fun String.lang(cn: String = this, tw: String = this): MultiText
    fun update(force: Boolean = false)
}