package net.spartanb312.everett.game.render.gui

import net.spartanb312.everett.game.option.impls.VideoOption
import net.spartanb312.everett.game.render.hud.impls.EnergyShield
import net.spartanb312.everett.game.render.hud.impls.Radar
import net.spartanb312.everett.game.render.scene.SceneManager
import net.spartanb312.everett.graphics.GLHelper
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.RenderSystem
import org.lwjgl.glfw.GLFW
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

object Render2DManager {

    private val screenStack = Stack<GuiScreen>()
    private val subscribedRenderers = CopyOnWriteArrayList<SubscribedRenderer>()
    val currentScreen: GuiScreen? get() = if (screenStack.isEmpty()) null else screenStack.peek()
    val updateCamera get() = screenStack.isEmpty()
    val size get() = screenStack.size
    val displaying get() = screenStack.isNotEmpty()

    fun displayScreen(screen: GuiScreen) {
        RenderSystem.checkRenderThread()
        if (screenStack.contains(screen)) return
        if (screenStack.isEmpty()) GLHelper.mouseMode(GLFW.GLFW_CURSOR_NORMAL)
        screenStack.push(screen)
        screen.onInit()
    }

    fun popScreen() = screenStack.pop()?.onClosed()
    fun closeAll() {
        while (screenStack.isNotEmpty()) {
            screenStack.pop()?.onClosed()
        }
    }

    fun onTick() {
        RS.rto = currentScreen != null
    }

    fun onRender(mouseX: Double, mouseY: Double) {
        if (SceneManager.inTraining) {
            if (VideoOption.radar.value) Radar.render2D()
            if (VideoOption.shield) EnergyShield.render2D()
        }
        subscribedRenderers.forEach { it.render2D() }
        val currentScreen = currentScreen
        if (currentScreen != null) {
            currentScreen.onRender(mouseX, mouseY)
        } else RenderSystem.addRenderThreadJob { GLHelper.mouseMode(GLFW.GLFW_CURSOR_DISABLED) }
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

    fun subscribe(subscribedRenderer: SubscribedRenderer) = subscribedRenderers.add(subscribedRenderer)
    fun unsubscribe(subscribedRenderer: SubscribedRenderer) = subscribedRenderers.remove(subscribedRenderer)

}