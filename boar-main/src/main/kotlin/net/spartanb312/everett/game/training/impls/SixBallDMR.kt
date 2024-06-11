package net.spartanb312.everett.game.training.impls

import net.spartanb312.everett.game.Player
import net.spartanb312.everett.game.entity.Ball
import net.spartanb312.everett.game.medal.MedalCounter
import net.spartanb312.everett.game.option.impls.AimAssistOption
import net.spartanb312.everett.game.render.BallRenderer
import net.spartanb312.everett.game.render.crosshair.CrosshairRenderer
import net.spartanb312.everett.game.render.gui.MedalRenderer
import net.spartanb312.everett.game.render.gui.Render2DManager
import net.spartanb312.everett.game.render.gui.impls.ScoreboardScreen
import net.spartanb312.everett.game.render.scene.Scene
import net.spartanb312.everett.game.render.scene.impls.AimTrainingScene
import net.spartanb312.everett.game.training.BallHitTraining
import net.spartanb312.everett.game.training.Training
import net.spartanb312.everett.game.training.TrainingInfo
import net.spartanb312.everett.game.training.TrainingInfoContainer
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.misc.asRange

class SixBallDMR(scoreboardScreen: ScoreboardScreen, scene: Scene) : BallHitTraining(
    scoreboardScreen,
    scene,
    6,
    1.0f.asRange,
    5f,
    5,
    5,
    1f,
    0f,
    0.5f,
    ballHP = 5
), TrainingInfoContainer by Companion {

    companion object : TrainingInfo("6-Ball DMR", "") {
        override fun new(scoreboardScreen: ScoreboardScreen, scene: Scene): Training {
            return SixBallDMR(scoreboardScreen, scene)
        }
    }

    override fun render() {
        AimTrainingScene.skybox.onRender3D()
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

    override fun onClick() {
        if (stage != Stage.Training || Render2DManager.displaying) return
        var hit = false
        var killed = false
        var firstShot = false
        val result = scene.getRayTracedResult(
            Player.offsetPos,
            Player.camera.front,
            if (AimAssistOption.bulletAdsorption.value) {
                if (CrosshairRenderer.overrideErrorAngle != -1f) CrosshairRenderer.overrideErrorAngle
                else errorAngle
            } else 0f
        )
        result?.let {
            if (it is Ball) {
                hit = true
                if (it.hp == 5) firstShot = true
                it.hp -= 1
                if (it.hp == 0) {
                    killed = true
                    entities.add(generateBall(it))
                    entities.remove(it)
                    fadeBalls[it] = System.currentTimeMillis()
                }
            }
        }
        medalCounter.perfectDMR(result)
        shots++
        val currentTime = System.currentTimeMillis()
        val lastHitTime = hitTime.lastOrNull() ?: (currentTime - 50L)
        val lastHitTimeLapse = (currentTime - lastHitTime).toInt()
        if (firstShot) hitTime.add(currentTime)
        if (hit) {
            hits++
            if (killed) {
                score += onHit(lastHitTimeLapse)
                reactionTimes.add(lastHitTimeLapse)
            }
        }
        score = score.coerceAtLeast(0)
        lastShotTime = currentTime
    }

    override fun onTick() {
        super.onTick()
        entities.forEach {
            if (it is Ball) it.randomMove(false, 2.5f)
        }
    }

    private var medalCounter = MedalCounter(3000)

    override fun reset(): Training {
        medalCounter.reset()
        return super.reset()
    }

    override fun onHit(timeLapse: Int): Int {
        medalCounter.triggerKill()
        val applyScore = (100000f / timeLapse.coerceIn(50..2000)).toInt()
        MedalRenderer.pushScore(applyScore)
        MedalRenderer.pushMessage("&fKilled &r${MedalRenderer.randomName}", color = ColorRGB(255, 20, 20))
        return applyScore
    }

    override fun onMiss(timeLapse: Int): Int {
        return 0
    }

}