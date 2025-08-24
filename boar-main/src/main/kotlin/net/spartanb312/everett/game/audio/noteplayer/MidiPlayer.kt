package net.spartanb312.everett.game.audio.noteplayer

import net.spartanb312.everett.audio.Sound
import net.spartanb312.everett.game.audio.notebox.Harp
import net.spartanb312.everett.game.audio.notebox.pianos
import net.spartanb312.everett.game.audio.noteplayer.Song.Command
import net.spartanb312.everett.game.event.TickEvent
import net.spartanb312.everett.game.option.impls.AudioOption
import net.spartanb312.everett.game.render.hud.PianoHUD
import net.spartanb312.everett.utils.Logger
import net.spartanb312.everett.utils.ResourceHelper
import net.spartanb312.everett.utils.event.ListenerOwner
import net.spartanb312.everett.utils.event.listener
import net.spartanb312.everett.utils.timing.Timer

object MidiPlayer : ListenerOwner() {

    private val tickTimer = Timer()
    private val sources = mutableListOf<Sound>()

    init {
        sources.addAll(pianos.flatMap { it.sounds })
        sources.addAll(Harp.sounds)
        listener<TickEvent.Audio> {
            tickTimer.tps(AudioOption.tps) {
                onTick()
            }
        }
        listener<TickEvent.Post> {
            sources.forEach {
                if (it.name != "dummy") it.setVolume(AudioOption.noteBox)
            }
        }
        subscribe()
    }

    fun readSong(path: String): Song {
        return MidiParser.parseMidi(path, ResourceHelper.getResourceStream(path)!!)
    }

    fun playSong(song: Song) {
        stop()
        this.song = song
        timer = -10
        for (i in 0..107) PianoHUD.release(i)
        PianoHUD.generateTilesForSong(song)
    }

    fun stop() {
        song = null
        timer = -10
        PianoHUD.stop()
        noteOffTime = Array(10) { IntArray(108) { 0 } }
        delayedCommand.clear()
    }

    private var song: Song? = null
    private var noteOffTime = Array(10) { IntArray(108) { 0 } }
    private val delayedCommand = mutableListOf<Triple<Int, Int, Int>>() // time, note, track
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
        song.tracks.forEachIndexed { trIndex, track ->
            val instrument = pianos[trIndex % 3]
            val current = track.notes[timer]
            if (current.isNotEmpty()) for (note in current) {
                if (note.track == trIndex && note.end > noteOffTime[trIndex][note.index]) {
                    noteOffTime[trIndex][note.index] = note.end
                    delayedCommand.add(Triple(note.end, note.index, note.track))
                }
            }
            track.commandQueue.forEach { (time, note, command) ->
                if (time == timer) {
                    if (command == Command.On) {
                        instrument.sounds[note].stop().play()
                        PianoHUD.press(note, Note.colors[trIndex % 11])
                    } else if (command == Command.Off && timer >= noteOffTime[trIndex][note]) {
                        instrument.sounds[note].stop()
                        PianoHUD.release(note)
                    }
                }
            }
        }
        delayedCommand.removeIf { (time, note, trIndex) ->
            val instrument = pianos[trIndex % 3]
            if (time == timer && timer >= noteOffTime[trIndex][note]) {
                instrument.sounds[note].stop()
                PianoHUD.release(note)
                true
            }
            false
        }
    }

}