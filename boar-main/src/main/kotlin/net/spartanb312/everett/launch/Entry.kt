package net.spartanb312.everett.launch

import net.spartanb312.everett.AimTrainer
import net.spartanb312.everett.graphics.RenderSystem
import java.io.File

// Only for debug
fun main() = Main.main(arrayOf("-DevMode"))

/**
 * The entry of the game
 */
object Entry {
    init {
        val graphicsAPI = File("launch_option.cfg").let {
            if (it.exists()) {
                val api = it.readLines().find { s -> s.startsWith("API=") }
                    ?.substringAfter("API=")
                when (api) {
                    RenderSystem.API.GL_210_COMPAT.saveName -> RenderSystem.API.GL_210_COMPAT
                    RenderSystem.API.GL_450_CORE.saveName -> RenderSystem.API.GL_450_CORE
                    RenderSystem.API.GL_460_CORE.saveName -> RenderSystem.API.GL_460_CORE
                    else -> {
                        LaunchLogger.info("Unsupported API: $api")
                        RenderSystem.API.GL_450_CORE
                    }
                }
            } else RenderSystem.API.GL_450_CORE
        }
        LaunchLogger.info("Attempting to use API: ${graphicsAPI.fullName} to launch")
        RenderSystem.launch(AimTrainer::class.java, 1920, 1080, graphicsAPI = graphicsAPI)
    }
}