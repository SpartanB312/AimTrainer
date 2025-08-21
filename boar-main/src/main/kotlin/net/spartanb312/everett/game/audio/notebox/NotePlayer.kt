package net.spartanb312.everett.game.audio.notebox

import net.spartanb312.everett.audio.Sound

interface NotePlayer {
    val name: String
    val sounds: List<Sound>

    fun setVolume(volume: Float, force: Boolean = false) = sounds.forEach { it.setVolume(volume, force) }
}