package net.spartanb312.everett.game.training

import net.spartanb312.everett.game.render.scene.Scene

interface TrainingInfoContainer {
    val trainingName: String
    val description: String
    val category: String
    fun new(scene: Scene): Training
}

abstract class TrainingInfo(
    override val trainingName: String,
    override val description: String,
    override val category: String = "AIM TRAINING"
) : TrainingInfoContainer