package net.spartanb312.boar.game.render.scene

import net.spartanb312.boar.game.input.interfaces.*
import net.spartanb312.boar.game.render.gui.SubscribedRenderer

abstract class Scene :
    KeyReleaseListener,
    KeyRepeatingListener,
    KeyTypedListener,
    MouseClickListener,
    MouseReleaseListener,
    SubscribedRenderer {
    abstract fun onRender()
    open fun onInit() {}
    override fun onMouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean = false
    override fun onMouseReleased(mouseX: Int, mouseY: Int, button: Int) {}
    override fun onKeyTyped(keyCode: Int, modifier: Int): Boolean = false
    override fun onKeyRepeating(key: Int, modifier: Int) {}
    override fun onKeyReleased(key: Int, modifier: Int) {}
}