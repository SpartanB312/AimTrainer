package net.spartanb312.everett.game.training.impls

import net.spartanb312.everett.game.medal.MedalCounter
import net.spartanb312.everett.game.render.MedalRenderer
import net.spartanb312.everett.game.render.gui.impls.ScoreboardScreen
import net.spartanb312.everett.game.render.scene.Scene
import net.spartanb312.everett.game.training.BallHitTraining
import net.spartanb312.everett.game.training.Training
import net.spartanb312.everett.game.training.TrainingInfo
import net.spartanb312.everett.game.training.TrainingInfoContainer
import net.spartanb312.everett.utils.misc.asRange

class OneShotSphere(scoreboardScreen: ScoreboardScreen, scene: Scene) : BallHitTraining(
    scoreboardScreen,
    scene,
    1,
    0.5f.asRange,
    5f,
    19,
    19,
    1f,
    0f,
    0.5f,
    -50f..50f
), TrainingInfoContainer by Companion {

    companion object : TrainingInfo("1-Shot Sphere", "") {
        override fun new(scoreboardScreen: ScoreboardScreen, scene: Scene): Training {
            return OneShotSphere(scoreboardScreen, scene)
        }
    }

    private var medalCounter = MedalCounter(1500)

    override fun reset(): Training {
        medalCounter.reset()
        return super.reset()
    }

    override fun onHit(timeLapse: Int): Int {
        medalCounter.triggerKill()
        val applyScore = (10000f / timeLapse.coerceIn(50..2000)).toInt()
        MedalRenderer.pushScore(applyScore)
        return applyScore
    }

    override fun onMiss(timeLapse: Int): Int {
        return (timeLapse.coerceIn(50..2000) / 30f).toInt()
    }

}