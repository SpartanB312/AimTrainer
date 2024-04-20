package net.spartanb312.everett.graphics.matrix

import net.spartanb312.everett.utils.math.vector.Vec3f
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.math.PI

fun Matrix4f.mulLookAt(
    eyeX: Float,
    eyeY: Float,
    eyeZ: Float,
    centerX: Float,
    centerY: Float,
    centerZ: Float,
    upX: Float,
    upY: Float,
    upZ: Float,
): Matrix4f = mul(lookAtf(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ))

fun Matrix4f.mulOrtho(left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float): Matrix4f =
    mul(orthof(left, right, bottom, top, near, far))

fun Matrix4f.mulPerspective(fov: Float, aspect: Float, zNear: Float, zFar: Float): Matrix4f =
    mul(perspectivef(fov, aspect, zNear, zFar))

fun Matrix4f.mulTranslate(x: Float, y: Float, z: Float): Matrix4f =
    mul(translatef(x, y, z))

fun Matrix4f.mulScale(x: Float, y: Float, z: Float): Matrix4f =
    mul(scalef(x, y, z))

fun Matrix4f.mulRotate(angleDegree: Float, axis: Vec3f): Matrix4f =
    mul(rotatef(angleDegree, axis))

fun lookAtf(
    eyeX: Float,
    eyeY: Float,
    eyeZ: Float,
    centerX: Float,
    centerY: Float,
    centerZ: Float,
    upX: Float,
    upY: Float,
    upZ: Float,
): Matrix4f = Matrix4f().lookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)

fun orthof(left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float): Matrix4f =
    Matrix4f().ortho(left, right, bottom, top, near, far)

fun perspectivef(fov: Float, aspect: Float, zNear: Float, zFar: Float): Matrix4f =
    Matrix4f().perspective((fov * PI / 180f).toFloat(), aspect, zNear, zFar)

fun translatef(x: Float, y: Float, z: Float): Matrix4f =
    Matrix4f().translate(x, y, z)

fun scalef(x: Float, y: Float, z: Float): Matrix4f =
    Matrix4f().scale(x, y, z)

fun rotatef(angleDegree: Float, axis: Vec3f): Matrix4f =
    Matrix4f().rotate((angleDegree / (180f / PI)).toFloat(), Vector3f(axis.x, axis.y, axis.z))

fun MatrixLayerStack.applyLookAt(
    eyeX: Float,
    eyeY: Float,
    eyeZ: Float,
    centerX: Float,
    centerY: Float,
    centerZ: Float,
    upX: Float,
    upY: Float,
    upZ: Float,
): Matrix4f = apply(Matrix4f().lookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ))

fun MatrixLayerStack.applyOrtho(
    left: Float,
    right: Float,
    bottom: Float,
    top: Float,
    near: Float,
    far: Float
): Matrix4f =
    apply(Matrix4f().ortho(left, right, bottom, top, near, far))

fun MatrixLayerStack.applyPerspective(fov: Float, aspect: Float, zNear: Float, zFar: Float): Matrix4f =
    apply(Matrix4f().perspective((fov * PI / 180f).toFloat(), aspect, zNear, zFar))

fun MatrixLayerStack.applyTranslate(x: Float, y: Float, z: Float): Matrix4f =
    apply(Matrix4f().translate(x, y, z))

fun MatrixLayerStack.applyScale(x: Float, y: Float, z: Float): Matrix4f =
    apply(Matrix4f().scale(x, y, z))

fun MatrixLayerStack.applyRotate(angleDegree: Float, axis: Vector3f): Matrix4f =
    apply(Matrix4f().rotate((angleDegree / (180f / PI)).toFloat(), axis))

fun Matrix4f.getFloatArray() = floatArrayOf(
    m00(), m01(), m02(), m03(),
    m10(), m11(), m12(), m13(),
    m20(), m21(), m22(), m23(),
    m30(), m31(), m32(), m33()
)

// Stack
fun MatrixLayerStack.lookAtf(
    eyeX: Float,
    eyeY: Float,
    eyeZ: Float,
    centerX: Float,
    centerY: Float,
    centerZ: Float,
    upX: Float,
    upY: Float,
    upZ: Float,
): MatrixLayerStack {
    mul(Matrix4f().lookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ))
    return this
}

fun MatrixLayerStack.orthof(
    left: Float,
    right: Float,
    bottom: Float,
    top: Float,
    near: Float,
    far: Float
): MatrixLayerStack {
    mul(Matrix4f().ortho(left, right, bottom, top, near, far))
    return this
}

fun MatrixLayerStack.perspectivef(fov: Float, aspect: Float, zNear: Float, zFar: Float): MatrixLayerStack {
    mul(Matrix4f().perspective((fov * PI / 180f).toFloat(), aspect, zNear, zFar))
    return this
}

fun MatrixLayerStack.translatef(x: Float, y: Float, z: Float): MatrixLayerStack {
    mul(Matrix4f().translate(x, y, z))
    return this
}

fun MatrixLayerStack.scalef(x: Float, y: Float, z: Float): MatrixLayerStack {
    mul(Matrix4f().scale(x, y, z))
    return this
}

fun MatrixLayerStack.rotatef(angleDegree: Float, axis: Vec3f): MatrixLayerStack {
    mul(Matrix4f().rotate((angleDegree / (180f / PI)).toFloat(), Vector3f(axis.x, axis.y, axis.z)))
    return this
}

fun Vector3f.transform(matrix: Matrix4f): Vector3f {
    return matrix.transformPosition(this)
}

inline fun matrixStack(block: MatrixLayerStack.() -> Unit): MatrixLayerStack = MatrixLayerStack().apply {
    block()
}
