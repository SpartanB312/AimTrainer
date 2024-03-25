package net.spartanb312.boar.game.render.gui.impls.option

import net.spartanb312.boar.game.config.setting.number.NumberSetting
import net.spartanb312.boar.game.config.setting.primitive.BooleanSetting
import net.spartanb312.boar.game.config.setting.primitive.EnumSetting
import net.spartanb312.boar.game.input.InputManager
import net.spartanb312.boar.game.input.interfaces.MouseClickListener
import net.spartanb312.boar.game.input.interfaces.MouseReleaseListener
import net.spartanb312.boar.game.option.Option
import net.spartanb312.boar.game.option.impls.CrosshairOption
import net.spartanb312.boar.game.option.impls.VideoOption
import net.spartanb312.boar.game.render.FontRendererBig
import net.spartanb312.boar.game.render.crosshair.CrosshairRenderer
import net.spartanb312.boar.game.render.crosshair.Crosshairs
import net.spartanb312.boar.game.render.crosshair.impls.CrosshairCustom
import net.spartanb312.boar.game.render.crosshair.impls.gun.GunCrosshair
import net.spartanb312.boar.graphics.GLHelper.glMatrixScope
import net.spartanb312.boar.graphics.GLHelper.scissor
import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.graphics.drawing.RenderUtils
import net.spartanb312.boar.graphics.matrix.mulToGL
import net.spartanb312.boar.graphics.matrix.translatef
import net.spartanb312.boar.utils.color.ColorRGB
import net.spartanb312.boar.utils.math.ConvergeUtil.converge
import net.spartanb312.boar.utils.timing.Timer

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

        glMatrixScope {
            val rightStartX = RS.widthF * 0.62f
            val rightEndX = RS.widthF * 0.93f
            val rightStartY = RS.heightF * 0.22f
            val rightEndY = RS.heightF * 0.9f
            // Description
            val bgColor = ColorRGB.WHITE.alpha((alpha * 0.1f).toInt())
            RenderUtils.drawRect(
                rightStartX, rightStartY,
                rightEndX, RS.heightF * 0.635f,
                bgColor
            )

            // Info
            FontRendererBig.drawString(
                "Crosshair Preview:",
                rightStartX + scale * 15f,
                RS.heightF * 0.685f + scale * 10f,
                scale = scale * 0.7f,
                color = ColorRGB.WHITE.alpha((alpha * 0.8f).toInt())
            )
            FontRendererBig.drawString(
                "${VideoOption.fovType}: ",
                rightStartX + scale * 15f,
                RS.heightF * 0.685f + scale * 10f + FontRendererBig.getHeight(scale * 0.9f),
                scale = scale * 0.7f,
                color = ColorRGB.WHITE.alpha((alpha * 0.8f).toInt())
            )
            val currentCrosshair = CrosshairRenderer.currentCrosshair.crosshair
            val addon = if (currentCrosshair is GunCrosshair && currentCrosshair.syncFOV) "[Synced]" else ""
            FontRendererBig.drawString(
                "${String.format("%.1f", VideoOption.actualFovValue)}$addon",
                rightStartX + scale * 15f + FontRendererBig.getWidth("${VideoOption.fovType}: ", scale * 0.7f),
                RS.heightF * 0.685f + scale * 10f + FontRendererBig.getHeight(scale),
                scale = scale * 0.5f,
                color = ColorRGB.WHITE.alpha((alpha * 0.6f).toInt())
            )
            RenderUtils.drawRect(
                rightStartX, RS.heightF * 0.685f,
                rightEndX, rightEndY,
                bgColor
            )

            // Crosshair
            val width = RS.heightF * 0.1075f
            val centerX = RS.widthF * 0.93f - width
            val centerY = RS.heightF * 0.7925f
            if (CrosshairCustom.calibrate.value && CrosshairOption.crosshairType.value == Crosshairs.Custom) {
                RenderUtils.drawLine(
                    centerX,
                    RS.heightF * 0.685f,
                    centerX,
                    RS.heightF * 0.9f,
                    2f * scale,
                    ColorRGB.RED.alpha(alpha.toInt())
                )
                RenderUtils.drawLine(
                    centerX - width,
                    centerY,
                    RS.widthF * 0.93f,
                    centerY,
                    2f * scale,
                    ColorRGB.RED.alpha(alpha.toInt())
                )
            }
            translatef(centerX, centerY, 0F).mulToGL()
            CrosshairRenderer.testRender(VideoOption.dfov, ColorRGB.WHITE.alpha(alpha.toInt()))
        }

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