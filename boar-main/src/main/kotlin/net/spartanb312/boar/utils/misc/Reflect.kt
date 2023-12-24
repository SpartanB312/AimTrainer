package net.spartanb312.boar.utils.misc

@Suppress("UNCHECKED_CAST")
inline val <T> Class<out T>.instance: T?
    get() = try {
        this.getDeclaredField("INSTANCE")[null] as T?
    } catch (ignore: Exception) {
        null
    }
