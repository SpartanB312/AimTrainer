package net.spartanb312.everett.game.training.impls

import net.spartanb312.everett.game.render.scene.Scene
import net.spartanb312.everett.game.training.Training
import net.spartanb312.everett.game.training.TrainingInfo
import net.spartanb312.everett.game.training.TrainingInfoContainer
import net.spartanb312.everett.game.training.modes.StrafeTraining

class Strafe(scene: Scene) : StrafeTraining(
    scene,
    3f,
    1f,
), TrainingInfoContainer by Companion {

    companion object : TrainingInfo(
        "Strafe",
        "Strafe following. Keep your cursor locked on the target for as long as possible!"
    ) {
        override fun new(scene: Scene): Training {
            return Strafe(scene)
        }
    }

}