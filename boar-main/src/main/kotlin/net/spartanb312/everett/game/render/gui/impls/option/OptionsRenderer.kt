package net.spartanb312.everett.game.render.gui.impls.option

import net.spartanb312.everett.game.crosshair.Crosshairs
import net.spartanb312.everett.game.crosshair.impls.CrosshairCustom
import net.spartanb312.everett.game.crosshair.impls.gun.GunCrosshair
import net.spartanb312.everett.game.input.InputManager
import net.spartanb312.everett.game.input.interfaces.MouseClickListener
import net.spartanb312.everett.game.input.interfaces.MouseReleaseListener
import net.spartanb312.everett.game.option.Option
import net.spartanb312.everett.game.option.impls.CrosshairOption
import net.spartanb312.everett.game.option.impls.VideoOption
import net.spartanb312.everett.game.render.CrosshairRenderer
import net.spartanb312.everett.game.render.FontRendererBig
import net.spartanb312.everett.game.render.FontRendererMain
import net.spartanb312.everett.graphics.GLHelper.scissor
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.drawing.RenderUtils
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.config.setting.number.NumberSetting
import net.spartanb312.everett.utils.config.setting.primitive.BooleanSetting
import net.spartanb312.everett.utils.config.setting.primitive.EnumSetting
import net.spartanb312.everett.utils.math.ConvergeUtil.converge
import net.spartanb312.everett.utils.timing.Timer
import java.text.DecimalFormat

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

    private val format = DecimalFormat("0.0")

    fun onRender2D(mouseX: Double, mouseY: Double, scale: Float, alpha: Float) {
        val startX = RS.widthF * 0.07f
        var startY = RS.heightF * 0.22f + scrollOffset

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
            "${VideoOption.fovType}: ",
            rightStartX + scale * 16f,
            RS.heightF * 0.685f + scale * 10f,
            scale = scale * 0.7f,
            color = ColorRGB.WHITE.alpha((alpha * 0.8f).toInt())
        )
        val currentCrosshair = CrosshairRenderer.currentCrosshair.crosshair
        val addon = if (currentCrosshair is GunCrosshair && currentCrosshair.syncFOV) "[Synced]" else ""
        FontRendererBig.drawString(
            "${String.format("%.1f", VideoOption.actualFovValue)}$addon",
            rightStartX + scale * 16f + FontRendererBig.getWidth("${VideoOption.fovType}: ", scale * 0.7f),
            RS.heightF * 0.685f + scale * 15f,
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
        val centerY = RS.heightF * 0.7925f - width * 0.25f
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
        CrosshairRenderer.onRender(false, centerX, centerY, VideoOption.dfov, ColorRGB.WHITE.alpha(alpha.toInt()))

        // V-RAM
        val framebufferScale = (RS.widthF * RS.heightF) / (1920 * 1080) // 128MB
        val displayScale = (RS.displayWidthF * RS.displayHeightF) / (1920 * 1080) // 512MB
        val estimatedVRAMmb = framebufferScale * 128L + displayScale * 256L
        val vStartX = rightStartX + scale * 15f
        val vEndX = rightEndX - scale * 15f
        var vStartY = rightEndY - 35f * scale
        val rate = estimatedVRAMmb / RS.totalVRam.toFloat()
        RenderUtils.drawRect(
            vStartX,
            rightEndY - 35f * scale,
            rightEndX - scale * 15f,
            rightEndY - 15f * scale,
            ColorRGB.WHITE.alpha((0.4f * alpha).toInt())
        )
        val color = if (rate <= 0.7f) ColorRGB.WHITE else if (rate <= 0.9f) ColorRGB.YELLOW else ColorRGB.RED
        RenderUtils.drawRect(
            vStartX,
            rightEndY - 35f * scale,
            vStartX + (vEndX - vStartX) * rate.coerceIn(0f, 1f),
            rightEndY - 15f * scale,
            color.alpha((0.8f * alpha).toInt())
        )
        vStartY -= FontRendererMain.getHeight(scale)
        FontRendererMain.drawString(
            "Estimated VRAM",
            vStartX,
            vStartY,
            scale = scale * 0.8f,
            color = ColorRGB.WHITE.alpha((alpha * 0.8f).toInt())
        )
        val str = "${format.format(estimatedVRAMmb / 1024f)} GB / ${format.format(RS.totalVRam / 1024f)} GB"
        FontRendererMain.drawString(
            str,
            vEndX - FontRendererMain.getWidth(str, scale * 0.8f),
            vStartY,
            scale = scale * 0.8f,
            color = ColorRGB.WHITE.alpha((alpha * 0.8f).toInt())
        )

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