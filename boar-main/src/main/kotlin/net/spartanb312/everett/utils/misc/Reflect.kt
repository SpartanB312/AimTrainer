package net.spartanb312.everett.utils.misc

@Suppress("UNCHECKED_CAST")
inline val <T> Class<out T>.instance: T?
    get() = try {
        this.getDeclaredField("INSTANCE")[null] as T?
    } catch (ignore: Exception) {
        null
    }
