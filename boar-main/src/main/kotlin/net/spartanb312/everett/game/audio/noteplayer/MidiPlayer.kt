package net.spartanb312.everett.game.audio.noteplayer

import net.spartanb312.everett.game.audio.notebox.Harp
import net.spartanb312.everett.graphics.event.EngineLoopEvent
import net.spartanb312.everett.utils.Logger
import net.spartanb312.everett.utils.ResourceHelper
import net.spartanb312.everett.utils.event.ListenerOwner
import net.spartanb312.everett.utils.event.listener
import net.spartanb312.everett.utils.timing.Timer

object MidiPlayer : ListenerOwner() {

    private val tickTimer = Timer()

    init {
        listener<EngineLoopEvent.Loop.Pre> {
            tickTimer.tps(100) {
                onTick()
            }
        }
        subscribe()
    }

    fun readSong(path: String): Song {
        return MidiParser.parseMidi(path, ResourceHelper.getResourceStream(path)!!)
    }

    fun playSong(song: Song) {
        this.song = song
        timer = -10
    }

    fun stop() {
        song = null
        timer = -10
    }

    private var song: Song? = null
    private var timer = -10

    private fun onTick() {
        // Loop
        val song = song ?: return
        if (timer - 10 > song.length) {
            Logger.info("Finished playing ${song.filename}")
            stop()
            return
        }
        if (timer == -10) Logger.info("Now playing: ${song.filename}")
        timer++
        val curNotes = song.notes[timer]
        if (curNotes.isEmpty()) return
        for (note in curNotes) {
            Harp.sounds[(note.octave - 1) * 12 + note.note].stop().play()
        }
    }

}