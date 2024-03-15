package net.spartanb312.boar.game.render.scene

import net.spartanb312.boar.game.input.InputManager
import net.spartanb312.boar.game.render.gui.Render2DManager
import net.spartanb312.boar.game.render.scene.impls.DummyScene

object SceneManager {

    var currentScene: Scene = DummyScene

    fun onRender() {
        currentScene.onRender()
    }

    private fun registerScene(scene: Scene) {
        InputManager.registerKeyRelease(scene)
        InputManager.registerKeyRepeating(scene)
        InputManager.registerKeyTyped(scene)
        InputManager.registerMouseClick(scene)
        InputManager.registerMouseRelease(scene)
    }

    private fun unregisterScene(scene: Scene) {
        InputManager.unregisterKeyRelease(scene)
        InputManager.unregisterKeyRepeating(scene)
        InputManager.unregisterKeyTyped(scene)
        InputManager.unregisterMouseClick(scene)
        InputManager.unregisterMouseRelease(scene)
    }

    fun switchScene(scene: Scene) {
        if (currentScene != scene) {
            unregisterScene(currentScene)
            Render2DManager.unsubscribe(currentScene)
            currentScene = scene
            registerScene(currentScene)
            Render2DManager.subscribe(currentScene)
            scene.onInit()
        }
    }

}