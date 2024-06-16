package net.spartanb312.everett.game.training.impls

import net.spartanb312.everett.game.render.scene.Scene
import net.spartanb312.everett.game.training.Training
import net.spartanb312.everett.game.training.TrainingInfo
import net.spartanb312.everett.game.training.TrainingInfoContainer
import net.spartanb312.everett.game.training.modes.FollowingTraining
import net.spartanb312.everett.utils.misc.asRange

class BallFollowing(scene: Scene) : FollowingTraining(
    scene,
    1,
    1.0f.asRange,
    5f,
    5,
    5,
    1f,
    0f,
    0.5f
), TrainingInfoContainer by Companion {

    companion object : TrainingInfo("Following", "") {
        override fun new(scene: Scene): Training {
            return BallFollowing(scene)
        }
    }

}