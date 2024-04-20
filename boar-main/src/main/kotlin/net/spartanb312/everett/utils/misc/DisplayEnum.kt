package net.spartanb312.everett.utils.misc

interface DisplayEnum {
    val displayName: CharSequence
    val displayString: String
        get() = displayName.toString()

    val lowerCase: String
        get() = displayName.toString().lowercase()
}