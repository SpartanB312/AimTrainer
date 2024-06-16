package net.spartanb312.everett.game.training.custom

import com.google.common.primitives.Ints.min
import net.spartanb312.everett.game.render.MedalRenderer
import net.spartanb312.everett.game.render.scene.Scene
import net.spartanb312.everett.game.training.BallHitTraining
import net.spartanb312.everett.game.training.Training
import net.spartanb312.everett.utils.misc.asRange

object CustomBallHit : CustomTraining("Ball Hit Training", "Custom Game") {

    private val amount by setting("Amount", 6, 1..50, 1)
    private val size by setting("Size", 1f, 0.1f..5f, 0.1f)
    private val gap by setting("Gap", 5f, 1f..20f, 0.5f)
    private val width by setting("Width", 5, 2..20, 1)
    private val height by setting("Height", 5, 2..20, 1)
    private val defaultErrorAngle by setting("Default Error Angle", 1f, 0f..10f, 0.1f)
    private val distance by setting("Distance", 50f, 10f..150f, 0.5f)
    private val killResetTime by setting("Continuous Kill Reset", 300, 0..10000, 50)
    private val scoreBase by setting("Score Base", 1f, 0.1f..10f, 0.1f)
    private val punishmentBase by setting("Punishment Base", 1f, 0.1f..10f, 0.1f)
    private val minKillTime by setting("Min Kill Time", 50, 10..1000, 10)
    private val maxKillTime by setting("Max Kill Time", 2000, 1000..10000, 50)

    override fun new(scene: Scene): Training {
        val maxAmount = min(width * height - 1, amount)
        return Impl(
            scene,
            maxAmount,
            size,
            gap,
            width,
            height,
            defaultErrorAngle,
            distance,
            killResetTime,
            scoreBase,
            punishmentBase,
            minKillTime,
            maxKillTime
        )
    }

    class Impl(
        scene: Scene,
        amount: Int,
        size: Float,
        gap: Float,
        width: Int,
        height: Int,
        errorAngle: Float,
        distance: Float,
        killResetTime: Int,
        private val scoreBase: Float = 1.0f,
        private val punishmentBase: Float = 1.0f,
        private val minKillTime: Int = 50,
        private val maxKillTime: Int = 2000,
    ) : BallHitTraining(
        scene,
        amount,
        size.asRange,
        gap,
        width,
        height,
        errorAngle,
        0f,
        0f,
        distance.asRange,
        1,
        100,
        killResetTime
    ) {

        override val trainingName = CustomBallHit.trainingName
        override val description = CustomBallHit.description

        override fun new(scene: Scene): Training {
            throw Exception("This method shouldn't be reached!")
        }

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

}