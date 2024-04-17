package net.spartanb312.boar.game.training.impls

import net.spartanb312.boar.game.render.gui.impls.ScoreboardScreen
import net.spartanb312.boar.game.render.scene.Scene
import net.spartanb312.boar.game.training.BallHitTraining
import net.spartanb312.boar.game.training.Training
import net.spartanb312.boar.game.training.TrainingInfo
import net.spartanb312.boar.game.training.TrainingInfoContainer
import net.spartanb312.boar.utils.misc.asRange

class OneShotWall(scoreboardScreen: ScoreboardScreen, scene: Scene) : BallHitTraining(
    scoreboardScreen,
    scene,
    1,
    0.5f.asRange,
    5f,
    9,
    7,
    1.5f,
    1.5f,
    0f,
    0.5f
), TrainingInfoContainer by Companion {

    companion object : TrainingInfo("1-Shot", "") {
        override fun new(scoreboardScreen: ScoreboardScreen, scene: Scene): Training {
            return OneShotWall(scoreboardScreen, scene)
        }
    }

    override fun onHit(timeLapse: Int): Int {
        return (10000f / timeLapse.coerceIn(50..2000)).toInt()
    }

    override fun onMiss(timeLapse: Int): Int {
        return (timeLapse.coerceIn(50..2000) / 30f).toInt()
    }

}