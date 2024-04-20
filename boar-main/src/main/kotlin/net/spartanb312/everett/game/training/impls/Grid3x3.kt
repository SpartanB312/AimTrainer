package net.spartanb312.everett.game.training.impls

import net.spartanb312.everett.game.render.gui.impls.ScoreboardScreen
import net.spartanb312.everett.game.render.scene.Scene
import net.spartanb312.everett.game.training.BallHitTraining
import net.spartanb312.everett.game.training.Training
import net.spartanb312.everett.game.training.TrainingInfo
import net.spartanb312.everett.game.training.TrainingInfoContainer
import net.spartanb312.everett.utils.misc.asRange

class Grid3x3(scoreboardScreen: ScoreboardScreen, scene: Scene) : BallHitTraining(
    scoreboardScreen,
    scene,
    3,
    2f.asRange,
    5f,
    3,
    3,
    1.5f,
    1.5f,
    0f,
    0.5f
), TrainingInfoContainer by Companion {

    companion object : TrainingInfo("Grid 3x3", "") {
        override fun new(scoreboardScreen: ScoreboardScreen, scene: Scene): Training {
            return Grid3x3(scoreboardScreen, scene)
        }
    }

    override fun onHit(timeLapse: Int): Int {
        return (10000f / timeLapse.coerceIn(50..2000)).toInt()
    }

    override fun onMiss(timeLapse: Int): Int {
        return (timeLapse.coerceIn(50..2000) / 10f).toInt()
    }

}