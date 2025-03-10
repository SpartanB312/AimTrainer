package net.spartanb312.everett.game

import net.spartanb312.everett.game.option.impls.ControlOption
import net.spartanb312.everett.graphics.Camera
import net.spartanb312.everett.graphics.RenderSystem
import net.spartanb312.everett.graphics.RenderSystem.initialMouseValue

object CameraImpl : Camera() {

    private var lastMouseX = RenderSystem.originMouseX
    private var lastMouseY = RenderSystem.originMouseY
    override fun onUpdate(
        sensitivity: Double,
        dpiModifier: Double,
        vRate: Float,
        hRate: Float,
        updateCamera: Boolean
    ) {
        val mouseX = RenderSystem.originMouseX
        val mouseY = RenderSystem.originMouseY
        if (updateCamera) {
            if (lastMouseX == initialMouseValue || lastMouseY == initialMouseValue) {
                lastMouseX = mouseX
                lastMouseY = mouseY
            }
            if (mouseX != lastMouseX || mouseY != lastMouseY) {
                val diffX = RenderSystem.originMouseX - lastMouseX
                val diffY = RenderSystem.originMouseY - lastMouseY
                lastMouseX = RenderSystem.originMouseX
                lastMouseY = RenderSystem.originMouseY
                yaw += (diffX * ControlOption.game.multiplier * sensitivity * hRate * dpiModifier).toFloat()
                pitch -= (diffY * ControlOption.game.multiplier * sensitivity * vRate * dpiModifier).toFloat()
                while (true) {
                    if (yaw > 360f) yaw -= 360f
                    else if (yaw < 0f) yaw += 360f
                    else break
                }
                pitch = pitch.coerceIn(-89.5f..89.5f)
            }
        } else {
            lastMouseX = mouseX
            lastMouseY = mouseY
        }
    }

}