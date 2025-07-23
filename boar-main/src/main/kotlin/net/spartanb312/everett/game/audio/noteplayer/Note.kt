package net.spartanb312.everett.game.audio.noteplayer

data class Note(
    val track: Int,
    val octave: Int,
    val note: Int,
    val velocity: Int,
    val instrument: Int
)