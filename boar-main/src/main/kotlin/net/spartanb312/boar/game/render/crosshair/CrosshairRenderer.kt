package net.spartanb312.boar.game.render.crosshair

import net.spartanb312.boar.utils.color.ColorRGB

object CrosshairRenderer {

    val transparentColor = ColorRGB.WHITE.alpha(160)
    var currentCrosshair: Crosshairs = Crosshairs.BanditEvo

    fun onRender(fov: Float) = currentCrosshair.crosshair.onRender(fov)
    fun onClick(): Boolean = currentCrosshair.crosshair.onClick()

}