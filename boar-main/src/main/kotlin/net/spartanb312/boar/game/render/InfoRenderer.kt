package net.spartanb312.boar.game.render

import com.soywiz.kds.iterators.fastForEachReverse
import net.spartanb312.boar.game.option.impls.ControlOption
import net.spartanb312.boar.game.option.impls.VideoOption
import net.spartanb312.boar.game.render.crosshair.CrosshairRenderer
import net.spartanb312.boar.graphics.Camera
import net.spartanb312.boar.graphics.RenderSystem
import net.spartanb312.boar.launch.Platform
import net.spartanb312.boar.utils.color.ColorRGB
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
            when (Camera.Default.yaw % 360) {
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
            val x1 = Formatter().format("%.2f", Camera.Default.cameraPos.x).toString()
            val y1 = Formatter().format("%.2f", Camera.Default.cameraPos.y).toString()
            val z1 = Formatter().format("%.2f", Camera.Default.cameraPos.z).toString()
            "XYZ&f [$x1, $y1, $z1]"
        }
    )

    private val rightUpInfo = mutableListOf(
        { "GPU:&f ${RenderSystem.compat.gpuName}" },
        { "Platform:&f ${Platform.getPlatform().getName()}" },
        { "Memory:&f ${RenderSystem.usedMemory}/${RenderSystem.totalMemory} MB" },
        { "TextureQueue:&f ${TextureManager.activeThread}/${TextureManager.totalThread}" },
        { "OpenGL:&f ${RenderSystem.compat.openGLVersion}" }
    )

    private val rightDownInfo = mutableListOf(
        { CrosshairRenderer.currentCrosshair.nameString }
    )

    fun render() {
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

}