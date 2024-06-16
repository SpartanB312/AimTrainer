package net.spartanb312.everett.game.training.impls

import net.spartanb312.everett.game.render.scene.Scene
import net.spartanb312.everett.game.training.Training
import net.spartanb312.everett.game.training.TrainingInfo
import net.spartanb312.everett.game.training.TrainingInfoContainer
import net.spartanb312.everett.game.training.modes.GridTraining
import net.spartanb312.everett.utils.misc.asRange

class Grid3x3(scene: Scene) : GridTraining(
    scene,
    3,
    2f.asRange,
    5f,
    3,
    3,
), TrainingInfoContainer by Companion {

    companion object : TrainingInfo("Grid 3x3", "") {
        override fun new(scene: Scene): Training {
            return Grid3x3(scene)
        }
    }

}