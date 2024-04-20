package net.spartanb312.everett.game.render.gui

import net.spartanb312.everett.game.input.interfaces.*

abstract class GuiScreen :
    KeyReleaseListener,
    KeyRepeatingListener,
    KeyTypedListener,
    MouseClickListener,
    MouseReleaseListener {
    abstract fun onRender(mouseX: Double, mouseY: Double)
    open fun onInit() {}
    open fun onClosed() {}
    override fun onMouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean = false
    override fun onMouseReleased(mouseX: Int, mouseY: Int, button: Int) {}
    override fun onKeyTyped(keyCode: Int, modifier: Int): Boolean = false
    override fun onKeyRepeating(key: Int, modifier: Int) {}
    override fun onKeyReleased(key: Int, modifier: Int) {}
}