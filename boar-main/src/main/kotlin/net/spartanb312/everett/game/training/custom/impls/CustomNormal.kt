package net.spartanb312.everett.game.training.custom.impls

import net.spartanb312.everett.game.render.MedalRenderer
import net.spartanb312.everett.game.render.scene.Scene
import net.spartanb312.everett.game.training.BallHitTraining
import net.spartanb312.everett.game.training.Training
import net.spartanb312.everett.game.training.custom.CustomTraining
import net.spartanb312.everett.game.training.custom.CustomTrainingFlag
import net.spartanb312.everett.utils.misc.asRange

class CustomNormal(
    scene: Scene,
    amount: Int,
    size: Float,
    gap: Float,
    width: Int,
    height: Int,
    errorAngle: Float,
    distance: Float,
    xOffset: Float,
    yOffset: Float,
    zOffset: Float,
    killResetTime: Int,
    private val scoreBase: Float,
    private val punishmentBase: Float,
    private val minKillTime: Int,
    private val maxKillTime: Int,
) : CustomTrainingFlag, BallHitTraining(
    scene,
    amount,
    size.asRange,
    gap,
    width,
    height,
    xOffset,
    yOffset,
    zOffset,
    errorAngle,
    0f,
    0f,
    distance.asRange,
    1,
    100,
    killResetTime
) {

    override val trainingName = "Ball Hit Training"
    override val description = CustomTraining.description
    override val category = CustomTraining.category
    override fun new(scene: Scene): Training = CustomTraining.new(scene)

    override fun onHit(timeLapse: Int): Int {
        medalCounter.triggerKill()
        val killTimeRange = minKillTime..maxKillTime.coerceAtLeast(minKillTime + 1)
        val applyScore = (scoreBase * 10000f / timeLapse.coerceIn(killTimeRange)).toInt()
        MedalRenderer.pushScore(applyScore)
        return applyScore
    }

    override fun onMiss(timeLapse: Int): Int {
        val killTimeRange = minKillTime..maxKillTime.coerceAtLeast(minKillTime + 1)
        return (punishmentBase * timeLapse.coerceIn(killTimeRange) / 10f).toInt()
    }

}