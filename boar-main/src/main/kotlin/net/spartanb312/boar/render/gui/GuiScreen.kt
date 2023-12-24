package net.spartanb312.boar.render.gui

abstract class GuiScreen {
    abstract fun onRender(mouseX: Int, mouseY: Int)
    open fun onMouseClicked(mouseX: Int, mouseY: Int, button: Int) {}
    open fun onMouseReleased(mouseX: Int, mouseY: Int, button: Int) {}
    open fun onKeyTyped(keyCode: Int) {}
    open fun onKeyRepeating(keyCode: Int) {}
}