package net.spartanb312.boar.game.render

import net.spartanb312.boar.game.Player
import net.spartanb312.boar.game.config.Configurable
import net.spartanb312.boar.game.config.setting.alias
import net.spartanb312.boar.game.config.setting.m
import net.spartanb312.boar.game.config.setting.whenTrue
import net.spartanb312.boar.game.entity.Ball
import net.spartanb312.boar.game.entity.EntityPlayer
import net.spartanb312.boar.game.option.impls.VideoOption
import net.spartanb312.boar.game.render.scene.SceneManager
import net.spartanb312.boar.graphics.GLHelper.glMatrixScope
import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.graphics.drawing.RenderUtils
import net.spartanb312.boar.utils.color.ColorRGB
import net.spartanb312.boar.utils.math.MathUtils.mul
import net.spartanb312.boar.utils.math.MathUtils.v2hFOV
import net.spartanb312.boar.utils.math.toRadian
import net.spartanb312.boar.utils.math.vector.Vec2f
import net.spartanb312.boar.utils.math.vector.Vec3f
import net.spartanb312.boar.utils.math.vector.distanceTo
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11
import kotlin.math.abs
import kotlin.math.max

object Radar : Configurable("Radar") {

    private var generalScale by setting("Radar-Scale", 1f, 0.1f..5.0f, 0.1f).alias("Radar Scale")
        .m("雷达大小", "雷達尺寸")
        .whenTrue(VideoOption.radar)
    private var radarRange by setting("Radar-Range", 75, 1..200, 1).alias("Radar Range")
        .m("雷达范围", "雷達範圍")
        .whenTrue(VideoOption.radar)

    private val arc1Vertices = RenderUtils.getArcVertices(0f, 0f, 50f, 0f..360f).reversed().toTypedArray()
    private val arc2Vertices = RenderUtils.getArcVertices(0f, 0f, 25f, 0f..360f).reversed().toTypedArray()
    private val centerVertices = RenderUtils.getArcVertices(0f, 0f, 7f, 0f..360f).reversed().toTypedArray()
    private val generalColor = ColorRGB(101, 176, 210)
    private val lightColor = ColorRGB(194, 247, 254)

    fun render2D() = glMatrixScope {
        val scale = generalScale * max(RS.widthScale, RS.heightScale) * 2f
        val h = RS.heightF - 70f * scale
        GL11.glTranslatef(70f * scale, h, 0f)
        GL11.glScalef(scale, scale, scale)

        RenderUtils.drawTriangleFan(0.0, 0.0, arc1Vertices, generalColor.alpha(64))
        RenderUtils.drawArcOutline(arc1Vertices, 2f, lightColor.alpha(128))
        RenderUtils.drawArcOutline(arc2Vertices, 2f, lightColor.alpha(128))

        // pulse
        val time = System.currentTimeMillis() % 3000
        val rate = (time / 1000f).coerceIn(0f..1f)
        val pulseRadius = 7 + 43 * rate
        val alpha = when (time) {
            in 0..1000 -> 64
            in 1000..2000 -> ((1 - (time - 1000) / 1000f) * 64).toInt()
            else -> 0
        }
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        RenderUtils.drawArc(
            Vec2f.ZERO,
            pulseRadius,
            0f..360f,
            color = generalColor.alpha(alpha / 2)
        )
        RenderUtils.drawArcOutline(
            Vec2f.ZERO,
            pulseRadius,
            0f..360f,
            lineWidth = 5f,
            color = generalColor.alpha(alpha)
        )

        // cross
        val angle = (Player.yaw + 360 + 90) % 360
        GL11.glRotatef(-angle, 0f, 0f, 1f)
        RenderUtils.drawLine(Vec2f(0f, 50f), Vec2f(0f, -50f), 1.5f, lightColor.alpha(96))
        RenderUtils.drawLine(Vec2f(50f, 0f), Vec2f(-50f, 0f), 1.5f, lightColor.alpha(96))
        GL11.glRotatef(angle, 0f, 0f, 1f)

        // fov
        val fovAngle = VideoOption.fov.v2hFOV(RS.aspectD) / 2
        RenderUtils.drawArc(Vec2f.ZERO, 50f, -fovAngle..fovAngle, color = generalColor.alpha(96))

        // center
        RenderUtils.drawTriangleFan(0.0, 0.0, centerVertices, lightColor, generalColor)
        RenderUtils.drawArcOutline(centerVertices, 2f, generalColor)

        // entities
        val playerVec = Vec2f(Player.pos.x, Player.pos.z)
        val rotateMat = Matrix4f().rotate(-angle.toRadian(), 0f, 0f, 1f)
        for (it in SceneManager.currentScene.entities) {
            if (it == Player) continue
            val color = when (it) {
                is Ball -> ColorRGB(255, 30, 0)
                is EntityPlayer -> lightColor
                else -> continue
            }
            val offsetVec = (Vec2f(it.pos.x, it.pos.z) - playerVec) * (50f / radarRange)
            val distance = offsetVec.distanceTo(Vec2f.ZERO)
            if (distance > 47.5) continue
            val highLight = (rate < 1) && (abs(distance - pulseRadius) < 5f)
            val highLightAlpha = 255 - 95 * abs(distance - pulseRadius) / 5f
            val renderPos = Vec3f(offsetVec.x, offsetVec.y, 0f).mul(rotateMat)
            val renderColor = color.alpha(if (highLight) highLightAlpha.toInt() else 160)
            RenderUtils.drawPoint(
                renderPos.x,
                renderPos.y,
                5f * scale,
                renderColor
            )

            val hOffset = 2.5f
            val vOffset = 2.5f
            if (it.pos.y >= Player.pos.y) {
                RenderUtils.drawLine(
                    renderPos.x, renderPos.y - vOffset * 2f,
                    renderPos.x - hOffset * 1f, renderPos.y - vOffset * 1f,
                    1f * scale,
                    renderColor
                )
                RenderUtils.drawLine(
                    renderPos.x, renderPos.y - vOffset * 2f,
                    renderPos.x + hOffset * 1f, renderPos.y - vOffset * 1f,
                    1f * scale,
                    renderColor
                )
            } else {
                RenderUtils.drawLine(
                    renderPos.x, renderPos.y + vOffset * 2f,
                    renderPos.x - hOffset * 1f, renderPos.y + vOffset * 1f,
                    1f * scale,
                    renderColor
                )
                RenderUtils.drawLine(
                    renderPos.x, renderPos.y + vOffset * 2f,
                    renderPos.x + hOffset * 1f, renderPos.y + vOffset * 1f,
                    1f * scale,
                    renderColor
                )
            }
        }
    }


}