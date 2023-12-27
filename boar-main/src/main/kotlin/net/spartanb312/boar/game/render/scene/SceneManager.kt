package net.spartanb312.boar.game.render.scene

import net.spartanb312.boar.game.render.scene.impls.SkyboxScene

object SceneManager {

    var currentScene: Scene = SkyboxScene

    fun onRender() {
        currentScene.onRender()
    }

}