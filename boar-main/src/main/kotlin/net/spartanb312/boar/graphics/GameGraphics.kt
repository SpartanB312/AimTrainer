package net.spartanb312.boar.graphics

import net.spartanb312.boar.utils.misc.Profiler

interface GameGraphics {
    fun onInit() {}

    fun Profiler.onLoop() {}

    fun onKeyCallback(key: Int, action: Int, modifier: Int) {}

    fun onScrollCallback(direction: Int) {}

    fun onMouseClicked(button: Int, action: Int, modifier: Int) {}

    fun onSync() {}
}