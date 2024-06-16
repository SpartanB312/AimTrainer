package net.spartanb312.everett.game.render.scene

import net.spartanb312.everett.game.input.InputManager
import net.spartanb312.everett.game.render.gui.Render2DManager
import net.spartanb312.everett.game.render.scene.impls.AimTrainingScene
import net.spartanb312.everett.game.render.scene.impls.DummyScene
import net.spartanb312.everett.game.training.Training

object SceneManager {

    var currentScene: Scene = DummyScene
    val inTraining get() = currentScene == AimTrainingScene
    val currentTraining: Training?
        get() = when (currentScene) {
            AimTrainingScene -> AimTrainingScene.currentTraining
            else -> null
        }

    val errorAngle
        get() = when (currentScene) {
            AimTrainingScene -> AimTrainingScene.currentTraining?.errorAngle ?: 0f
            else -> 0f
        }

    fun onRender() {
        currentScene.render3D()
    }

    fun onTick() {
        currentScene.onTick()
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
            currentScene.onClosed()
            currentScene = scene
            registerScene(currentScene)
            Render2DManager.subscribe(currentScene)
            scene.onInit()
        }
    }

}