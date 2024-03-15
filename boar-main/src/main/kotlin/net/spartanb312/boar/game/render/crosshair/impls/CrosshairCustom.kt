package net.spartanb312.boar.game.render.crosshair.impls

import net.spartanb312.boar.game.config.setting.alias
import net.spartanb312.boar.game.config.setting.whenTrue
import net.spartanb312.boar.game.render.TextureManager
import net.spartanb312.boar.game.render.crosshair.Crosshair
import net.spartanb312.boar.graphics.GLHelper
import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.graphics.matrix.mulScale
import net.spartanb312.boar.graphics.matrix.mulToGL
import net.spartanb312.boar.graphics.matrix.mulTranslate
import net.spartanb312.boar.graphics.matrix.translatef
import net.spartanb312.boar.graphics.texture.drawTexture
import net.spartanb312.boar.utils.color.ColorRGB
import kotlin.math.min

object CrosshairCustom : Crosshair(0f) {

    private val size by setting("Custom-Size", 15f, 1f..50f, 0.5f).alias("Size")
    val calibrate = setting("Custom-Calibrate", false).alias("Calibrate")
    private val offsetX by setting("Custom-Offset X", 0.0, -5.0..5.0, 0.005).alias("Offset X").whenTrue(calibrate)
    private val offsetY by setting("Custom-Offset Y", 0.0, -5.0..5.0, 0.005).alias("Offset Y").whenTrue(calibrate)

    private val crosshair = TextureManager.lazyTexture("assets/CustomCrosshair.png")

    override var clickTime = System.currentTimeMillis()

    override fun onRender(fov: Float, test: Boolean, colorRGB: ColorRGB) {
        val centerX = (if (test) 0f else RS.centerXF) + offsetX.toFloat()
        val centerY = (if (test) 0f else RS.centerYF) + offsetY.toFloat()
        val scaledSize = min(RS.widthScale, RS.heightScale) * size
        GLHelper.glMatrixScope {
            translatef(centerX, centerY, 0.0f)
                .mulScale(scaledSize, scaledSize, 1.0f)
                .mulTranslate(-centerX, -centerY, 0.0f)
                .mulToGL()
            crosshair.drawTexture(centerX - 5f, centerY - 5f, centerX + 5f, centerY + 5f)
        }
    }

}