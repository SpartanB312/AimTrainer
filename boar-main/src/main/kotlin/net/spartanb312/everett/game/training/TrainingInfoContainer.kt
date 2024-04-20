package net.spartanb312.everett.game.training

import net.spartanb312.everett.game.render.gui.impls.ScoreboardScreen
import net.spartanb312.everett.game.render.scene.Scene

interface TrainingInfoContainer {
    val trainingName: String
    val description: String
    fun new(scoreboardScreen: ScoreboardScreen, scene: Scene): Training
}

abstract class TrainingInfo(
    override val trainingName: String,
    override val description: String
) : TrainingInfoContainer