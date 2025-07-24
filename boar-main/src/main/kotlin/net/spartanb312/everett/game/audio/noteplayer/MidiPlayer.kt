package net.spartanb312.everett.game.audio.noteplayer

import net.spartanb312.everett.game.audio.notebox.Harp
import net.spartanb312.everett.game.audio.notebox.Piano
import net.spartanb312.everett.game.render.hud.PianoHUD
import net.spartanb312.everett.graphics.event.EngineLoopEvent
import net.spartanb312.everett.utils.Logger
import net.spartanb312.everett.utils.ResourceHelper
import net.spartanb312.everett.utils.color.ColorRGB
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
        for (i in 0..107) PianoHUD.release(i)
        PianoHUD.generateTilesForSong(song)
    }

    fun stop() {
        song = null
        timer = -10
        PianoHUD.stop()
    }

    private var song: Song? = null
    var timer = -10

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
        val curOff = song.noteOff[timer]
        if (curNotes.isNotEmpty()) for (note in curNotes) {
            val index = note.octave * 12 + note.note
            Harp.sounds[index].stop().play()
            PianoHUD.press(index, note.color)
        }
        if (curOff.isNotEmpty()) for (note in curOff) {
            val index = note.octave * 12 + note.note
            //Harp.sounds[index].stop()
            PianoHUD.release(index)
        }
    }

}