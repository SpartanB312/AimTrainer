package net.spartanb312.boar.game.render.gui

import net.spartanb312.boar.graphics.GLHelper
import net.spartanb312.boar.graphics.RenderSystem
import org.lwjgl.glfw.GLFW
import java.util.*

object Render2DManager {

    private val screenStack = Stack<GuiScreen>()
    val currentScreen: GuiScreen? get() = if (screenStack.isEmpty()) null else screenStack.peek()
    val updateCamera get() = screenStack.isEmpty()

    fun displayScreen(screen: GuiScreen) {
        RenderSystem.checkRenderThread()
        if (screenStack.contains(screen)) return
        screenStack.push(screen)
        screen.onInit()
    }

    fun popScreen() = screenStack.pop()

    fun onRender(mouseX: Double, mouseY: Double) {
        val currentScreen = currentScreen
        if (currentScreen != null) {
            GLHelper.mouseMode(GLFW.GLFW_CURSOR_NORMAL)
            currentScreen.onRender(mouseX, mouseY)
        } else GLHelper.mouseMode(GLFW.GLFW_CURSOR_DISABLED)
    }

    fun onKeyTyped(key: Int, modifier: Int): Boolean {
        return currentScreen?.onKeyTyped(key, modifier) ?: false
    }

    fun onKeyRepeating(key: Int, modifier: Int) {
        currentScreen?.onKeyRepeating(key, modifier)
    }

    fun onKeyReleased(key: Int, modifier: Int) {
        currentScreen?.onKeyReleased(key, modifier)
    }

    fun onMouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int): Boolean {
        return currentScreen?.onMouseClicked(mouseX, mouseY, mouseButton) ?: false
    }

    fun onMouseReleased(mouseX: Int, mouseY: Int, button: Int) {
        currentScreen?.onMouseReleased(mouseX, mouseY, button)
    }

}