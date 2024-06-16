package net.spartanb312.everett.game.training.impls

import net.spartanb312.everett.game.render.scene.Scene
import net.spartanb312.everett.game.training.Training
import net.spartanb312.everett.game.training.TrainingInfo
import net.spartanb312.everett.game.training.TrainingInfoContainer
import net.spartanb312.everett.game.training.modes.GridTraining
import net.spartanb312.everett.utils.misc.asRange

class Grid(scene: Scene) : GridTraining(
    scene,
    3,
    2f.asRange,
    5f,
    6,
    5,
), TrainingInfoContainer by Companion {

    companion object : TrainingInfo("Grid", "") {
        override fun new(scene: Scene): Training {
            return Grid(scene)
        }
    }

}