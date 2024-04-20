package net.spartanb312.everett.game.render.crosshair

import net.spartanb312.everett.game.option.impls.CrosshairOption
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.matrix.scope
import net.spartanb312.everett.utils.color.ColorRGB

object CrosshairRenderer {

    var enabled = false; private set
    val transparentAlphaRate = 0.627451f
    val currentCrosshair: Crosshairs get() = CrosshairOption.crosshairType.value

    fun onRender(fov: Float, colorRGB: ColorRGB = ColorRGB.WHITE) =
        onRender(true, RS.centerXF, RS.centerYF, fov, colorRGB)

    fun onRender(
        checkEnabled: Boolean = true,
        centerX: Float,
        centerY: Float,
        fov: Float,
        colorRGB: ColorRGB = ColorRGB.WHITE
    ) {
        RS.matrixLayer.scope {
            if (!checkEnabled || enabled) with(currentCrosshair.crosshair) {
                onRender(centerX, centerY, fov, colorRGB)
            }
        }
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

}