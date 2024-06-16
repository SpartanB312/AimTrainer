package net.spartanb312.everett.game.training.impls

import net.spartanb312.everett.game.render.scene.Scene
import net.spartanb312.everett.game.training.Training
import net.spartanb312.everett.game.training.TrainingInfo
import net.spartanb312.everett.game.training.TrainingInfoContainer
import net.spartanb312.everett.game.training.modes.WallShotTraining

class OneShotWall(scene: Scene) : WallShotTraining(
    scene,
    1,
), TrainingInfoContainer by Companion {

    companion object : TrainingInfo("1-Shot", "") {
        override fun new(scene: Scene): Training {
            return OneShotWall(scene)
        }
    }

}