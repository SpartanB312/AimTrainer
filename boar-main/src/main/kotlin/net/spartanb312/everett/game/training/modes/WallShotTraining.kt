package net.spartanb312.everett.game.training.modes

import net.spartanb312.everett.game.render.MedalRenderer
import net.spartanb312.everett.game.render.scene.Scene
import net.spartanb312.everett.game.training.BallHitTraining
import net.spartanb312.everett.utils.misc.asRange

abstract class WallShotTraining(
    scene: Scene,
    amount: Int,
    sizeRange: ClosedFloatingPointRange<Float> = 0.5f.asRange,
    gap: Float = 5f,
    width: Int = 9,
    height: Int = 7,
    errorAngle: Float = 1f,
    horizontalOffset: Float = 0f,
    verticalOffset: Float = 0.5f,
    xOffset: Float = 0f,
    yOffset: Float = 0f,
    zOffset: Float = 0f,
) : BallHitTraining(
    scene,
    amount,
    sizeRange,
    gap,
    width,
    height,
    xOffset,
    yOffset,
    zOffset,
    errorAngle,
    horizontalOffset,
    verticalOffset,
    killResetTime = 500
) {

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