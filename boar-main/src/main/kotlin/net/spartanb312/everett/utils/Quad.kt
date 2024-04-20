package net.spartanb312.everett.utils

data class Quad<T, U, V, W>(val first: T, val second: U, val third: V, val forth: W) {

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Quad<*, *, *, *>

        if (first != other.first) return false
        if (second != other.second) return false
        if (third != other.third) return false
        return forth == other.forth
    }

}