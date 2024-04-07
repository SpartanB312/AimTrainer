package net.spartanb312.boar.game.training.impls

import net.spartanb312.boar.game.Player
import net.spartanb312.boar.game.entity.Ball
import net.spartanb312.boar.game.option.impls.AimAssistOption
import net.spartanb312.boar.game.render.BallRenderer
import net.spartanb312.boar.game.render.gui.Render2DManager
import net.spartanb312.boar.game.render.gui.impls.ScoreboardScreen
import net.spartanb312.boar.game.render.scene.Scene
import net.spartanb312.boar.game.training.BallHitTraining
import net.spartanb312.boar.game.training.Training
import net.spartanb312.boar.game.training.TrainingInfo
import net.spartanb312.boar.game.training.TrainingInfoContainer
import net.spartanb312.boar.utils.color.ColorRGB
import net.spartanb312.boar.utils.misc.asRange
import net.spartanb312.boar.utils.timing.Timer
import kotlin.random.Random

class BallFollowing(scoreboardScreen: ScoreboardScreen, scene: Scene) : BallHitTraining(
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
    0.5f
), TrainingInfoContainer by Companion {

    companion object : TrainingInfo("Following", "") {
        override fun new(scoreboardScreen: ScoreboardScreen, scene: Scene): Training {
            return BallFollowing(scoreboardScreen, scene)
        }
    }

    private val reverseTimer = Timer()

    private fun click() {
        if (stage != Stage.Training || Render2DManager.displaying) return
        var hit = false
        scene.getRayTracedResult(
            Player.offsetPos,
            Player.camera.front,
            if (AimAssistOption.bulletAdsorption.value) errorAngle else 0f
        )?.let {
            if (it is Ball) hit = true
        }
        shots++
        val currentTime = System.currentTimeMillis()
        val lastHitTime = hitTime.lastOrNull() ?: (currentTime - 50L)
        val lastHitTimeLapse = (currentTime - lastHitTime).toInt()
        val lastShotTimeLapse = (currentTime - lastShotTime).toInt()
        if (hit) {
            score += onHit(lastHitTimeLapse)
            hitTime.add(currentTime)
            reactionTimes.add(lastHitTimeLapse)
            hits++
        } else score -= onMiss(lastShotTimeLapse)
        score = score.coerceAtLeast(0)
        lastShotTime = currentTime
    }

    override fun onClick() {

    }

    override fun render() {
        entities.forEach {
            if (it is Ball) BallRenderer.render(it.pos.x, it.pos.y, it.pos.z, it.size, ColorRGB.GREEN, false, outlineC)
        }
        fadeBalls.toList().forEach { (it, time) ->
            val alphaRate = 1f - ((System.currentTimeMillis() - time) / fadeTime.toFloat())
            if (alphaRate > 0f) BallRenderer.render(
                it.pos.x,
                it.pos.y,
                it.pos.z,
                it.size,
                ColorRGB.GREEN.alpha((alphaRate.coerceAtMost(1f) * 255).toInt()),
                false,
                outlineC
            ) else fadeBalls.remove(it)
        }
    }

    override fun onTick() {
        super.onTick()
        var reverse = false
        reverseTimer.passedAndReset(1000) {
            reverse = 0.3 >= Random.nextDouble(0.0, 1.0)
        }
        click()
        entities.forEach {
            if (it is Ball) it.randomMove(reverse, 2.5f)
        }
    }

    override fun onHit(timeLapse: Int): Int {
        return (400f / timeLapse.coerceIn(50..2000)).toInt()
    }

    override fun onMiss(timeLapse: Int): Int {
        return (timeLapse.coerceIn(50..2000) / 10f).toInt()
    }

}