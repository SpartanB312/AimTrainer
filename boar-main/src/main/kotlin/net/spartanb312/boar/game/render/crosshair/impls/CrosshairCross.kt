package net.spartanb312.boar.game.render.crosshair.impls

import net.spartanb312.boar.game.render.crosshair.Crosshair

object CrosshairCross : Crosshair(0f) {

    override var clickTime = System.currentTimeMillis()

    override fun onRender(fov: Float) {

    }

}