package net.spartanb312.everett.game.training

import net.spartanb312.everett.game.render.Background
import net.spartanb312.everett.game.render.FontRendererBig
import net.spartanb312.everett.game.render.gui.GuiScreen
import net.spartanb312.everett.game.render.gui.Render2DManager
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.drawing.RenderUtils
import net.spartanb312.everett.utils.color.ColorRGB
import org.lwjgl.glfw.GLFW

class SchulteGrid(private val size: Int) : GuiScreen() {

    private class Block(val num: Int, var state: Int) {
        var xRange: ClosedFloatingPointRange<Float> = 0f..0f
        var yRange: ClosedFloatingPointRange<Float> = 0f..0f
    }

    private val num = (1..size * size).shuffled().map { Block(it, 0) }
    private var current = 1

    override fun onRender(mouseX: Double, mouseY: Double) {
        val scale = RS.generalScale
        val blockSize = 1000f * scale / size
        val gap = 2f * scale
        Background.renderBackground(mouseX, mouseY)
        var startX = RS.centerXF - size * blockSize / 2f
        var startY = RS.centerYF - size * blockSize / 2f - blockSize
        var count = 0
        num.forEach {
            if (count % size == 0) {
                startY += blockSize
                startX = RS.centerXF - size * blockSize / 2f
            }

            it.xRange = startX..startX + blockSize
            it.yRange = startY..startY + blockSize

            RenderUtils.drawRect(
                startX + gap,
                startY + gap,
                startX + blockSize - gap,
                startY + blockSize - gap,
                when (it.state) {
                    0 -> ColorRGB.BLUE
                    1 -> ColorRGB.GREEN
                    else -> ColorRGB.RED
                }
            )
            FontRendererBig.drawCenteredString(
                it.num.toString(),
                startX + blockSize / 2f,
                startY + blockSize / 2f,
                scale = 10f * scale / size
            )
            startX += blockSize
            count++
        }
    }

    override fun onMouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
            num.forEach {
                if (mouseX.toFloat() in it.xRange && mouseY.toFloat() in it.yRange) {
                    if (it.state == 0) {
                        if (it.num == current) {
                            it.state = 1
                            current++
                        } else it.state = 2
                    } else if (it.state == 2) {
                        if (it.num == current) {
                            it.state = 1
                            current++
                        }
                    }
                    return true
                }
            }
        }
        return false
    }

    override fun onKeyTyped(keyCode: Int, modifier: Int): Boolean {
        if (keyCode == GLFW.GLFW_KEY_F4 || keyCode == GLFW.GLFW_KEY_ESCAPE) {
            Render2DManager.popScreen()
            return true
        }
        return false
    }

}