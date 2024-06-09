package net.spartanb312.everett.launch

import net.spartanb312.everett.AimTrainer
import net.spartanb312.everett.graphics.RenderSystem

// Only for debug
fun main() = Main.main(arrayOf("-DevMode"))

/**
 * The entry of the game
 */
object Entry {
    init {
        RenderSystem.launch(AimTrainer::class.java, 1920, 1080)
    }
}