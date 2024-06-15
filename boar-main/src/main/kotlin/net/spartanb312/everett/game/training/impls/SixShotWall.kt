package net.spartanb312.everett.game.training.impls

import net.spartanb312.everett.game.medal.MedalCounter
import net.spartanb312.everett.game.render.MedalRenderer
import net.spartanb312.everett.game.render.gui.impls.ScoreboardScreen
import net.spartanb312.everett.game.render.scene.Scene
import net.spartanb312.everett.game.training.*
import net.spartanb312.everett.utils.misc.asRange

class SixShotWall(scoreboardScreen: ScoreboardScreen, scene: Scene) : BallHitTraining(
    scoreboardScreen,
    scene,
    6,
    0.5f.asRange,
    5f,
    9,
    7,
    1f,
    0f,
    0.5f
), MedalContainer, TrainingInfoContainer by Companion {

    companion object : TrainingInfo("6-Shot", "") {
        override fun new(scoreboardScreen: ScoreboardScreen, scene: Scene): Training {
            return SixShotWall(scoreboardScreen, scene)
        }
    }

    override val medalCounter = MedalCounter(500)

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