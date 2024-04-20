package net.spartanb312.everett.utils.misc

fun String.limitLength(length: Int): String = if (this.length > length) this.substring(0, length) else this