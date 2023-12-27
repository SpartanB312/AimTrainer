package net.spartanb312.boar.graphics

interface GameGraphics {
    fun onInit() {}

    fun onLoop() {}

    fun onKeyCallback(key: Int, action: Int, modifier: Int) {}

    fun onScrollCallback(direction: Int) {}

    fun onMouseClicked(button: Int, action: Int, modifier: Int) {}
}