package net.spartanb312.everett.game.training.impls

import net.spartanb312.everett.game.render.scene.Scene
import net.spartanb312.everett.game.training.Training
import net.spartanb312.everett.game.training.TrainingInfo
import net.spartanb312.everett.game.training.TrainingInfoContainer
import net.spartanb312.everett.game.training.modes.DMRTraining

class SixBallDMR(scene: Scene) : DMRTraining(
    scene,
    6,
), TrainingInfoContainer by Companion {

    companion object : TrainingInfo("6-Ball DMR", "") {
        override fun new(scene: Scene): Training {
            return SixBallDMR(scene)
        }
    }

}