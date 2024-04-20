package net.spartanb312.everett.utils.collection

@Suppress("UNCHECKED_CAST")
class CircularArray<T>(val size: Int) {

    init {
        if (size < 1) throw IllegalArgumentException("Size must > 0")
    }

    private val array = Array<Any?>(size) { null }
    private var index = 0

    fun <T> add(param: T) {
        array[index] = param
        index++
        if (index >= size) index = 0
    }

    fun toList(): List<T> {
        var pointer = index
        val list = mutableListOf<T>()
        do {
            if (array[pointer] != null) {
                list.add(array[pointer] as T)
            } else break
            if (pointer == 0) pointer = size - 1
            else pointer--
        } while (pointer != index)

        return list
    }

    fun removeLast() {
        if (array[index] != null) {
            array[index] = null
            if (index == 0) index = size - 1
            else index--
        }
    }

}