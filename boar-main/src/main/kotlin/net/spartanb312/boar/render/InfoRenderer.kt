package net.spartanb312.boar.render

import net.spartanb312.boar.graphics.RenderSystem
import net.spartanb312.boar.graphics.drawing.RenderUtils
import net.spartanb312.boar.utils.color.ColorRGB

object InfoRenderer {

    private val infos = listOf(
        { "FPS: ${RenderSystem.fps}" },
        { "OpenGL: ${RenderSystem.compat.glVersion}" }
    )

    fun render() {
        var startY = 0f
        infos.forEach {
            val str = it.invoke()
            val width = FontRendererMain.getWidth(str)
            RenderUtils.drawRect(0f, startY, width, startY + FontRendererMain.getHeight(), ColorRGB.GRAY.alpha(128))
            FontRendererMain.drawString(str, 0f, startY)
            startY += FontRendererMain.getHeight()
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