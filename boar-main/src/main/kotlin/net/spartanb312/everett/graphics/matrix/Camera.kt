package net.spartanb312.everett.graphics.matrix

import net.spartanb312.everett.utils.math.toRadian
import net.spartanb312.everett.utils.math.vector.Vec3f
import org.joml.Matrix4f
import kotlin.math.cos
import kotlin.math.sin

fun Matrix4f.mulCameraProject(
    yaw: Float,
    pitch: Float,
    cameraPosX: Float,
    cameraPosY: Float,
    cameraPosZ: Float
): Matrix4f = mul(cameraProjectMat(yaw, pitch, cameraPosX, cameraPosY, cameraPosZ))

fun MatrixLayerStack.MatrixScope.applyCameraProject(
    yaw: Float,
    pitch: Float,
    cameraPosX: Float,
    cameraPosY: Float,
    cameraPosZ: Float
): Matrix4f = layer.apply(checkInc, cameraProjectMat(yaw, pitch, cameraPosX, cameraPosY, cameraPosZ))

fun MatrixLayerStack.MatrixScope.cameraProject(
    yaw: Float,
    pitch: Float,
    cameraPosX: Float,
    cameraPosY: Float,
    cameraPosZ: Float
): MatrixLayerStack.MatrixScope {
    layer.mul(checkInc, cameraProjectMat(yaw, pitch, cameraPosX, cameraPosY, cameraPosZ))
    return this
}

fun Matrix4f.mulCameraProject(
    yaw: Float,
    pitch: Float,
    cameraPos: Vec3f
): Matrix4f = mul(cameraProjectMat(yaw, pitch, cameraPos.x, cameraPos.y, cameraPos.z))

fun MatrixLayerStack.MatrixScope.applyCameraProject(
    yaw: Float,
    pitch: Float,
    cameraPos: Vec3f
): Matrix4f = layer.apply(checkInc, cameraProjectMat(yaw, pitch, cameraPos.x, cameraPos.y, cameraPos.z))

fun MatrixLayerStack.MatrixScope.cameraProject(
    yaw: Float,
    pitch: Float,
    cameraPos: Vec3f
): MatrixLayerStack.MatrixScope {
    layer.mul(checkInc, cameraProjectMat(yaw, pitch, cameraPos.x, cameraPos.y, cameraPos.z))
    return this
}

// Raw
fun cameraProjectMat(
    yaw: Float,
    pitch: Float,
    cameraPosX: Float,
    cameraPosY: Float,
    cameraPosZ: Float
) = cameraProjectMat(yaw, pitch, Vec3f(cameraPosX, cameraPosY, cameraPosZ))

fun cameraProjectMat(
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