package net.spartanb312.everett.game.input.interfaces

interface KeyTypedListener {
    fun onKeyTyped(keyCode: Int, modifier: Int): Boolean
}