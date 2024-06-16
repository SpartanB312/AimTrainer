package net.spartanb312.everett.game.training.impls

import net.spartanb312.everett.game.render.scene.Scene
import net.spartanb312.everett.game.training.Training
import net.spartanb312.everett.game.training.TrainingInfo
import net.spartanb312.everett.game.training.TrainingInfoContainer
import net.spartanb312.everett.game.training.modes.DMRTraining

class OneBallDMR(scene: Scene) : DMRTraining(
    scene,
    1,
), TrainingInfoContainer by Companion {

    companion object : TrainingInfo("1-Ball DMR", "") {
        override fun new(scene: Scene): Training {
            return OneBallDMR(scene)
        }
    }

}