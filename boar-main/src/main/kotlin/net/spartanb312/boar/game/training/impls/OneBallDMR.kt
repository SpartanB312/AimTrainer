package net.spartanb312.boar.game.training.impls

import net.spartanb312.boar.game.entity.Ball
import net.spartanb312.boar.game.render.BallRenderer
import net.spartanb312.boar.game.render.gui.impls.ScoreboardScreen
import net.spartanb312.boar.game.render.scene.Scene
import net.spartanb312.boar.game.training.BallHitTraining
import net.spartanb312.boar.game.training.Training
import net.spartanb312.boar.game.training.TrainingInfo
import net.spartanb312.boar.game.training.TrainingInfoContainer
import net.spartanb312.boar.utils.color.ColorRGB
import net.spartanb312.boar.utils.misc.asRange

class OneBallDMR(scoreboardScreen: ScoreboardScreen, scene: Scene) : BallHitTraining(
    scoreboardScreen,
    scene,
    1,
    1.0f.asRange,
    5f,
    5,
    5,
    1f,
    1f,
    0f,
    0.5f,
    ballHP = 5
), TrainingInfoContainer by Companion {

    companion object : TrainingInfo("1-Ball DMR", "") {
        override fun new(scoreboardScreen: ScoreboardScreen, scene: Scene): Training {
            return OneBallDMR(scoreboardScreen, scene)
        }
    }

    override fun render() {
        entities.forEach {
            if (it is Ball) {
                BallRenderer.render(
                    it.pos.x,
                    it.pos.y,
                    it.pos.z,
                    it.size,
                    ColorRGB.RED.mix(ColorRGB.GREEN, (it.hp - 1) / (ballHP - 1).toFloat())
                )
            }
        }
        fadeBalls.toList().forEach { (it, time) ->
            val alphaRate = 1f - ((System.currentTimeMillis() - time) / fadeTime.toFloat())
            if (alphaRate > 0f) BallRenderer.render(
                it.pos.x,
                it.pos.y,
                it.pos.z,
                it.size,
                ColorRGB.RED.mix(ColorRGB.GREEN, (it.hp - 1) / (ballHP - 1).toFloat())
                    .alpha((alphaRate.coerceAtMost(1f) * 255).toInt())
            ) else fadeBalls.remove(it)
        }
    }

    override fun onTick() {
        super.onTick()
        entities.forEach {
            if (it is Ball) it.randomMove(false, 1.5f)
        }
    }

    override fun onHit(timeLapse: Int): Int {
        return (10000f / timeLapse.coerceIn(50..2000)).toInt()
    }

    override fun onMiss(timeLapse: Int): Int {
        return (timeLapse.coerceIn(50..2000) / 30f).toInt()
    }

}