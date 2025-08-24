package net.spartanb312.everett.game.render

import com.soywiz.kds.iterators.fastForEachReverse
import net.spartanb312.everett.game.CameraImpl
import net.spartanb312.everett.game.option.impls.ControlOption
import net.spartanb312.everett.game.option.impls.VideoOption
import net.spartanb312.everett.graphics.RenderSystem
import net.spartanb312.everett.launch.Platform
import net.spartanb312.everett.utils.color.ColorRGB
import oshi.SystemInfo
import java.util.*

object InfoRenderer {

    private val leftUpInfo = mutableListOf(
        { "Boar Engine &f${RenderSystem.ENGINE_VERSION}" },
        { "FPS:&f ${RenderSystem.averageFPS}" },
        { "${VideoOption.fovType}:&f ${VideoOption.fov}" },
        { "Sens:&f ${ControlOption.sensitivity}" },
    )

    private val leftDownInfo = mutableListOf(
        {
            when (CameraImpl.yaw % 360) {
                in 0f..22.5f -> "East&f [+X]"
                in 22.5f..67.5f -> "South East&f [+X +Z]"
                in 67.5f..112.5f -> "South&f [+Z]"
                in 112.5f..157.5f -> "South West&f [-X +Z]"
                in 157.5f..202.5f -> "West&f [-X]"
                in 202.5f..247.5f -> "North West&f [-X -Z]"
                in 247.5f..292.5f -> "North&f [-Z]"
                in 292.5f..337.5f -> "North East&f [+X -Z]"
                else -> "East&f [+X]"
            }
        },
        {
            val x1 = Formatter().format("%.2f", CameraImpl.cameraPos.x).toString()
            val y1 = Formatter().format("%.2f", CameraImpl.cameraPos.y).toString()
            val z1 = Formatter().format("%.2f", CameraImpl.cameraPos.z).toString()
            "XYZ&f [$x1, $y1, $z1]"
        }
    )

    private val rightUpInfo = mutableListOf(
        { "GPU:&f ${RenderSystem.compat.gpuName}" },
        { "CPU:&f ${Runtime.getRuntime().availableProcessors()}x$cpuName" },
        { "Platform:&f ${Platform.getPlatform().platformName}" },
        { "Memory:&f ${RenderSystem.usedMemory}/${RenderSystem.totalMemory} MB" },
        { "TextureQueue:&f ${TextureManager.activeThread}/${TextureManager.totalThread}" },
        { "OpenGL:&f ${RenderSystem.compat.openGLVersion}" }
    )

    private val rightDownInfo = mutableListOf(
        { CrosshairRenderer.currentCrosshair.nameString }
    )

    fun renderInfo() {
        var startY = 0f
        val color = ColorRGB.AQUA
        leftUpInfo.forEach {
            val str = it.invoke()
            FontRendererMain.drawStringWithShadow(str, 0f, startY, color)
            startY += FontRendererMain.getHeight()
        }
        startY = 0f
        rightUpInfo.forEach {
            val str = it.invoke()
            val width = FontRendererMain.getWidth(str)
            FontRendererMain.drawStringWithShadow(str, RenderSystem.widthF - width, startY, color)
            startY += FontRendererMain.getHeight()
        }
        startY = RenderSystem.heightF
        leftDownInfo.fastForEachReverse {
            val str = it.invoke()
            startY -= FontRendererMain.getHeight()
            FontRendererMain.drawStringWithShadow(str, 0f, startY, color)
        }
        startY = RenderSystem.heightF
        rightDownInfo.fastForEachReverse {
            val str = it.invoke()
            val width = FontRendererMain.getWidth(str)
            startY -= FontRendererMain.getHeight()
            FontRendererMain.drawStringWithShadow(str, RenderSystem.widthF - width, startY, color)
        }
    }
    /*
    RenderUtils.drawGradientRect(50, 50, 100, 100, ColorRGB.BLUE, ColorRGB.RED, ColorRGB.RED, ColorRGB.BLUE)
            bro.drawImage(0f, 0f, 0.6f)
            FontRendererMain.drawGradientStringWithShadow(
                "We are brothers!",
                100f, 150f,
                colors = arrayOf(ColorRGB.RED, ColorRGB.GOLD, ColorRGB.YELLOW, ColorRGB.GREEN, ColorRGB.AQUA),
                scale = 2f
            )
            FontRendererMain.drawColoredString("Everett & Andy",100f,230f,2f, shadowDepth = 1f)
            FontRendererMain.drawString("Thank you RED Maynard",10,1040)
            tex.drawTexture(100f, 600f,400f,900f)
     */

    fun getCpuName(isWindows: Boolean = true): String {
        return try {
            val process = if (isWindows) Runtime.getRuntime().exec("wmic cpu get name")
            else Runtime.getRuntime().exec("sysctl machdep.cpu.brand_string")
            process.outputStream.close()
            val scanner = Scanner(process.inputStream)
            var cpuName = ""
            while (scanner.hasNext()) {
                cpuName += scanner.next() + " "
            }
            cpuName.removeSuffix(" ").removePrefix("Name ")
        } catch (_: Exception) {
            "Unknown processor"
        }
    }

    val cpuName = try {
        val aprocessor = SystemInfo().hardware.processors
        String.format("%dx %s", aprocessor.size, aprocessor[0]).replace("\\s+".toRegex(), " ")
    } catch (_: Exception) {
        getCpuName(System.getProperty("os.name").startsWith("Windows"))
    }

}