package net.spartanb312.everett.game.input

import net.spartanb312.everett.game.audio.BGMPlayer
import net.spartanb312.everett.game.input.interfaces.*
import net.spartanb312.everett.game.option.impls.VideoOption
import net.spartanb312.everett.game.render.crosshair.CrosshairRenderer
import net.spartanb312.everett.game.render.gui.Render2DManager
import net.spartanb312.everett.game.render.gui.impls.OptionScreen
import net.spartanb312.everett.game.training.SchulteGrid
import net.spartanb312.everett.utils.timing.Timer
import org.lwjgl.glfw.GLFW
import java.util.concurrent.CopyOnWriteArrayList

object InputManager {

    private val keyTypedListeners = CopyOnWriteArrayList<KeyTypedListener>()
    private val keyRepeatingListeners = CopyOnWriteArrayList<KeyRepeatingListener>()
    private val keyReleaseListeners = CopyOnWriteArrayList<KeyReleaseListener>()

    private val mouseClickListener = CopyOnWriteArrayList<MouseClickListener>()
    private val mouseReleaseListener = CopyOnWriteArrayList<MouseReleaseListener>()

    fun onKeyTyped(key: Int, modifier: Int) {
        if (Render2DManager.onKeyTyped(key, modifier)) return
        keyTypedListeners.forEach { if (it.onKeyTyped(key, modifier)) return }
        if (key == GLFW.GLFW_KEY_F2) Render2DManager.displayScreen(OptionScreen)
        if (key == GLFW.GLFW_KEY_F4) Render2DManager.displayScreen(SchulteGrid(5))
        if (key == GLFW.GLFW_KEY_F5) BGMPlayer.changeSound(BGMPlayer.nextBGM())
        if (key == GLFW.GLFW_KEY_F11) VideoOption.fullScreen = !VideoOption.fullScreen
    }

    fun onKeyRepeating(key: Int, modifier: Int) {
        Render2DManager.onKeyRepeating(key, modifier)
        keyRepeatingListeners.forEach { it.onKeyRepeating(key, modifier) }
    }

    fun onKeyReleased(key: Int, modifier: Int) {
        Render2DManager.onKeyReleased(key, modifier)
        keyReleaseListeners.forEach { it.onKeyReleased(key, modifier) }
    }

    fun onMouseClicked(mouseX: Int, mouseY: Int, button: Int) {
        if (Render2DManager.onMouseClicked(mouseX, mouseY, button)) return
        if (button == 0) CrosshairRenderer.onClick()
        mouseClickListener.forEach { if (it.onMouseClicked(mouseX, mouseY, button)) return }
    }

    fun onMouseReleased(mouseX: Int, mouseY: Int, button: Int) {
        Render2DManager.onMouseReleased(mouseX, mouseY, button)
        mouseReleaseListener.forEach { it.onMouseReleased(mouseX, mouseY, button) }
    }

    fun registerKeyTyped(listener: KeyTypedListener) = keyTypedListeners.add(listener)
    fun unregisterKeyTyped(listener: KeyTypedListener) = keyTypedListeners.remove(listener)

    fun registerKeyRepeating(listener: KeyRepeatingListener) = keyRepeatingListeners.add(listener)
    fun unregisterKeyRepeating(listener: KeyRepeatingListener) = keyRepeatingListeners.remove(listener)

    fun registerKeyRelease(listener: KeyReleaseListener) = keyReleaseListeners.add(listener)
    fun unregisterKeyRelease(listener: KeyReleaseListener) = keyReleaseListeners.remove(listener)

    fun registerMouseClick(listener: MouseClickListener) = mouseClickListener.add(listener)
    fun unregisterMouseClick(listener: MouseClickListener) = mouseClickListener.remove(listener)

    fun registerMouseRelease(listener: MouseReleaseListener) = mouseReleaseListener.add(listener)
    fun unregisterMouseRelease(listener: MouseReleaseListener) = mouseReleaseListener.remove(listener)

    private var scrollValue = 0
    private val scrollTimer = Timer()

    fun updateScroll(value: Int) {
        scrollValue = value
        scrollTimer.reset()
    }

    fun getScroll(): Int {
        if (scrollTimer.passed(50)) scrollValue = 0
        return scrollValue
    }

}