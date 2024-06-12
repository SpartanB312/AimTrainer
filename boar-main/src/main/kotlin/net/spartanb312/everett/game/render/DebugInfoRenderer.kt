package net.spartanb312.everett.game.render

import net.spartanb312.everett.graphics.OpenGL
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.drawing.RenderUtils
import net.spartanb312.everett.launch.Platform
import net.spartanb312.everett.utils.color.ColorRGB
import org.lwjgl.opengl.GL11.glGetString

object DebugInfoRenderer {

    var enabled = false

    private val fps get() = RS.averageFPS
    private val gpuName = glGetString(OpenGL.GL_RENDERER) ?: ""
    private val glContextVersion = glGetString(OpenGL.GL_VERSION) ?: ""
    private val jreVersion = System.getProperty("java.version")
    private val x64 = !Platform.getPlatform().name.contains("32")
    private val system = System.getProperty("os.name")!! + " " +
            if (Platform.getPlatform().name.contains("ARM")) {
                if (x64) "ARM 64 Bit" else "ARM 32 Bit"
            } else if (x64) "64 Bit" else "32 Bit"

    private val debugInfos: MutableList<() -> String> = mutableListOf(
        { "OpenGL: $glContextVersion" },
        { "GPU: $gpuName" },
        { "System: $system" },
        { "Java: $jreVersion" },
        { "FPS: $fps" }
    )

    fun onRender() {
        if (!enabled) return
        var startY = 0f
        val backgroundColor = ColorRGB.GRAY.alpha(64)
        debugInfos.forEach {
            val str = it.invoke()
            RenderUtils.drawRect(
                0f,
                startY,
                FontRendererMain.getWidth(str),
                startY + FontRendererMain.getHeight().toInt() - 3,
                backgroundColor
            )
            FontRendererMain.drawString(str, 0f, startY, ColorRGB.WHITE)
            startY += FontRendererMain.getHeight().toInt() - 3
        }
    }

}