package net.spartanb312.boar.game.render.crosshair

import net.spartanb312.boar.game.option.impls.CrosshairOption
import net.spartanb312.boar.utils.color.ColorRGB

object CrosshairRenderer {

    var enabled = false; private set
    val transparentAlphaRate = 0.627451f
    val currentCrosshair: Crosshairs get() = CrosshairOption.crosshairType.value

    fun onRender(fov: Float, colorRGB: ColorRGB = ColorRGB.WHITE) {
        if (enabled) currentCrosshair.crosshair.onRender(fov, false, colorRGB)
    }

    fun onClick(): Boolean {
        return if (enabled) currentCrosshair.crosshair.onClick()
        else false
    }

    fun enable() {
        enabled = true
    }

    fun disable() {
        enabled = false
    }

    fun testRender(fov: Float, colorRGB: ColorRGB = ColorRGB.WHITE) =
        currentCrosshair.crosshair.onRender(fov, true, colorRGB)

}