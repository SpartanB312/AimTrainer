package net.spartanb312.everett.game.training.modes

import net.spartanb312.everett.game.Player
import net.spartanb312.everett.game.entity.Ball
import net.spartanb312.everett.game.option.impls.AimAssistOption
import net.spartanb312.everett.game.render.BallRenderer
import net.spartanb312.everett.game.render.CrosshairRenderer
import net.spartanb312.everett.game.render.gui.Render2DManager
import net.spartanb312.everett.game.render.gui.impls.ScoreboardScreen
import net.spartanb312.everett.game.render.scene.Scene
import net.spartanb312.everett.game.render.scene.impls.AimTrainingScene
import net.spartanb312.everett.game.training.BallHitTraining
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.misc.asRange
import net.spartanb312.everett.utils.timing.Timer
import kotlin.random.Random

abstract class FollowingTraining(
    scene: Scene,
    amount: Int,
    sizeRange: ClosedFloatingPointRange<Float> = 1f.asRange,
    gap: Float = 5f,
    width: Int = 5,
    height: Int = 5,
    errorAngle: Float = 1f,
    horizontalOffset: Float = 0f,
    verticalOffset: Float = 0.5f,
) : BallHitTraining(
    scene,
    amount,
    sizeRange,
    gap,
    width,
    height,
    errorAngle,
    horizontalOffset,
    verticalOffset
) {

    override fun displayScoreboard() {
        this["Accuracy"] = String.format("%.2f", accuracy * 100) + "%"
        this["Fired"] = shots.toString()
        this["Hits"] = hits.toString()
        Render2DManager.displayScreen(
            ScoreboardScreen(
                showingScore,
                category,
                trainingName,
                (timeLapsed / 1000f).toInt(),
                results,
                medalCounter
            )
        )
    }

    private val reverseTimer = Timer()

    private fun click() {
        if (stage != Stage.Training || Render2DManager.displaying) return
        var hit = false
        scene.getRayTracedResult(
            Player.offsetPos,
            Player.camera.front,
            if (AimAssistOption.bulletAdsorption.value) {
                if (CrosshairRenderer.overrideErrorAngle != -1f) CrosshairRenderer.overrideErrorAngle
                else errorAngle
            } else 0f
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
        // NOTHING HERE
    }

    override fun render() {
        AimTrainingScene.skybox.onRender3D()
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