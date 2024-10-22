package net.spartanb312.everett.game.render

import net.spartanb312.everett.game.Player
import net.spartanb312.everett.game.crosshair.Crosshairs
import net.spartanb312.everett.game.option.impls.CrosshairOption
import net.spartanb312.everett.game.render.scene.SceneManager
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.matrix.scope
import net.spartanb312.everett.utils.color.ColorRGB

object CrosshairRenderer {

    var enabled = false; private set
    val transparentAlphaRate = 0.727451f
    val currentCrosshair: Crosshairs get() = CrosshairOption.crosshairType.value
    val raytraced get() = Player.raytraced && SceneManager.inTraining
    val errorAngle get() = (SceneManager.currentTraining?.errorAngle ?: currentCrosshair.crosshair.errorAngle)
    val shadowColor = ColorRGB.DARK_GRAY.alpha(128)
    val overrideErrorAngle get() = currentCrosshair.crosshair.overrideErrorAngle

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
                if (CrosshairOption.shadow) {
                    onRender(centerX, centerY, fov, true, colorRGB)
                    recover()
                }
                onRender(centerX, centerY, fov, false, colorRGB)
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