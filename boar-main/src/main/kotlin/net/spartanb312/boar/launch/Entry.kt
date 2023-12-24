package net.spartanb312.boar.launch

import net.spartanb312.boar.Boar
import net.spartanb312.boar.graphics.RenderSystem

// Only for debug
fun main() = Main.main(arrayOf("-DevMode"))

/**
 * The entry of the game
 */
object Entry {
    init {
        RenderSystem.launch(Boar::class.java, 1920, 1080, "Test")
    }
}