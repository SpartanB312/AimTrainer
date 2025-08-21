package net.spartanb312.everett.game.audio.notebox

import net.spartanb312.everett.audio.Sound

object Harp : GeneratedPitch("harp", "assets/sound/harp.wav", "C4", 1f)

object Piano : SpecifiedPitch("piano1", { index ->
    if (index < 10 || index > 97) Sound() // empty sound
    else Sound("assets/sound/piano/${index + 11}.wav")
})

object Piano2 : SpecifiedPitch("piano2", { index ->
    if (index < 10 || index > 97) Sound() // empty sound
    else Sound("assets/sound/piano/${index + 11}.wav")
})

object Piano3 : SpecifiedPitch("piano3", { index ->
    if (index < 10 || index > 97) Sound() // empty sound
    else Sound("assets/sound/piano/${index + 11}.wav")
})

val pianos = listOf(Piano, Piano2, Piano3)