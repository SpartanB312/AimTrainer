package net.spartanb312.boar.game.aimassist

import net.spartanb312.boar.game.Academy
import net.spartanb312.boar.game.Player
import net.spartanb312.boar.game.entity.Entity
import net.spartanb312.boar.game.option.impls.AimAssistOption
import net.spartanb312.boar.game.option.impls.AimAssistOption.zeroAngleFrictionRate
import net.spartanb312.boar.game.render.gui.Render2DManager
import net.spartanb312.boar.game.render.scene.SceneManager
import net.spartanb312.boar.game.render.scene.impls.AimTrainingScene
import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.utils.math.ConvergeUtil.converge
import net.spartanb312.boar.utils.math.MathUtils.mul
import net.spartanb312.boar.utils.math.toRadian
import net.spartanb312.boar.utils.math.vector.Vec3f
import net.spartanb312.boar.utils.timing.Timer
import org.joml.Matrix4d
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import kotlin.math.cos
import kotlin.math.sin

class HaloInfiniteAA {

    private var locked: LockData? = null
    private val aaTimer = Timer()

    private fun lockEntity(entity: Entity) {
        locked = if (locked != null) null
        else LockData(entity, entity.pos, Player.pos, Player.yaw, Player.pitch)
    }

    class LockData(val entity: Entity, val entityPos: Vec3f, val playerPos: Vec3f, val yaw: Float, val pitch: Float)

    private val feedbackRate
        get() = if (GLFW.glfwGetMouseButton(
                RS.window,
                GLFW.GLFW_MOUSE_BUTTON_1
            ) == GLFW.GLFW_PRESS
        ) AimAssistOption.firingCompensation else AimAssistOption.moveCompensation

    fun aimComposite(sensitivity: Double) {
        aaTimer.passedAndReset(5) {
            Player.sensK = if (Player.sensK == -1.0) sensitivity * 1000.0
            else {
                val firing = !Render2DManager.displaying
                        && (SceneManager.currentScene == AimTrainingScene || SceneManager.currentScene is Academy.AcademyScene)
                        && GLFW.glfwGetMouseButton(RS.window, GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS
                val targetAA = when {
                    AimAssistOption.frEnabled && Player.raytraced && firing -> AimAssistOption.firingCoefficient
                    AimAssistOption.frEnabled && Player.raytraced && !firing -> AimAssistOption.raytraceCoefficient
                    else -> 1.0
                }
                if (targetAA == 1.0) Player.sensK.converge(sensitivity * 1000.0, 0.3)
                else Player.sensK.converge(sensitivity * targetAA * 1000.0, 0.5)
            }
        }
    }

    var max = 0f

    fun moveComposite() {
        if (!AimAssistOption.mcEnabled) return
        val locked = locked
        val result = SceneManager.currentScene.getRayTracedResult(
            Player.offsetPos,
            Player.camera.front,
            if (AimAssistOption.bulletAdsorption.value) AimAssistOption.errorAngle else 0f,
        )
        if (locked != null && result != null && locked.entity == result) {
            val facing = Vec3f(cos(Player.yaw.toRadian()), 0, sin(Player.yaw.toRadian()))
            val ref = Vec3f(1f, 0f, 0f)
            val refAngle = facing.angle(ref)
            val axis = ref cross facing
            val rotateMat = Matrix4d().rotate(refAngle.toDouble(), Vector3f(axis.x, axis.y, axis.z))
            val lastPlayerPos = locked.playerPos
            val lastEntityPos = locked.entityPos
            val preVec = (lastEntityPos - lastPlayerPos).mul(rotateMat)
            val vec = (result.pos - Player.pos).mul(rotateMat)
            val angle = vec.angle(preVec)
            val zeroAngle = vec == preVec
            if (!angle.isNaN() && (AimAssistOption.zeroAngleFriction || !zeroAngle)) {
                val feedbackAngle = angle * feedbackRate * if (zeroAngle) 0.5f * zeroAngleFrictionRate else 1.0f
                val normal = preVec cross vec
                val mat = Matrix4d().rotate(feedbackAngle.toDouble(), Vector3f(normal.x, normal.y, normal.z))
                val feedbackVec = preVec.mul(mat)
                //println("Feedback Angle${feedbackVec.angle(vec).toDegree()}, Angle ${feedbackAngle.toDegree()}")
                val clampRange = -(feedbackRate / 10f)..(feedbackRate / 10f)
                val yawOffset = (feedbackVec.yaw - preVec.yaw).coerceIn(clampRange)
                val pitchOffset = (feedbackVec.pitch - preVec.pitch).coerceIn(clampRange)
                Player.yaw = locked.yaw + yawOffset * cos(Player.pitch.toRadian())
                Player.pitch = locked.pitch + pitchOffset
            }
        }
        if (result != null) lockEntity(result)
        else this.locked = null
    }

}