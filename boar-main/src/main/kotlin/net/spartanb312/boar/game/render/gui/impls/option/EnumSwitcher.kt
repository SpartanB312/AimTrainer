package net.spartanb312.boar.game.render.gui.impls.option

import net.spartanb312.boar.game.config.setting.primitive.EnumSetting
import net.spartanb312.boar.game.render.FontRendererBig
import net.spartanb312.boar.graphics.drawing.RenderUtils
import net.spartanb312.boar.utils.color.ColorRGB
import net.spartanb312.boar.utils.math.ConvergeUtil.converge
import net.spartanb312.boar.utils.misc.DisplayEnum
import net.spartanb312.boar.utils.timing.Timer

class EnumSwitcher<T : Enum<T>>(setting: EnumSetting<T>) : AbstractSettingComponent<T>(setting) {

    private var progress = 0f // 0-100 %
    private val animationTimer = Timer()

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

        animationTimer.passedAndReset(10) {
            progress = progress.converge(if (isHoovered) 100f else 0f, 0.1f)
        }

        //0.45 -> 0.85
        val moveProgress = (progress / 70f).coerceIn(0f, 1f)
        val switcherAlphaRate = ((progress - 70f) / 30f).coerceIn(0f, 1f)
        val switcherAlpha = alpha * switcherAlphaRate * if (isHoovered) 1f else 0.8f

        val value = setting.value
        val modeName = if (value is DisplayEnum) value.displayString else value.name

        val strWidth = FontRendererBig.getWidth(modeName, scale * 0.8f)
        val dockCenterX = x + this.width - scale * 20 - strWidth / 2f
        val targetCenterX = x + this.width * 0.65f
        val centerX = dockCenterX - (dockCenterX - targetCenterX) * moveProgress
        val centerY = y + this.height / 2f
        FontRendererBig.drawCenteredString(
            modeName,
            centerX,
            centerY,
            ColorRGB.WHITE.alpha((alpha * if (isHoovered) 1.0f else 0.6f).toInt()),
            scale * 0.8f
        )
        if (switcherAlpha > 0) {
            FontRendererBig.drawString(
                "<",
                x + this.width * 0.45f,
                y + scale * 6,
                ColorRGB.WHITE.alpha(switcherAlpha.toInt()),
                scale * 0.8f
            )
            FontRendererBig.drawString(
                ">",
                x + this.width * 0.825f,
                y + scale * 6,
                ColorRGB.WHITE.alpha(switcherAlpha.toInt()),
                scale * 0.8f
            )
        }
    }

    override fun onMouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (button == 0 && isHoovered(mouseX.toDouble(), mouseY.toDouble())) {
            val left = x + width * 0.2f
            val center = x + width * 0.6f
            val right = x + width
            if (mouseX.toFloat() in left..center) {
                (setting as EnumSetting).lastValue()
                return true
            } else if (mouseX.toFloat() in center..right) {
                (setting as EnumSetting).nextValue()
                return true
            }
        }
        return false
    }

}