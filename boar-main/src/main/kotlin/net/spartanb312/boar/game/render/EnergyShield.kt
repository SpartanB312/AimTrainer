package net.spartanb312.boar.game.render

import net.spartanb312.boar.game.Player
import net.spartanb312.boar.graphics.GLHelper.glMatrixScope
import net.spartanb312.boar.graphics.GLHelper.scissor
import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.graphics.drawing.RenderUtils
import net.spartanb312.boar.graphics.drawing.VertexBuffer.buffer
import net.spartanb312.boar.graphics.drawing.VertexFormat
import net.spartanb312.boar.utils.color.ColorRGB
import net.spartanb312.boar.utils.math.ConvergeUtil.converge
import net.spartanb312.boar.utils.math.ceilToInt
import net.spartanb312.boar.utils.math.floorToInt
import net.spartanb312.boar.utils.math.vector.Vec2f
import net.spartanb312.boar.utils.timing.Timer
import org.lwjgl.opengl.GL11
import kotlin.math.abs
import kotlin.math.max

object EnergyShield {

    private val generalColor = ColorRGB(101, 176, 210)
    private val generalLightColor = ColorRGB(194, 247, 254)

    // 0f..100f
    private var currentHealthRate = 0f
    private var currentAbsRate = 0f
    private val updateTimer = Timer()

    fun render2D() = glMatrixScope {
        val rate = max(RS.widthScale, RS.heightScale)
        val scale = 3f * rate
        val w = RS.widthF / 2f
        val h = 40f * scale
        GL11.glTranslatef(w, h, 0f)
        GL11.glScalef(scale, scale, scale)

        renderShield(scale)
        renderOutline()

        GL11.glScalef(1f / scale, 1f / scale, 1f / scale)
        GL11.glTranslatef(-w, -h, 0f)
    }

    private fun renderShield(scale: Float) {
        renderCompass(scale)
        updateTimer.passedAndRun(16) {
            val targetHealthRate = (16 / 20f * 100f).coerceIn(0f..100f)
            val targetAbsRate = (0f / 16f * 100f).coerceIn(0f..100f)
            currentHealthRate = currentHealthRate.converge(targetHealthRate, 0.1f)
            currentAbsRate = currentAbsRate.converge(targetAbsRate, 0.1f)
        }
        val w = RS.widthD / 2f
        // health
        val startX = (w - 80 * scale).floorToInt()
        val healthBound = startX + (160 * scale * (currentHealthRate / 100f)).ceilToInt()
        //RenderUtils.scissor(startX, 0, healthBound, 1000, ) {
        GL11.GL_TRIANGLE_FAN.buffer(VertexFormat.Pos2fColor, 10) {
            shieldOutline.forEachIndexed { index, it ->
                val light = generalLightColor
                val dark = generalColor
                when (index) {
                    0 -> v2fc(it.x, it.y, light.alpha(192))
                    5 -> v2fc(it.x, it.y, light.mix(dark, 0.9f).alpha(192))
                    else -> v2fc(it.x, it.y, dark.alpha(192))
                }
            }
        }
        //}
        RenderUtils.drawLinesLoop(shieldOutline, 1.5f, generalColor.alpha(192))

        // absorption
        //val absBound = startX + (160 * scale * (currentAbsRate / 100f)).ceilToInt()
        ////RenderUtils.scissor(startX, 0, absBound, 1000, event.res) {
        //    GL11.GL_TRIANGLE_FAN.buffer(VertexFormat.Pos2fColor, 10) {
        //        shieldOutline.forEachIndexed { index, it ->
        //            val light = ColorRGB(246, 237, 96)
        //            val dark = ColorRGB(242, 218, 67)
        //            when (index) {
        //                0 -> v2fc(it.x, it.y, light.alpha(128))
        //                5 -> v2fc(it.x, it.y, light.mix(dark, 0.9f).alpha(128))
        //                else -> v2fc(it.x, it.y, dark.alpha(128))
        //            }
        //        }
        //    }
        //    RenderUtils.drawLinesLoop(shieldOutline, 1.5f, ColorRGB(242, 218, 67).alpha(192)) // included
        ////}
    }

    private fun renderCompass(scale: Float) {
        val w = RS.centerXF
        // 140 pixels -> 100 angle
        val offsetAngle = Player.yaw % 360
        scissor((w - 70 * scale).floorToInt(), 0, (w + 70 * scale).ceilToInt(), 1000) {
            angles.forEachIndexed { index, it ->
                val angle = index * 15
                val actualX = ((-offsetAngle + angle + 50) % 360) * 1.4f - 70
                val renderX = if (actualX > 70) actualX - 504 else if (actualX < -70) actualX + 504 else actualX
                val offsetRate = (abs(renderX) / 90f).coerceIn(0f..1f)
                val offsetY = offsetRate * -1f
                val alpha = (255 - offsetRate * 128).toInt()
                if (it.second == 2) {
                    RenderUtils.drawLine(
                        Vec2f(renderX, -5f + offsetY),
                        Vec2f(renderX, -2f + offsetY),
                        2f,
                        generalLightColor.alpha(alpha)
                    )
                } else FontRendererMain.drawCenteredString(
                    it.first,
                    renderX,
                    -5f + offsetY,
                    generalLightColor.alpha(alpha),
                    scale = 0.27f
                )
            }
        }
    }

    private fun renderOutline() {
        // left outline
        val lineWidth = 2f
        RenderUtils.drawLine(
            Vec2f(95f, 7.5f),
            Vec2f(140f, 5.5f),
            lineWidth,
            generalColor.alpha(160),
            generalColor.alpha(128)
        )
        RenderUtils.drawLine(
            Vec2f(140f, 5.5f),
            Vec2f(200f, 2f),
            lineWidth,
            generalColor.alpha(128),
            generalColor.alpha(0)
        )

        // right outline
        RenderUtils.drawLine(
            Vec2f(-95f, 7.5f),
            Vec2f(-140f, 5.5f),
            lineWidth,
            generalColor.alpha(160),
            generalColor.alpha(128)
        )
        RenderUtils.drawLine(
            Vec2f(-140f, 5.5f),
            Vec2f(-200f, 2f),
            lineWidth,
            generalColor.alpha(128),
            generalColor.alpha(0)
        )
    }

    private val shieldOutline: Array<Vec2f> = arrayOf(
        Vec2f(0f, 1f),

        Vec2f(-75f, 0f),
        Vec2f(-80f, 5f),
        Vec2f(-77f, 8f),

        Vec2f(-50f, 9f),
        Vec2f(0f, 9.5f),
        Vec2f(50f, 9f),

        Vec2f(77f, 8f),
        Vec2f(80f, 5f),
        Vec2f(75f, 0f)
    )

    private val angles = mutableListOf(
        Pair("S", 1),
        Pair("15", 2),
        Pair("30", 2),
        Pair("SW", 3),
        Pair("60", 2),
        Pair("75", 2),
        Pair("W", 1),
        Pair("105", 2),
        Pair("120", 2),
        Pair("NW", 3),
        Pair("150", 2),
        Pair("165", 2),
        Pair("N", 1),
        Pair("195", 2),
        Pair("210", 2),
        Pair("NE", 3),
        Pair("240", 2),
        Pair("255", 2),
        Pair("E", 1),
        Pair("285", 2),
        Pair("300", 2),
        Pair("SE", 3),
        Pair("330", 2),
        Pair("345", 2)
    ) // name ,type, each for 15°
}