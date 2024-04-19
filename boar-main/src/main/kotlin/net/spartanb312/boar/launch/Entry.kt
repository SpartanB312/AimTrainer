package net.spartanb312.boar.launch

import net.spartanb312.boar.AimTrainer
import net.spartanb312.boar.graphics.RenderSystem
import net.spartanb312.boar.utils.Logger
import java.io.File

// Only for debug
fun main() = Main.main(arrayOf("-DevMode"))

/**
 * The entry of the game
 */
object Entry {
    init {
        val ogl = File("launch_option.cfg").let {
            if (it.exists()) {
                val api = it.readLines().find { s -> s.startsWith("API=") }
                    ?.substringAfter("API=")
                when (api) {
                    RenderSystem.API.GL_210.displayName -> RenderSystem.API.GL_210
                    RenderSystem.API.GL_450.displayName -> RenderSystem.API.GL_450
                    RenderSystem.API.Vulkan.displayName -> RenderSystem.API.Vulkan
                    else -> {
                        Logger.info("Unsupported API: $api")
                        RenderSystem.API.GL_210
                    }
                }
            } else RenderSystem.API.GL_210
        }
        RenderSystem.launch(AimTrainer::class.java, 1920, 1080, APIVersion = ogl)
    }
}