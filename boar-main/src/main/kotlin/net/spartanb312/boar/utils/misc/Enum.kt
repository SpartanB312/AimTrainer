package net.spartanb312.boar.utils.misc

fun <E : Enum<E>> E.next(): E = declaringJavaClass.enumConstants.run {
    get((ordinal + 1) % size)
}

fun <E : Enum<E>> E.last(): E = declaringJavaClass.enumConstants.run {
    get(if (ordinal == 0) size - 1 else ordinal - 1)
}