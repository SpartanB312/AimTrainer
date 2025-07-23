package net.spartanb312.everett.game.audio.noteplayer

import com.google.common.collect.Multimap

class Song(
    var filename: String,
    var notes: Multimap<Int, Note>
) {
    var requirements: MutableSet<Note> = HashSet()
    var length: Int

    init {
        notes.values().stream().distinct().forEach { e: Note -> requirements.add(e) }
        length = notes.keySet().stream().max(Comparator.naturalOrder()).orElse(0)
    }
}