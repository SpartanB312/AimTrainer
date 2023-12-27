package net.spartanb312.boar.game.input.interfaces

interface KeyTypedListener {
    fun onKeyTyped(keyCode: Int, modifier: Int): Boolean
}