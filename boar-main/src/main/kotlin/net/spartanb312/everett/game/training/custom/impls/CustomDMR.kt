package net.spartanb312.everett.game.training.custom.impls

import net.spartanb312.everett.game.render.scene.Scene
import net.spartanb312.everett.game.training.Training
import net.spartanb312.everett.game.training.custom.CustomTraining
import net.spartanb312.everett.game.training.custom.CustomTrainingFlag
import net.spartanb312.everett.game.training.modes.DMRTraining
import net.spartanb312.everett.utils.misc.asRange

class CustomDMR(
    scene: Scene,
    amount: Int,
    size: Float,
    gap: Float,
    width: Int,
    height: Int,
    xOffset: Float,
    yOffset: Float,
    zOffset: Float,
    errorAngle: Float,
    distance: Float,
    killResetTime: Int,
    moveSpeed: Float,
    scoreBase: Float,
    minKillTime: Int,
    maxKillTime: Int
) : CustomTrainingFlag, DMRTraining(
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
    100,
    xOffset,
    yOffset,
    zOffset,
    killResetTime,
    moveSpeed,
    scoreBase,
    minKillTime,
    maxKillTime
) {
    override val trainingName = "DMR Training"
    override val description = CustomTraining.description
    override val category = CustomTraining.category
    override fun new(scene: Scene): Training = CustomTraining.new(scene)
}