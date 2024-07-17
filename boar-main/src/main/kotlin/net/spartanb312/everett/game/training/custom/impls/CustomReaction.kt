package net.spartanb312.everett.game.training.custom.impls

import net.spartanb312.everett.game.render.scene.Scene
import net.spartanb312.everett.game.training.ReactionTest
import net.spartanb312.everett.game.training.Training
import net.spartanb312.everett.game.training.custom.CustomTraining
import net.spartanb312.everett.game.training.custom.CustomTrainingFlag

class CustomReaction(
    rounds: Int,
    minInterval: Int,
    gap: Int
) : CustomTrainingFlag, ReactionTest(rounds, minInterval.toLong(), gap.toLong()) {
    override val trainingName = "Reaction Test"
    override val description = CustomTraining.description
    override val category = CustomTraining.category
    override fun new(scene: Scene): Training = CustomTraining.new(scene)
}