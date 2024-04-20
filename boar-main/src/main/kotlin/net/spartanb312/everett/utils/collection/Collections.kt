@file:Suppress("NOTHING_TO_INLINE")

package net.spartanb312.everett.utils.collection

import java.util.*

inline fun <T> Array<T>.getOrPut(index: Int, block: () -> T): T {
    if (this.getOrNull(index) == null) {
        this[index] = block()
    }
    return this[index]
}

inline fun <T, reified U> Array<T>.map(block: (T) -> U): Array<U> = this.toList().map(block).toTypedArray()

inline fun <E : Any> MutableCollection<E>.add(e: E?) {
    if (e != null) this.add(e)
}

inline fun <reified T> Array<T>.removeFirst(counts: Int): Array<T> = if (this.size < counts) this
else if (this.size == counts) emptyArray()
else Array(size - counts) { index ->
    this[index + counts]
}

inline fun <reified T> Array<T>.removeLast(counts: Int): Array<T> = if (this.size <= counts) emptyArray()
else Array(size - counts) { index ->
    this[index]
}

inline fun <reified T> List<T>.removeFirstToArray(counts: Int): Array<T> = if (this.size < counts) this.toTypedArray()
else if (this.size == counts) emptyArray()
else Array(size - counts) { index ->
    this[index + counts]
}

inline fun <reified T> List<T>.removeLastToArray(counts: Int): Array<T> = if (this.size <= counts) emptyArray()
else Array(size - counts) { index ->
    this[index]
}

inline fun <T> List<T>.removeFirst(counts: Int): List<T> = if (this.size < counts) this
else if (this.size == counts) listOf()
else List(size - counts) { index ->
    this[index + counts]
}

inline fun <T> List<T>.removeLast(counts: Int): List<T> = if (this.size <= counts) listOf()
else List(size - counts) { index ->
    this[index]
}

inline fun <T> Iterable<T>.sumOfFloat(selector: (T) -> Float): Float {
    var sum = 0.0f
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

inline fun CharSequence.sumOfFloat(selector: (Char) -> Float): Float {
    var sum = 0.0f

    for (element in this) {
        sum += selector(element)
    }

    return sum
}

inline fun <E> MutableCollection<E>.synchronized(): MutableCollection<E> =
    Collections.synchronizedCollection(this)

inline fun <E> MutableList<E>.synchronized(): MutableList<E> =
    Collections.synchronizedList(this)

inline fun <E> MutableSet<E>.synchronized(): MutableSet<E> =
    Collections.synchronizedSet(this)

inline fun <E> SortedSet<E>.synchronized(): SortedSet<E> =
    Collections.synchronizedSortedSet(this)

inline fun <A, B, K, V> Set<Map.Entry<A, B>>.mapAll(transform: (Map.Entry<A, B>.() -> Pair<K, V>)): Map<K, V> =
    mutableMapOf<K, V>().also { map -> forEach { it.transform().apply { map[first] = second } } }

inline fun <A, B, K, V> Map<A, B>.mapAll(transform: (Map.Entry<A, B>.() -> Pair<K, V>)): Map<K, V> =
    mutableMapOf<K, V>().also { map -> forEach { it.transform().apply { map[first] = second } } }

inline fun List<String>.noPrefix(prefix: String): List<String> = this.filter { !it.startsWith(prefix) }

inline fun List<String>.prefix(prefix: String): List<String> = this.filter { it.startsWith(prefix) }

inline fun List<String>.containsValue(value: String): List<String> = this.filter { it.contains(value) }

inline fun List<String>.containsValues(vararg values: String): List<String> =
    this.filter { values.all { v -> it.contains(v) } }

inline fun List<String>.noValue(value: String): List<String> = this.filter { !it.contains(value) }

inline fun List<String>.noValues(vararg values: String): List<String> =
    this.filter { values.none { v -> it.contains(v) } }

inline fun List<String>.noPrefixes(vararg prefixes: String): List<String> =
    this.filter { prefixes.none { ex -> it.startsWith(ex) } }

inline fun <T, reified U> Array<T>.mapArray(transform: (T) -> U): Array<U> = Array(this.size) {
    transform(this[it])
}
