package net.spartanb312.boar.utils.misc

fun String.limitLength(length: Int): String = if (this.length > length) this.substring(0, length) else this