package net.spartanb312.everett.game.render.crosshair.impls

import net.spartanb312.everett.game.config.setting.alias
import net.spartanb312.everett.game.config.setting.m
import net.spartanb312.everett.game.config.setting.whenTrue
import net.spartanb312.everett.game.render.TextureManager
import net.spartanb312.everett.game.render.crosshair.Crosshair
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.matrix.MatrixLayerStack
import net.spartanb312.everett.graphics.matrix.scalef
import net.spartanb312.everett.graphics.matrix.translatef
import net.spartanb312.everett.graphics.texture.drawTexture
import net.spartanb312.everett.utils.color.ColorRGB
import kotlin.math.min

object CrosshairCustom : Crosshair(0f) {

    private val size by setting("Custom-Size", 15f, 1f..50f, 0.5f)
        .alias("Size").m("准星大小", "準星大小")
    val calibrate = setting("Custom-Calibrate", false)
        .alias("Calibrate").m("校准准星", "準星校準")
    private val offsetX by setting("Custom-Offset X", 0.0, -5.0..5.0, 0.005)
        .alias("Offset X").m("X偏移", "X位移")
        .whenTrue(calibrate)
    private val offsetY by setting("Custom-Offset Y", 0.0, -5.0..5.0, 0.005)
        .alias("Offset Y").m("Y偏移", "Y位移")
        .whenTrue(calibrate)

    private val crosshair = TextureManager.lazyTexture("assets/CustomCrosshair.png")

    override var clickTime = System.currentTimeMillis()

    override fun MatrixLayerStack.MatrixScope.onRender(
        centerX: Float,
        centerY: Float,
        fov: Float,
        shadow: Boolean,
        colorRGB: ColorRGB
    ) {
        if (shadow) return
        val scaledSize = min(RS.widthScale, RS.heightScale) * size
        val cx = centerX + offsetX.toFloat()
        val cy = centerY + offsetY.toFloat()
        translatef(cx, cy, 0.0f)
        scalef(scaledSize, scaledSize, 1.0f)
        translatef(-cx, -cy, 0.0f)
        crosshair.drawTexture(cx - 5f, cy - 5f, cx + 5f, cy + 5f)
    }

}