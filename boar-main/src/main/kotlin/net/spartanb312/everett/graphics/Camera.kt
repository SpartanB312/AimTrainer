package net.spartanb312.everett.graphics

import net.spartanb312.everett.graphics.matrix.MatrixLayerStack
import net.spartanb312.everett.graphics.matrix.cameraProject
import net.spartanb312.everett.graphics.matrix.perspectivef
import net.spartanb312.everett.utils.math.toRadian
import net.spartanb312.everett.utils.math.vector.Vec3f
import net.spartanb312.everett.utils.misc.DelegateValue
import kotlin.math.cos
import kotlin.math.sin

abstract class Camera(
    open var fov: Float = 70f,
    open var zRange: ClosedFloatingPointRange<Float> = 0.01f..1000f,
    yaw: Float = 0f,
    pitch: Float = 0f,
    open var cameraPos: Vec3f = Vec3f()
) {

    val yawDelegate = DelegateValue(yaw)
    val pitchDelegate = DelegateValue(pitch)

    var yaw by yawDelegate
    var pitch by pitchDelegate

    val front
        get() = Vec3f(
            cos(pitch.toRadian()) * cos(yaw.toRadian()),
            sin(pitch.toRadian()),
            cos(pitch.toRadian()) * sin(yaw.toRadian())
        )

    abstract fun onUpdate(
        sensitivity: Double = 2.2,
        dpiModifier: Double = 1.0,
        vRate: Float = 1.0f,
        hRate: Float = 1.0f,
        updateCamera: Boolean = false
    )

    fun MatrixLayerStack.MatrixScope.project(
        yaw: Float = this@Camera.yaw,
        pitch: Float = this@Camera.pitch,
        position: Vec3f = this@Camera.cameraPos,
        fov: Float = this@Camera.fov,
        zNear: Float = this@Camera.zRange.start,
        zFar: Float = this@Camera.zRange.endInclusive,
        sensitivity: Double = 2.2,
        dpiModifier: Double = 1.0,
        vRate: Float = 1.0f,
        hRate: Float = 1.0f,
        updateCamera: Boolean = true,
        block: Camera.() -> Unit
    ) {
        onUpdate(sensitivity, dpiModifier, vRate, hRate, updateCamera)
        perspectivef(fov, RenderSystem.widthF / RenderSystem.heightF, zNear, zFar)
        cameraProject(yaw, pitch, position)
        block.invoke(this@Camera)
    }

    companion object {
        fun MatrixLayerStack.MatrixScope.project(
            yaw: Float,
            pitch: Float,
            position: Vec3f = Vec3f.ZERO,
            fov: Float = 78f,
            zNear: Float = 0.01f,
            zFar: Float = 1000f,
            block: () -> Unit
        ) {
            perspectivef(fov, RenderSystem.widthF / RenderSystem.heightF, zNear, zFar)
            cameraProject(yaw, pitch, position)
            block.invoke()
        }
    }

}