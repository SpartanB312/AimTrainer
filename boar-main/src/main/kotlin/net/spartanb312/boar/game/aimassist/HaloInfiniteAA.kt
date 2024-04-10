package net.spartanb312.boar.game.aimassist

import net.spartanb312.boar.game.Player
import net.spartanb312.boar.game.entity.Entity
import net.spartanb312.boar.game.option.impls.AimAssistOption
import net.spartanb312.boar.game.render.scene.SceneManager
import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.utils.math.MathUtils.mul
import net.spartanb312.boar.utils.math.toRadian
import net.spartanb312.boar.utils.math.vector.Vec3f
import net.spartanb312.boar.utils.math.vector.distanceTo
import org.joml.Matrix4d
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import kotlin.math.cos
import kotlin.math.sin

object HaloInfiniteAA : AimAssist {

    private var locked: LockData? = null

    private fun lockEntity(entity: Entity) {
        locked = LockData(entity, entity.pos, Player.pos, Player.yaw, Player.pitch)
    }

    class LockData(val entity: Entity, val entityPos: Vec3f, val playerPos: Vec3f, val yaw: Float, val pitch: Float)

    private val feedbackRate
        get() = if (GLFW.glfwGetMouseButton(
                RS.window,
                GLFW.GLFW_MOUSE_BUTTON_1
            ) == GLFW.GLFW_PRESS
        ) AimAssistOption.firingCompensation else AimAssistOption.moveCompensation

    override fun compensate(sensitivity: Double) {
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
            if (!angle.isNaN()) {
                if (zeroAngle) {
                    if (AimAssistOption.zeroAngleFriction) FrictionAA.compensate(sensitivity, true)
                } else {
                    var feedbackAngle = angle * feedbackRate
                    feedbackAngle *= 100f / Player.pos.distanceTo(result.pos).toFloat()
                    val normal = preVec cross vec
                    val mat = Matrix4d().rotate(feedbackAngle, Vector3f(normal.x, normal.y, normal.z))
                    val feedbackVec = preVec.mul(mat)
                    //println("Feedback Angle${feedbackVec.angle(vec).toDegree()}, Angle ${feedbackAngle.toDegree()}")
                    val clampRange = -(feedbackRate.toFloat() / 10f)..(feedbackRate.toFloat() / 10f)
                    val yawOffset = (feedbackVec.yaw - preVec.yaw).coerceIn(clampRange)
                    val pitchOffset = (feedbackVec.pitch - preVec.pitch).coerceIn(clampRange)
                    Player.yaw = locked.yaw + yawOffset * cos(Player.pitch.toRadian())
                    Player.pitch = locked.pitch + pitchOffset
                }
            }
        } else FrictionAA.compensate(sensitivity)
        if (result != null) lockEntity(result)
        else this.locked = null
    }

}