package net.spartanb312.everett.game.training.impls

import net.spartanb312.everett.game.render.scene.Scene
import net.spartanb312.everett.game.training.Training
import net.spartanb312.everett.game.training.TrainingInfo
import net.spartanb312.everett.game.training.TrainingInfoContainer
import net.spartanb312.everett.game.training.modes.DMRTraining
import net.spartanb312.everett.game.training.modes.SurroundTraining

class SixBallSurround(scene: Scene) : SurroundTraining(
    scene,
    6,
), TrainingInfoContainer by Companion {

    companion object : TrainingInfo(
        "6-Ball Surround",
        "Surround mode with six targets. One shot to eliminate the moving target."
    ) {
        override fun new(scene: Scene): Training {
            return SixBallSurround(scene)
        }
    }

}