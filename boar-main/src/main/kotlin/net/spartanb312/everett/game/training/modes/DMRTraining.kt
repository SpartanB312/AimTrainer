package net.spartanb312.everett.game.training.modes

import net.spartanb312.everett.AimTrainer
import net.spartanb312.everett.game.Player
import net.spartanb312.everett.game.entity.Ball
import net.spartanb312.everett.game.option.impls.AccessibilityOption
import net.spartanb312.everett.game.option.impls.AimAssistOption
import net.spartanb312.everett.game.render.BallRenderer
import net.spartanb312.everett.game.render.CrosshairRenderer
import net.spartanb312.everett.game.render.HitEffectsRenderer
import net.spartanb312.everett.game.render.MedalRenderer
import net.spartanb312.everett.game.render.gui.Render2DManager
import net.spartanb312.everett.game.render.gui.impls.ScoreboardScreen
import net.spartanb312.everett.game.render.scene.Scene
import net.spartanb312.everett.game.render.scene.impls.AimTrainingScene
import net.spartanb312.everett.game.training.BallHitTraining
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.misc.asRange

abstract class DMRTraining(
    scene: Scene,
    amount: Int,
    sizeRange: ClosedFloatingPointRange<Float> = 1f.asRange,
    gap: Float = 5f,
    width: Int = 5,
    height: Int = 5,
    errorAngle: Float = 1f,
    horizontalOffset: Float = 0f,
    verticalOffset: Float = 0.5f,
    distanceRange: ClosedFloatingPointRange<Float> = 50f.asRange,
    fadeTime: Int = 100,
    xOffset: Float = 0f,
    yOffset: Float = 0f,
    zOffset: Float = 0f,
    killResetTime: Int = 2500,
    private val moveSpeed: Float = 2f,
    private val scoreBase: Float = 1f,
    private val minKillTime: Int = 50,
    private val maxKillTime: Int = 2000
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
    distanceRange,
    5,
    fadeTime,
    killResetTime
) {

    override fun displayScoreboard() {
        this["Accuracy"] = String.format("%.2f", accuracy * 100) + "%"
        this["Fired"] = shots.toString()
        this["Hits"] = hits.toString()
        this["Time to kill"] = String.format("%.2f", reactionTimes.average())
        Render2DManager.displayScreen(
            ScoreboardScreen(
                showingScore, category,
                trainingName,
                (timeLapsed / 1000f).toInt(),
                results,
                medalCounter
            )
        )
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
        if (AccessibilityOption.shouldPktLoss) return
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

        fun solve() {
            result?.let {
                if (it is Ball) {
                    if (it.hp == 5) firstShot = true
                    if (it.hp > 0) hit = true
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
                    HitEffectsRenderer.killCounter++
                } else HitEffectsRenderer.hitCounter++
            }
            score = score.coerceAtLeast(0)
            lastShotTime = currentTime
        }
        if (AccessibilityOption.pingSimulate.value) {
            AimTrainer.taskManager.runLater(AccessibilityOption.ping) {
                RS.addRenderThreadJob { solve() }
            }
        } else { solve() }
    }

    override fun onTick() {
        super.onTick()
        entities.forEach {
            if (it is Ball) it.randomMove(false, moveSpeed)
        }
    }

    override fun onHit(timeLapse: Int): Int {
        medalCounter.triggerKill()
        val killTimeRange = minKillTime..maxKillTime.coerceAtLeast(minKillTime + 1)
        val applyScore = (scoreBase * 100000f / timeLapse.coerceIn(killTimeRange)).toInt()
        MedalRenderer.pushScore(applyScore)
        MedalRenderer.pushMessage("&fKilled &r${MedalRenderer.randomName}", color = ColorRGB(255, 20, 20))
        return applyScore
    }

    override fun onMiss(timeLapse: Int): Int {
        return 0
    }

}