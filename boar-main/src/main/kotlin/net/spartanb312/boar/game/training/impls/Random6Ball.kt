package net.spartanb312.boar.game.training.impls

import net.spartanb312.boar.game.render.gui.impls.ScoreboardScreen
import net.spartanb312.boar.game.render.scene.Scene
import net.spartanb312.boar.game.training.BallHitTraining
import net.spartanb312.boar.game.training.Training
import net.spartanb312.boar.game.training.TrainingInfo
import net.spartanb312.boar.game.training.TrainingInfoContainer
import net.spartanb312.boar.utils.misc.asRange

class Random6Ball(scoreboardScreen: ScoreboardScreen, scene: Scene) : BallHitTraining(
    scoreboardScreen,
    scene,
    6,
    2f.asRange,
    5f,
    19,
    19,
    1f,
    1f,
    0f,
    0.5f,
    -100f..100f
), TrainingInfoContainer by Companion {

    companion object : TrainingInfo("Random 6 Balls", "") {
        override fun new(scoreboardScreen: ScoreboardScreen, scene: Scene): Training {
            return Random6Ball(scoreboardScreen, scene)
        }
    }

    override fun render() {
        super.render()
        //scene.entities.forEach {
        //    val direct = it.pos - Player.offsetPos
        //    val distance = direct.length
        //    val ray = Player.camera.front
        //
        //    //BallRenderer.render(refPos.x, refPos.y, refPos.z, 1f,ColorRGB.WHITE)
        //}
    }

    override fun onHit(timeLapse: Int): Int {
        return (10000f / timeLapse.coerceIn(50..2000)).toInt()
    }

    override fun onMiss(timeLapse: Int): Int {
        return (timeLapse.coerceIn(50..2000) / 10f).toInt()
    }

}