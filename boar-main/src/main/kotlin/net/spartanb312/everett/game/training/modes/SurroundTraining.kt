package net.spartanb312.everett.game.training.modes

import net.spartanb312.everett.game.entity.Ball
import net.spartanb312.everett.game.render.BallRenderer
import net.spartanb312.everett.game.render.gui.Render2DManager
import net.spartanb312.everett.game.render.gui.impls.ScoreboardScreen
import net.spartanb312.everett.game.render.scene.Scene
import net.spartanb312.everett.game.render.scene.impls.AimTrainingScene
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.misc.asRange

abstract class SurroundTraining(
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
    moveSpeed: Float = 2f,
    scoreBase: Float = 1f,
    minKillTime: Int = 50,
    maxKillTime: Int = 2000
) : DMRTraining(
    scene,
    amount,
    sizeRange,
    gap,
    width,
    height,
    errorAngle,
    horizontalOffset,
    verticalOffset,
    distanceRange,
    fadeTime,
    xOffset,
    yOffset,
    zOffset,
    killResetTime,
    moveSpeed,
    scoreBase,
    minKillTime,
    maxKillTime,
    1
) {

    override fun displayScoreboard() {
        this["Accuracy"] = String.format("%.2f", accuracy * 100) + "%"
        this["Fired"] = shots.toString()
        this["Hits"] = hits.toString()
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
        vecTimer.passedAndReset(30) { entities.forEach { if (it is Ball) it.updateVec(false, moveSpeed) } }
        entities.forEach {
            if (it is Ball) {
                BallRenderer.render(
                    it.pos.x,
                    it.pos.y,
                    it.pos.z,
                    it.size,
                    ColorRGB.RED
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
                ColorRGB.RED.alpha((alphaRate.coerceAtMost(1f) * 255).toInt())
            ) else fadeBalls.remove(it)
        }
    }

}