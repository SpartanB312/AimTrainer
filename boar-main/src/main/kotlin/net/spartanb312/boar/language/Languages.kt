package net.spartanb312.boar.language

import net.spartanb312.boar.utils.misc.DisplayEnum

enum class Languages(override val displayName: CharSequence) : DisplayEnum {
    English("English(Global)"),
    ChineseCN("简体中文(中国)"),
    ChineseTW("繁體中文(台灣)"),
    Custom("Custom")
}