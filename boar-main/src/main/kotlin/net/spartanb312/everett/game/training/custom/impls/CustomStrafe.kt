package net.spartanb312.everett.game.training.custom.impls

import net.spartanb312.everett.game.render.scene.Scene
import net.spartanb312.everett.game.training.Training
import net.spartanb312.everett.game.training.custom.CustomTraining
import net.spartanb312.everett.game.training.custom.CustomTrainingFlag
import net.spartanb312.everett.game.training.modes.StrafeTraining

class CustomStrafe(
    scene: Scene,
    size: Float,
    xOffset: Float,
    yOffset: Float,
    zOffset: Float,
    errorAngle: Float = 1f,
    distance: Float,
    boundary: Float,
    moveSpeed: Float,
    reverseAttemptFreq: Int,
    reverseCooldown: Int,
    reverseChance: Float,
    scoreBase: Float,
    punishmentBase: Float
) : CustomTrainingFlag, StrafeTraining(
    scene,
    size,
    errorAngle,
    0f,
    0f,
    distance,
    xOffset,
    yOffset,
    zOffset,
    boundary,
    moveSpeed,
    reverseAttemptFreq,
    reverseCooldown,
    reverseChance,
    scoreBase,
    punishmentBase
){

    override val trainingName = "Strafe Training"
    override val description = CustomTraining.description
    override val category = CustomTraining.category
    override fun new(scene: Scene): Training = CustomTraining.new(scene)

}