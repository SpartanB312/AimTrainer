package net.spartanb312.boar.game.render.gui.impls.option

import net.spartanb312.boar.game.config.setting.primitive.BooleanSetting
import net.spartanb312.boar.game.render.FontRendererBig
import net.spartanb312.boar.graphics.drawing.RenderUtils
import net.spartanb312.boar.utils.color.ColorRGB

class BooleanButton(setting: BooleanSetting) : AbstractSettingComponent<Boolean>(setting) {

    override fun onRender2D(mouseX: Double, mouseY: Double, scale: Float, alpha: Float) {
        val isHoovered = isHoovered(mouseX, mouseY)
        RenderUtils.drawRect(
            x,
            y,
            x + this.width,
            y + this.height,
            if (isHoovered) ColorRGB.WHITE.alpha((alpha * 0.25f).toInt())
            else ColorRGB.WHITE.alpha((alpha * 0.1f).toInt())
        )
        FontRendererBig.drawString(
            setting.aliasName,
            x + scale * 20,
            y + scale * 6,
            ColorRGB.WHITE.alpha((alpha * if (isHoovered) 1.0f else 0.6f).toInt()),
            scale * 0.8f
        )
        val state = if (setting.value) "ON" else "OFF"
        FontRendererBig.drawString(
            state,
            x + this.width - scale * 20 - FontRendererBig.getWidth(state, scale * 0.8f),
            y + scale * 6,
            ColorRGB.WHITE.alpha((alpha * if (isHoovered) 1.0f else 0.6f).toInt()),
            scale * 0.8f
        )
    }

    override fun onMouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (button == 0 && isHoovered(mouseX.toDouble(), mouseY.toDouble())) {
            setting.value = !setting.value
            return true
        }
        return false
    }

}