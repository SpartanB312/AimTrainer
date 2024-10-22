package net.spartanb312.everett.game.render.gui.impls.option

import net.spartanb312.everett.game.Language.lang
import net.spartanb312.everett.game.render.FontRendererBig
import net.spartanb312.everett.graphics.drawing.RenderUtils
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.config.setting.primitive.BooleanSetting

class BooleanButton(setting: BooleanSetting) : AbstractSettingComponent<Boolean>(setting) {

    private val on by "ON".lang("开", "開")
    private val off by "OFF".lang("关", "關")

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
            setting.displayName,
            x + scale * 20,
            y + scale * 6,
            ColorRGB.WHITE.alpha((alpha * if (isHoovered) 1.0f else 0.6f).toInt()),
            scale * 0.8f
        )
        val state = if (setting.value) on else off
        FontRendererBig.drawString(
            state,
            x + this.width - scale * 20 - FontRendererBig.getWidth(state, scale * 0.8f),
            y + scale * 6,
            ColorRGB.WHITE.alpha((alpha * if (isHoovered) 1.0f else 0.6f).toInt()),
            scale * 0.8f
        )
    }

    override fun onMouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (isHoovered(mouseX.toDouble(), mouseY.toDouble())) {
            if (button == 0) {
                setting.value = !setting.value
                return true
            } else if (button == 1) {
                setting.value = setting.defaultValue
                return true
            }
        }
        return false
    }

}