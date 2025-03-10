package net.spartanb312.everett.graphics.font.metrics

import net.spartanb312.everett.utils.math.ceilToInt
import net.spartanb312.everett.utils.math.toRadian
import java.awt.Font
import java.awt.GraphicsEnvironment
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import kotlin.math.tan

interface FontMetrics {
    val metricsHeight: Int
    val charHeight: Int
    val charAscent: Int
    fun charWidth(char: Char): Int
    fun charRenderWidth(char: Char): Int
}

class DummyFontMetrics(
    val font: Font,
    val fractionalMetrics: Boolean,
    val antiAlias: Boolean,
    val italicAngleDegree: Float
) : FontMetrics {

    val metrics = GraphicsEnvironment.getLocalGraphicsEnvironment().createGraphics(
        BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
    ).also {
        it.font = font
        it.setRenderingHint(
            RenderingHints.KEY_FRACTIONALMETRICS,
            if (fractionalMetrics) RenderingHints.VALUE_FRACTIONALMETRICS_ON
            else RenderingHints.VALUE_FRACTIONALMETRICS_OFF
        )
        it.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            if (antiAlias) RenderingHints.VALUE_TEXT_ANTIALIAS_ON
            else RenderingHints.VALUE_TEXT_ANTIALIAS_OFF
        )
        it.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            if (antiAlias) RenderingHints.VALUE_ANTIALIAS_ON
            else RenderingHints.VALUE_ANTIALIAS_OFF
        )
    }.fontMetrics
    val italicAddon = if (font.isItalic) (metrics.height * tan(italicAngleDegree.toRadian())).ceilToInt() else 0
    override val metricsHeight = metrics.height
    override val charAscent = metrics.ascent

    override val charHeight = metrics.ascent + metrics.descent
    override fun charWidth(char: Char): Int {
        val charWidth = metrics.charWidth(char)
        return if (charWidth == 0) charHeight
        else charWidth
    }

    override fun charRenderWidth(char: Char): Int {
        return metrics.charWidth(char) + italicAddon
    }

}