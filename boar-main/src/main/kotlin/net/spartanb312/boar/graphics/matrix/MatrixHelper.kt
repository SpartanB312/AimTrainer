package net.spartanb312.boar.graphics.matrix

import net.spartanb312.boar.utils.math.vector.Vec3f
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL11
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

fun Matrix4f.mulTranslate(x: Float, y: Float, z: Float): Matrix4f = mul(translatef(x, y, z))

fun Matrix4f.mulScale(x: Float, y: Float, z: Float): Matrix4f = mul(scalef(x, y, z))

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

fun applyLookAt(
    eyeX: Float,
    eyeY: Float,
    eyeZ: Float,
    centerX: Float,
    centerY: Float,
    centerZ: Float,
    upX: Float,
    upY: Float,
    upZ: Float,
): Matrix4f = Matrix4f().lookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ).applyToGL()

fun applyOrtho(left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float): Matrix4f =
    Matrix4f().ortho(left, right, bottom, top, near, far).applyToGL()

fun applyPerspective(fov: Float, aspect: Float, zNear: Float, zFar: Float): Matrix4f =
    Matrix4f().perspective((fov * PI / 180f).toFloat(), aspect, zNear, zFar).applyToGL()

fun applyTranslate(x: Float, y: Float, z: Float): Matrix4f =
    Matrix4f().translate(x, y, z).applyToGL()

fun applyScale(x: Float, y: Float, z: Float): Matrix4f =
    Matrix4f().scale(x, y, z).applyToGL()

fun applyRotate(angleDegree: Float, axis: Vector3f): Matrix4f =
    Matrix4f().rotate((angleDegree / (180f / PI)).toFloat(), axis).applyToGL()

fun Matrix4f.getFloatArray() = floatArrayOf(
    m00(), m01(), m02(), m03(),
    m10(), m11(), m12(), m13(),
    m20(), m21(), m22(), m23(),
    m30(), m31(), m32(), m33()
)

fun Matrix4f.applyToGL(): Matrix4f = apply { GL11.glLoadMatrixf(getFloatArray()) }

fun Matrix4f.mulToGL(): Matrix4f = apply { GL11.glMultMatrixf(getFloatArray()) }

// Stack
fun MatrixStack.lookAtf(
    eyeX: Float,
    eyeY: Float,
    eyeZ: Float,
    centerX: Float,
    centerY: Float,
    centerZ: Float,
    upX: Float,
    upY: Float,
    upZ: Float,
): MatrixStack {
    current().mulLookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ)
    return this
}

fun MatrixStack.orthof(left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float): MatrixStack {
    current().mulOrtho(left, right, bottom, top, near, far)
    return this
}

fun MatrixStack.perspectivef(fov: Float, aspect: Float, zNear: Float, zFar: Float): MatrixStack {
    current().mulPerspective(fov, aspect, zNear, zFar)
    return this
}

fun MatrixStack.translatef(x: Float, y: Float, z: Float): MatrixStack {
    current().mulTranslate(x, y, z)
    return this
}

fun MatrixStack.scalef(x: Float, y: Float, z: Float): MatrixStack {
    current().mulScale(x, y, z)
    return this
}

fun MatrixStack.rotatef(angleDegree: Float, axis: Vec3f): MatrixStack {
    current().mulRotate(angleDegree, axis)
    return this
}

// Apply to GL
fun MatrixStack.applyOrtho(left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float): MatrixStack {
    current().mulOrtho(left, right, bottom, top, near, far)
    applyToGL()
    return this
}

fun MatrixStack.applyPerspective(fov: Float, aspect: Float, zNear: Float, zFar: Float): MatrixStack {
    current().mulPerspective(fov, aspect, zNear, zFar)
    applyToGL()
    return this
}

fun MatrixStack.applyTranslate(x: Float, y: Float, z: Float): MatrixStack {
    current().mulTranslate(x, y, z)
    applyToGL()
    return this
}

fun MatrixStack.applyScale(x: Float, y: Float, z: Float): MatrixStack {
    current().mulScale(x, y, z)
    applyToGL()
    return this
}

fun MatrixStack.applyRotate(angleDegree: Float, axis: Vec3f): MatrixStack {
    current().mulRotate(angleDegree, axis)
    applyToGL()
    return this
}

fun Vector3f.transform(matrix: Matrix4f): Vector3f {
    return matrix.transformPosition(this)
}

inline fun matrixStack(block: MatrixStack.() -> Unit): MatrixStack = MatrixStack().apply {
    block()
}
