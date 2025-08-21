package net.spartanb312.everett.game.audio.noteplayer

import com.google.common.collect.Multimap
import kotlin.math.max

class Song(
    val filename: String,
    val tracks: List<Track>,
) {

    val requirements: MutableSet<Note> = HashSet()
    var length: Int

    init {
        length = 0
        tracks.forEach {
            it.notes.values().stream().distinct().forEach { e: Note -> requirements.add(e) }
            val maxLength = max(
                it.notes.keySet().stream().max(Comparator.naturalOrder()).orElse(0),
                it.noteOff.keySet().stream().max(Comparator.naturalOrder()).orElse(0)
            )
            length = max(maxLength, length)
        }
    }

    class Track(
        val notes: Multimap<Int, Note>,
        val noteOff: Multimap<Int, Note>,
        val commandQueue: MutableList<Triple<Int, Int, Command>> // time, note, command
    )

    enum class Command {
        On,
        Off
    }

}