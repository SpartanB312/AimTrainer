package net.spartanb312.everett.game.audio.noteplayer

import net.spartanb312.everett.utils.color.ColorRGB

data class Note(
    val track: Int,
    val octave: Int,
    val note: Int,
    val velocity: Int,
    val instrument: Int
) {
    // Tick
    var start = -11
    var end = -11
    val index = octave * 12 + note

    val color = colors[track % 11]

    companion object {
        private val colors = arrayOf(
            ColorRGB.DARK_AQUA,
            ColorRGB.RED,
            ColorRGB.YELLOW,
            ColorRGB.GREEN,
            ColorRGB.AQUA,
            ColorRGB.LIGHT_PURPLE,
            ColorRGB.DARK_RED,
            ColorRGB.GOLD,
            ColorRGB.DARK_GREEN,
            ColorRGB.BLUE,
            ColorRGB.DARK_BLUE,
            ColorRGB.DARK_PURPLE,
        )
    }

}