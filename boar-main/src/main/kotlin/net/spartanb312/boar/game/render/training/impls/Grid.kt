package net.spartanb312.boar.game.render.training.impls

import net.spartanb312.boar.game.render.gui.impls.ScoreboardScreen
import net.spartanb312.boar.game.render.scene.Scene
import net.spartanb312.boar.game.render.training.BallHitTraining

class Grid(scoreboardScreen: ScoreboardScreen, scene: Scene) : BallHitTraining(
    "Grid",
    scoreboardScreen,
    scene,
    4,
    2f,
    5f,
    5,
    5,
    0f,
    0.5f
) {

    override val errorOffset = { distance: Double, radius: Float -> distance < radius + 0.7f }

    override fun onHit(timeLapse: Int): Int {
        return (10000f / timeLapse.coerceIn(50..2000)).toInt()
    }

    override fun onMiss(timeLapse: Int): Int {
        return (timeLapse.coerceIn(50..2000) / 10f).toInt()
    }
}