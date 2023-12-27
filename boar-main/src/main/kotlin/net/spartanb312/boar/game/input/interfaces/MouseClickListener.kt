package net.spartanb312.boar.game.input.interfaces

interface MouseClickListener {
    fun onMouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean = false
}