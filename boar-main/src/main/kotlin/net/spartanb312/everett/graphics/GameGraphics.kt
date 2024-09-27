package net.spartanb312.everett.graphics

import net.spartanb312.everett.utils.misc.Profiler

interface GameGraphics {
    fun onInit() {}

    fun Profiler.onLoop() {}

    fun onFramebufferDrawing() {}

    fun onKeyCallback(key: Int, action: Int, modifier: Int) {}

    fun onScrollCallback(direction: Int) {}

    fun onMouseClicked(button: Int, action: Int, modifier: Int) {}

    fun onSync() {}

    fun onResolutionUpdate(oldWith: Int, oldHeight: Int, newWidth: Int, newHeight: Int) {}
}