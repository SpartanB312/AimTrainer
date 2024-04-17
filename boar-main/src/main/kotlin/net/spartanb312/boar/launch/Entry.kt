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
                    RenderSystem.GL.GL_210.displayName -> RenderSystem.GL.GL_210
                    RenderSystem.GL.GL_450.displayName -> RenderSystem.GL.GL_450
                    RenderSystem.GL.Vulkan.displayName -> RenderSystem.GL.Vulkan
                    else -> {
                        Logger.info("Unsupported API: $api")
                        RenderSystem.GL.GL_210
                    }
                }
            } else RenderSystem.GL.GL_210
        }
        RenderSystem.launch(AimTrainer::class.java, 1920, 1080, glVersion = ogl)
    }
}