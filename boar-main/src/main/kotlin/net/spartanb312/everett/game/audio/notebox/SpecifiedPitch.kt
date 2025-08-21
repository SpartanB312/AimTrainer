package net.spartanb312.everett.game.audio.notebox

import net.spartanb312.everett.audio.Sound

open class SpecifiedPitch(
    override val name: String,
    pitchBuilder: (Int) -> Sound
) : NotePlayer {

    override val sounds = mutableListOf<Sound>()

    init {
        for (i in 1..108) {
            sounds.add(pitchBuilder.invoke(i))
        }
    }

}