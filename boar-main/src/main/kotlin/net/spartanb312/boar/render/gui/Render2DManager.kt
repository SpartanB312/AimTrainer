package net.spartanb312.boar.render.gui

import net.spartanb312.boar.graphics.GLHelper
import net.spartanb312.boar.graphics.RenderSystem
import org.lwjgl.glfw.GLFW
import java.util.*

object Render2DManager {

    private val screenStack = Stack<GuiScreen>()
    val currenScreen: GuiScreen? get() = if (screenStack.isEmpty()) null else screenStack.peek()

    fun onRender(mouseX: Int, mouseY: Int) {
        val currentScreen = currenScreen
        if (currentScreen != null) currentScreen.onRender(mouseX, mouseY)
        else GLHelper.mouseMode(RenderSystem.window, GLFW.GLFW_CURSOR_DISABLED)
    }

    fun displayScreen(screen: GuiScreen) = screenStack.push(screen)
    fun popScreen() = screenStack.pop()

}