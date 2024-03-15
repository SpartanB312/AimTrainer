package net.spartanb312.boar.utils.misc

interface DisplayEnum {
    val displayName: CharSequence
    val displayString: String
        get() = displayName.toString()

    val lowerCase: String
        get() = displayName.toString().lowercase()
}