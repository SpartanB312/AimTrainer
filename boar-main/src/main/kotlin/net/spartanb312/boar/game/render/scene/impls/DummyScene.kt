package net.spartanb312.boar.game.render.scene.impls

import net.spartanb312.boar.game.audio.BGMPlayer
import net.spartanb312.boar.game.render.scene.Scene

object DummyScene : Scene() {

    override fun onInit() {
        BGMPlayer.fadeIn()
    }

    override fun onClosed() {
        BGMPlayer.fadeOut()
    }

    override fun render3D() {
        // NOTHING HERE
    }


}