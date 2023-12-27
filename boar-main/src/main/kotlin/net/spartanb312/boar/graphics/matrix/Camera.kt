package net.spartanb312.boar.graphics.matrix

import net.spartanb312.boar.utils.math.toRadian
import net.spartanb312.boar.utils.math.vector.Vec3f
import org.joml.Matrix4f
import kotlin.math.cos
import kotlin.math.sin

fun Matrix4f.mulCameraProject(
    yaw: Float,
    pitch: Float,
    cameraPosX: Float,
    cameraPosY: Float,
    cameraPosZ: Float
): Matrix4f = mul(cameraProject(yaw, pitch, cameraPosX, cameraPosY, cameraPosZ))

fun applyCameraProject(
    yaw: Float,
    pitch: Float,
    cameraPosX: Float,
    cameraPosY: Float,
    cameraPosZ: Float
): Matrix4f = cameraProject(yaw, pitch, cameraPosX, cameraPosY, cameraPosZ).applyToGL()

fun MatrixStack.cameraProject(
    yaw: Float,
    pitch: Float,
    cameraPosX: Float,
    cameraPosY: Float,
    cameraPosZ: Float
): MatrixStack {
    current().mulCameraProject(yaw, pitch, cameraPosX, cameraPosY, cameraPosZ)
    return this
}

fun MatrixStack.applyCameraProject(
    yaw: Float,
    pitch: Float,
    cameraPosX: Float,
    cameraPosY: Float,
    cameraPosZ: Float
): MatrixStack {
    current().mulCameraProject(yaw, pitch, cameraPosX, cameraPosY, cameraPosZ)
    applyToGL()
    return this
}

fun Matrix4f.mulCameraProject(
    yaw: Float,
    pitch: Float,
    cameraPos: Vec3f
): Matrix4f = mul(cameraProject(yaw, pitch, cameraPos.x, cameraPos.y, cameraPos.z))

fun applyCameraProject(
    yaw: Float,
    pitch: Float,
    cameraPos: Vec3f
): Matrix4f = cameraProject(yaw, pitch, cameraPos.x, cameraPos.y, cameraPos.z).applyToGL()

fun MatrixStack.cameraProject(
    yaw: Float,
    pitch: Float,
    cameraPos: Vec3f
): MatrixStack {
    current().mulCameraProject(yaw, pitch, cameraPos.x, cameraPos.y, cameraPos.z)
    return this
}

fun MatrixStack.applyCameraProject(
    yaw: Float,
    pitch: Float,
    cameraPos: Vec3f
): MatrixStack {
    current().mulCameraProject(yaw, pitch, cameraPos.x, cameraPos.y, cameraPos.z)
    applyToGL()
    return this
}

// Raw
fun cameraProject(
    yaw: Float,
    pitch: Float,
    cameraPosX: Float,
    cameraPosY: Float,
    cameraPosZ: Float
) = cameraProject(yaw, pitch, Vec3f(cameraPosX, cameraPosY, cameraPosZ))

fun cameraProject(
    yaw: Float,
    pitch: Float,
    cameraPos: Vec3f
): Matrix4f {
    val cameraTarget = Vec3f(cameraPos.x - 0.00001f, cameraPos.y, cameraPos.z - 0.00001f)
    val cameraFront = Vec3f(
        cos(pitch.toRadian()) * cos(yaw.toRadian()),
        sin(pitch.toRadian()),
        cos(pitch.toRadian()) * sin(yaw.toRadian())
    )
    val cameraDirection = cameraPos - cameraTarget
    val cameraUp = cameraDirection cross (Vec3f(0f, 1f, 0f) cross cameraDirection)
    val eyePos = cameraPos + cameraFront
    return lookAtf(
        cameraPos.x,
        cameraPos.y,
        cameraPos.z,
        eyePos.x,
        eyePos.y,
        eyePos.z,
        cameraUp.x,
        cameraUp.y,
        cameraUp.z
    )
}