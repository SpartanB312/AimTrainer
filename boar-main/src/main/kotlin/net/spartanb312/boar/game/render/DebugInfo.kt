package net.spartanb312.boar.game.render

import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.graphics.drawing.RenderUtils
import net.spartanb312.boar.utils.color.ColorRGB

object DebugInfo {

    var enabled = false

    fun render2D() {
        if (!enabled) return
        RenderUtils.drawRect(0, 0, RS.width, RS.height, ColorRGB.BLACK.alpha(128))
    }

}