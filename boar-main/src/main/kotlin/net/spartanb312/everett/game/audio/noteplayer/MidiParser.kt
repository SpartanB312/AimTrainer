package net.spartanb312.everett.game.audio.noteplayer

import com.google.common.collect.Multimap
import com.google.common.collect.MultimapBuilder
import net.spartanb312.everett.utils.math.ceilToInt
import net.spartanb312.everett.utils.math.floorToInt
import java.io.InputStream
import javax.sound.midi.MetaMessage
import javax.sound.midi.MidiSystem
import javax.sound.midi.ShortMessage

object MidiParser {

    fun parseMidi(path: String, inputStream: InputStream): Song {
        val notes: Multimap<Int, Note> = MultimapBuilder.linkedHashKeys().arrayListValues().build()
        val noteOff: Multimap<Int, Note> = MultimapBuilder.linkedHashKeys().arrayListValues().build()
        var bpm = 120
        try {
            val seq = MidiSystem.getSequence(inputStream)
            val res = seq.resolution
            for ((trackCount, track) in seq.tracks.withIndex()) {
                var time: Long
                for (i in 0 until track.size()) {
                    val event = track[i]
                    val message = event.message
                    if (message is MetaMessage) {
                        val data = message.getData()
                        if (message.getType() == 0x51) {
                            val tempo = ((data[0].toInt() and 0xFF) shl 16) or
                                    ((data[1].toInt() and 0xFF) shl 8) or
                                    (data[2].toInt() and 0xFF)
                            bpm = 60_000_000 / tempo
                        }
                    }
                    val ticksPerSecond = (res * (bpm / 60.0)).toInt()
                    time = ((1000.0 / ticksPerSecond) * event.tick).toLong()
                    if (message is ShortMessage && (message.command == ShortMessage.NOTE_ON || message.command == ShortMessage.NOTE_OFF)) {
                        val key = message.getData1()
                        val octave = (key / 12) - 1
                        val note = key % 12
                        val velocity = message.getData2()
                        if (message.command == ShortMessage.NOTE_ON) notes.put(
                            (time / 10.0).floorToInt(),
                            Note(trackCount, octave, note, velocity, 0)
                        )
                        else if (message.command == ShortMessage.NOTE_OFF) noteOff.put(
                            (time / 10.0).ceilToInt() + 1,
                            Note(trackCount, octave, note, velocity, 0)
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Song(path, notes, noteOff)
    }

}