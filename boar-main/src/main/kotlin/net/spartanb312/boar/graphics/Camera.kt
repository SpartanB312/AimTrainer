package net.spartanb312.boar.graphics

import net.spartanb312.boar.graphics.RenderSystem.initialMouseValue
import net.spartanb312.boar.graphics.matrix.mulCameraProject
import net.spartanb312.boar.graphics.matrix.mulToGL
import net.spartanb312.boar.graphics.matrix.perspectivef
import net.spartanb312.boar.utils.math.vector.Vec3f

abstract class Camera(
    open var fov: Float = 70f,
    open var zRange: ClosedFloatingPointRange<Float> = 0.01f..1000f,
    open var yaw: Float = 0f,
    open var pitch: Float = 0f,
    open var cameraPos: Vec3f = Vec3f()
) {

    abstract fun onUpdate(sensitivity: Float = 2.2f, updateCamera: Boolean = false)

    fun project(
        yaw: Float = this.yaw,
        pitch: Float = this.pitch,
        position: Vec3f = this.cameraPos,
        fov: Float = this.fov,
        zNear: Float = this.zRange.start,
        zFar: Float = this.zRange.endInclusive,
        sensitivity: Float = 2.2f,
        updateCamera: Boolean = true,
        block: Camera.() -> Unit
    ) {
        onUpdate(sensitivity, updateCamera)
        perspectivef(fov, RenderSystem.widthF / RenderSystem.heightF, zNear, zFar)
            .mulCameraProject(yaw, pitch, position)
            .mulToGL()
        block.invoke(this)
    }

    object Default : Camera() {
        private var lastMouseX = RenderSystem.mouseXD
        private var lastMouseY = RenderSystem.mouseYD
        override fun onUpdate(sensitivity: Float, updateCamera: Boolean) {
            val mouseX = RenderSystem.mouseXD
            val mouseY = RenderSystem.mouseYD
            if (updateCamera) {
                if (lastMouseX == initialMouseValue || lastMouseY == initialMouseValue) {
                    lastMouseX = mouseX
                    lastMouseY = mouseY
                }
                if (mouseX != lastMouseX || mouseY != lastMouseY) {
                    val diffX = RenderSystem.mouseXD - lastMouseX
                    val diffY = RenderSystem.mouseYD - lastMouseY
                    //if (diffX > 1000 || diffY > 1000) {
                    //    println("${RenderSystem.mouseXD} - $lastMouseX")
                    //    println(diffX)
                    //    println(diffY)
                    //}
                    lastMouseX = RenderSystem.mouseXD
                    lastMouseY = RenderSystem.mouseYD
                    yaw += (diffX * 0.0371248537 * sensitivity).toFloat()
                    pitch -= (diffY * 0.0371248537 * sensitivity).toFloat()
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

    companion object {
        fun project(
            yaw: Float,
            pitch: Float,
            position: Vec3f = Vec3f.ZERO,
            fov: Float = 78f,
            zNear: Float = 0.01f,
            zFar: Float = 1000f,
            block: () -> Unit
        ) {
            perspectivef(fov, RenderSystem.widthF / RenderSystem.heightF, zNear, zFar)
                .mulCameraProject(yaw, pitch, position)
                .mulToGL()
            block.invoke()
        }
    }

}