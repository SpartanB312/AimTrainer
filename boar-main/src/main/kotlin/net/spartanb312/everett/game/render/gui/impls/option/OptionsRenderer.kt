package net.spartanb312.everett.game.render.gui.impls.option

import net.spartanb312.everett.game.input.InputManager
import net.spartanb312.everett.game.input.interfaces.MouseClickListener
import net.spartanb312.everett.game.input.interfaces.MouseReleaseListener
import net.spartanb312.everett.game.option.Option
import net.spartanb312.everett.game.render.FontRendererBig
import net.spartanb312.everett.graphics.GLHelper.scissor
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.utils.config.setting.number.NumberSetting
import net.spartanb312.everett.utils.config.setting.primitive.BooleanSetting
import net.spartanb312.everett.utils.config.setting.primitive.EnumSetting
import net.spartanb312.everett.utils.math.ConvergeUtil.converge
import net.spartanb312.everett.utils.timing.Timer

class OptionsRenderer(private val options: Option) : MouseClickListener, MouseReleaseListener {

    val buttons = sequence<AbstractSettingComponent<*>> {
        options.settings.forEach {
            when (it) {
                is BooleanSetting -> yield(BooleanButton(it))
                is EnumSetting -> yield(EnumSwitcher(it))
                is NumberSetting -> yield(NumberSlider(it))
            }
        }
    }.toList()

    fun onRender2D(mouseX: Double, mouseY: Double, scale: Float, alpha: Float) {
        val startX = RS.widthF * 0.07f
        var startY = RS.heightF * 0.22f + scrollOffset

        scissor(
            RS.widthF * 0.04f,
            RS.heightF * 0.18f,
            RS.widthF * 0.58f,
            RS.heightF * 0.9f
        ) {
            for (it in buttons) {
                if (!it.setting.isVisible) continue
                it.x = startX
                it.y = startY
                it.width = RS.widthF * 0.5f
                it.height = FontRendererBig.getHeight(scale)
                it.onRender2D(mouseX, mouseY, scale, alpha)
                startY += FontRendererBig.getHeight(scale) * 1.1f
            }
        }

        updateScrollBar(scale)
    }

    private val scrollUpdateTimer = Timer()
    var scrollOffset = 0
    var scrollAccumulation = 0

    private fun updateScrollBar(scale: Float) = scrollUpdateTimer.passedAndReset(10) {
        val addition = InputManager.getScroll()
        scrollAccumulation += (addition * 25 * scale).toInt()
        val renderHeight = RS.heightF * 0.68f
        val fontHeight = FontRendererBig.getHeight(scale)
        val visibleButtons = buttons.count { it.setting.isVisible }
        val maxHeight = fontHeight * (visibleButtons + (visibleButtons - 1).coerceAtLeast(0) * 0.1f)
        if (addition == 0) {
            if (scrollOffset > 0 || renderHeight > maxHeight) {
                scrollAccumulation = (scrollAccumulation.toFloat().converge(0.0f, 0.1f)).toInt()
            } else if (renderHeight < maxHeight && scrollOffset < 0 && renderHeight - maxHeight > scrollOffset) {
                scrollAccumulation = (scrollAccumulation.toFloat().converge(renderHeight - maxHeight, 0.1f)).toInt()
            }
        }
        scrollOffset = scrollOffset.toFloat().converge(scrollAccumulation.toFloat(), 0.2f).toInt()
    }

    override fun onMouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean {
        buttons.forEach {
            if (it.setting.isVisible && it.onMouseClicked(mouseX, mouseY, button)) return true
        }
        return false
    }

    override fun onMouseReleased(mouseX: Int, mouseY: Int, button: Int) {
        buttons.forEach {
            it.onMouseReleased(mouseX, mouseY, button)
        }
    }

}