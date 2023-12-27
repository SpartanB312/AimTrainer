package net.spartanb312.boar.game.render.crosshair

import net.spartanb312.boar.utils.math.ceilToInt

abstract class Crosshair(resetTime: Float) {
    val resetTime = resetTime.ceilToInt()
    protected abstract var clickTime: Long
    abstract fun onRender(fov: Float)
    open fun onClick(): Boolean {
        val current = System.currentTimeMillis()
        return if (current - clickTime > resetTime) {
            clickTime = current
            true
        } else false
    }
}