package net.spartanb312.boar.launch

import net.spartanb312.boar.AimTrainer
import net.spartanb312.boar.graphics.RenderSystem

// Only for debug
fun main() = Main.main(arrayOf("-DevMode"))

/**
 * The entry of the game
 */
object Entry {
    init {
        RenderSystem.launch(AimTrainer::class.java, 1920, 1080, "Test")
    }
}