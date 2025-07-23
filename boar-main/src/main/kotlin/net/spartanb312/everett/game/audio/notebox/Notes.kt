package net.spartanb312.everett.game.audio.notebox

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap

object Notes {

    private val map = Object2IntOpenHashMap<String>()
    private val revMap = Int2ObjectOpenHashMap<String>()

    init {
        val notes = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
        var index = 0
        for (i in 1..6) {
            notes.forEach { note ->
                map.put("${note}$i", index)
                revMap.put(index, "${note}$i")
                index++
            }
        }
    }

    fun getPitchIndex(name: String) = map.getInt(name)
    fun getIndexedPitch(index: Int) = revMap.get(index)

}