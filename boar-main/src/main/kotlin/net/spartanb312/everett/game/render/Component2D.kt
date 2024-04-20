package net.spartanb312.everett.game.render

import kotlin.math.max
import kotlin.math.min

interface Component2D {

    var x: Float
    var y: Float
    var width: Float
    var height: Float

    fun onRender2D(mouseX: Double, mouseY: Double, alpha: Float) {
    }

    fun onKeyTyped(typedChar: Char, keyCode: Int) {
    }

    fun onMouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int): Boolean = false

    fun onMouseReleased(mouseX: Int, mouseY: Int, state: Int) {
    }

    fun onMouseMove(mouseX: Int, mouseY: Int, clickedMouseButton: Int, timeSinceLastClick: Long) {
    }

    fun isHoovered(mouseX: Int, mouseY: Int): Boolean {
        return mouseX >= min(x, x + width) && mouseX <= max(x, x + width)
                && mouseY >= min(y, y + height) && mouseY <= max(y, y + height)
    }

    fun isVisible(): Boolean = true

}