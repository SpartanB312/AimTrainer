package net.spartanb312.everett.game.training.modes

import net.spartanb312.everett.game.render.MedalRenderer
import net.spartanb312.everett.game.render.scene.Scene
import net.spartanb312.everett.game.training.BallHitTraining
import net.spartanb312.everett.utils.misc.asRange

abstract class GridTraining(
    scene: Scene,
    amount: Int,
    sizeRange: ClosedFloatingPointRange<Float> = 2f.asRange,
    gap: Float = 5f,
    width: Int = 6,
    height: Int = 5,
    errorAngle: Float = 1.5f,
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
    verticalOffset,
    killResetTime = 300
) {

    override fun onHit(timeLapse: Int): Int {
        medalCounter.triggerKill()
        val applyScore = (10000f / timeLapse.coerceIn(50..2000)).toInt()
        MedalRenderer.pushScore(applyScore)
        return applyScore
    }

    override fun onMiss(timeLapse: Int): Int {
        return (timeLapse.coerceIn(50..2000) / 10f).toInt()
    }

}