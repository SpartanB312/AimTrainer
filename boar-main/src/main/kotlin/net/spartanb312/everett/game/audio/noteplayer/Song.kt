package net.spartanb312.everett.game.audio.noteplayer

import com.google.common.collect.Multimap
import kotlin.math.max

class Song(
    val filename: String,
    val notes: Multimap<Int, Note>,
    val noteOff: Multimap<Int, Note>
) {
    val requirements: MutableSet<Note> = HashSet()
    var length: Int

    init {
        notes.values().stream().distinct().forEach { e: Note -> requirements.add(e) }
        length = max(
            notes.keySet().stream().max(Comparator.naturalOrder()).orElse(0),
            noteOff.keySet().stream().max(Comparator.naturalOrder()).orElse(0)
        )
    }
}