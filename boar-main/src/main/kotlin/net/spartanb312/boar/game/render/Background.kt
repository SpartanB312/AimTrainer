package net.spartanb312.boar.game.render

import net.spartanb312.boar.game.render.scene.SceneManager
import net.spartanb312.boar.game.render.scene.impls.DummyScene
import net.spartanb312.boar.graphics.GLHelper
import net.spartanb312.boar.graphics.ParticleSystem
import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.graphics.texture.Texture
import net.spartanb312.boar.graphics.texture.drawTexture
import net.spartanb312.boar.utils.color.ColorRGB
import net.spartanb312.boar.utils.math.ConvergeUtil.converge
import net.spartanb312.boar.utils.timing.Timer
import kotlin.math.max

object Background {

    private var offsetXRate = 0.0
    private var offsetYRate = 0.0
    private val offsetTimer = Timer()
    private val particleSystem = ParticleSystem(
        speed = 0.1f,
        initialColor = ColorRGB(164, 255, 255).alpha(128)
    )

    fun renderBackground(mouseX: Double, mouseY: Double) {
        if (SceneManager.currentScene != DummyScene) return
        TextureManager.bg.drawBackground(mouseX, mouseY)
        particleSystem.render()
    }

    fun Texture.drawBackground(mouseX: Double, mouseY: Double, offsetRate: Float = 0.01f) {
        GLHelper.glMatrixScope {
            val aspect = width / height.toFloat()
            val maxOffset = max(RS.widthF * offsetRate, RS.heightF * offsetRate)
            offsetTimer.passedAndReset(17) {
                val xOffsetRate = (if (mouseX < RS.centerXF) (mouseX / RS.centerXF) - 1f
                else (mouseX - RS.centerXF) / RS.centerXF).coerceIn(-1.0..1.0) * 100
                offsetXRate = offsetXRate.converge(xOffsetRate, 0.05)
                val yOffsetRate = (if (mouseY < RS.centerYF) (mouseY / RS.centerYF) - 1f
                else (mouseY - RS.centerYF) / RS.centerYF).coerceIn(-1.0..1.0) * 100
                offsetYRate = offsetYRate.converge(yOffsetRate, 0.05)
            }
            val offsetX = (maxOffset * offsetXRate).toFloat() / 100f
            val offsetY = (maxOffset * offsetYRate).toFloat() / 100f
            var startX = 0f - maxOffset + offsetX
            var startY = 0f - maxOffset + offsetY
            var endX = RS.widthF + maxOffset + offsetX
            var endY = RS.heightF + maxOffset + offsetY
            if (RS.aspect > aspect) {
                val offset = (RS.widthF / aspect - RS.heightF) / 2f
                startY -= offset
                endY += offset
            } else if (RS.aspect < aspect) {
                val offset = (RS.heightF * aspect - RS.widthF) / 2f
                startX -= offset
                endX += offset
            }
            drawTexture(startX, startY, endX, endY)
        }
    }

}