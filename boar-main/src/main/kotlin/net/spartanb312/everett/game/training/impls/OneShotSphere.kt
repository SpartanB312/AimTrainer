package net.spartanb312.everett.game.training.impls

import net.spartanb312.everett.game.render.scene.Scene
import net.spartanb312.everett.game.training.Training
import net.spartanb312.everett.game.training.TrainingInfo
import net.spartanb312.everett.game.training.TrainingInfoContainer
import net.spartanb312.everett.game.training.modes.SphereShotTraining

class OneShotSphere(scene: Scene) : SphereShotTraining(
    scene,
    1
), TrainingInfoContainer by Companion {

    companion object : TrainingInfo("1-Shot Sphere", "") {
        override fun new(scene: Scene): Training {
            return OneShotSphere(scene)
        }
    }

}