package net.spartanb312.everett.game.training.impls

import net.spartanb312.everett.game.render.scene.Scene
import net.spartanb312.everett.game.training.Training
import net.spartanb312.everett.game.training.TrainingInfo
import net.spartanb312.everett.game.training.TrainingInfoContainer
import net.spartanb312.everett.game.training.modes.StrafeTraining

class FastStrafe(scene: Scene) : StrafeTraining(
    scene,
    3f,
    1f,
    moveSpeed = 3f,
    reverseChance = 0.3f
), TrainingInfoContainer by Companion {

    companion object : TrainingInfo(
        "FastStrafe",
        "Strafe following but faster. Keep your cursor locked on the target for as long as possible!"
    ) {
        override fun new(scene: Scene): Training {
            return FastStrafe(scene)
        }
    }

}