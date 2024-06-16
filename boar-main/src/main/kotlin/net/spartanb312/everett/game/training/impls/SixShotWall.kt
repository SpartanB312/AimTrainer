package net.spartanb312.everett.game.training.impls

import net.spartanb312.everett.game.render.scene.Scene
import net.spartanb312.everett.game.training.Training
import net.spartanb312.everett.game.training.TrainingInfo
import net.spartanb312.everett.game.training.TrainingInfoContainer
import net.spartanb312.everett.game.training.modes.WallShotTraining

class SixShotWall(scene: Scene) : WallShotTraining(
    scene,
    6,
), TrainingInfoContainer by Companion {

    companion object : TrainingInfo("6-Shot", "") {
        override fun new(scene: Scene): Training {
            return SixShotWall(scene)
        }
    }

}