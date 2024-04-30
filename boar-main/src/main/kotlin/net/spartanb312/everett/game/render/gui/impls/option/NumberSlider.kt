package net.spartanb312.everett.game.render.gui.impls.option

import net.spartanb312.everett.utils.config.setting.number.NumberSetting
import net.spartanb312.everett.game.render.FontRendererBig
import net.spartanb312.everett.graphics.drawing.RenderUtils
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.math.ConvergeUtil.converge
import net.spartanb312.everett.utils.timing.Timer

class NumberSlider<T>(
    setting: NumberSetting<T>
) : AbstractSettingComponent<T>(setting) where T : Number, T : Comparable<T> {

    private val animationTimer = Timer()
    private val slideTimer = Timer()
    private var sliding = false
    private var currentRate = 0f
    private var animatedAlphaRate = 0f

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

        //value slider
        val sliderStartX = x + this.width * 0.45f // 0.45 -> 0.85
        val sliderWidth = this.width * 0.4f
        val sliderEndX = sliderStartX + sliderWidth
        val setting = this.setting as NumberSetting

        if (sliding) setting.setByPercent(((mouseX.toFloat() - sliderStartX) / sliderWidth).coerceIn(0.0f..1.0f))

        animationTimer.passedAndReset(10) {
            val percent = setting.getPercentBar()
            currentRate = (currentRate * 100f).converge(percent * 100f, 0.2f) / 100f
            animatedAlphaRate = (animatedAlphaRate * 100f).converge(if (isHoovered) 100f else 0f, 0.1f) / 100f
        }

        if (sliding && slideTimer.passed(150)) currentRate = setting.getPercentBar()

        val boundLeft = sliderStartX + currentRate * sliderWidth - (if (isHoovered) this.height * 0.2f else 0f)
        val boundRight = sliderStartX + currentRate * sliderWidth + (if (isHoovered) this.height * 0.2f else 0f)
        if (boundLeft > sliderStartX) RenderUtils.drawRect(
            sliderStartX,
            y + this.height * 0.47f,
            boundLeft,
            y + this.height * 0.53f,
            ColorRGB.WHITE.alpha(alpha.toInt())
        )
        if (boundRight < sliderEndX) RenderUtils.drawRect(
            boundRight,
            y + this.height * 0.47f,
            sliderEndX,
            y + this.height * 0.53f,
            ColorRGB.WHITE.alpha((alpha * 0.4f).toInt()),
        )
        if (isHoovered) RenderUtils.drawRect(
            boundLeft,
            y + this.height * 0.1f,
            boundRight,
            y + this.height * 0.9f,
            ColorRGB.WHITE.alpha((alpha * animatedAlphaRate).toInt())
        )
        //display value
        val str = setting.getDisplay(currentRate)
        FontRendererBig.drawString(
            str,
            x + this.width - scale * 20 - FontRendererBig.getWidth(str, scale * 0.8f),
            y + scale * 6,
            ColorRGB.WHITE.alpha((alpha * if (isHoovered) 1.0f else 0.6f).toInt()),
            scale * 0.8f
        )
    }

    override fun onMouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (button == 0 && isHoovered(mouseX.toDouble(), mouseY.toDouble())) {
            slideTimer.reset()
            sliding = true
            return true
        }
        return false
    }

    override fun onMouseReleased(mouseX: Int, mouseY: Int, button: Int) {
        if (button == 0) sliding = false
    }

    override fun reset() {
        animationTimer.reset()
        currentRate = 0f
        sliding = false
    }

}