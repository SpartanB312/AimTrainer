package net.spartanb312.boar.game.aimassist

import net.spartanb312.boar.game.Player
import net.spartanb312.boar.game.entity.Entity
import net.spartanb312.boar.game.option.impls.AimAssistOption
import net.spartanb312.boar.game.render.scene.SceneManager
import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.utils.math.toDegree
import net.spartanb312.boar.utils.math.toRadian
import net.spartanb312.boar.utils.math.vector.Vec3f
import net.spartanb312.boar.utils.math.vector.distanceTo
import org.joml.Matrix4d
import org.joml.Vector3f
import org.joml.Vector4d
import org.lwjgl.glfw.GLFW
import kotlin.math.cos
import kotlin.math.sin

class HaloInfiniteAA {

    var locked: LockData? = null; private set

    private fun lockEntity(entity: Entity) {
        locked = if (locked != null) null
        else LockData(entity, entity.pos, Player.pos, Player.yaw, Player.pitch)
        // println("Lock on ${entity.pos}")
    }

    class LockData(val entity: Entity, val entityPos: Vec3f, val playerPos: Vec3f, val yaw: Float, val pitch: Float)

    private val adsorptionOffset = { distance: Double, radius: Float -> distance < radius + 0.7f }

    private val feedbackRate
        get() = if (GLFW.glfwGetMouseButton(
                RS.window,
                GLFW.GLFW_MOUSE_BUTTON_1
            ) == GLFW.GLFW_PRESS
        ) 18f else 27f

    fun aimBot() {
        val locked = locked
        val result = SceneManager.currentScene.entities.sortedBy { it.pos.distanceTo(Player.pos) }.firstOrNull()
        if (locked != null && result != null && locked.entity == result) {
            val vec = result.pos - Player.pos
            Player.yaw = vec.yaw.toDegree()
            Player.pitch = vec.pitch.toDegree() + 0.1f
        }
        if (result != null) lockEntity(result)
        else this.locked = null
    }

    fun onTick() {
        val locked = locked
        val result = SceneManager.currentScene.getRayTracedResult(
            Player.offsetPos,
            Player.camera.front,
            if (AimAssistOption.bulletAdsorption) adsorptionOffset else { _, _ -> false },
        )
        if (locked != null && result != null && locked.entity == result) {
            val facing = Vec3f(cos(Player.yaw.toRadian()), 0, sin(Player.yaw.toRadian()))
            val ref = Vec3f(1f, 0f, 0f)
            val refAngle = facing.angle(ref)
            val axis = ref cross facing
            val rotateMat = Matrix4d().rotate(refAngle.toDouble(), Vector3f(axis.x, axis.y, axis.z))
            val lastPlayerPos = locked.playerPos
            val lastEntityPos = locked.entityPos
            val preVec = (lastEntityPos - lastPlayerPos).let {
                Vector4d(it.x.toDouble(), it.y.toDouble(), it.z.toDouble(), 0.0)
            }.mul(rotateMat).let {
                Vec3f(it.x, it.y, it.z)
            }
            val vec = (result.pos - Player.pos).let {
                Vector4d(it.x.toDouble(), it.y.toDouble(), it.z.toDouble(), 0.0)
            }.mul(rotateMat).let {
                Vec3f(it.x, it.y, it.z)
            }
            val angle = vec.angle(preVec)
            if (!angle.isNaN()) {
                val feedbackAngle = angle * feedbackRate
                val normal = preVec cross vec
                val mat = Matrix4d().rotate(feedbackAngle.toDouble(), Vector3f(normal.x, normal.y, normal.z))
                val feedbackVec = Vector4d(preVec.x.toDouble(), preVec.y.toDouble(), preVec.z.toDouble(), 0.0).mul(mat)
                    .let { Vec3f(it.x, it.y, it.z) }
                val yawOffset = feedbackVec.yaw - vec.yaw
                val pitchOffset = feedbackVec.pitch - vec.pitch
                Player.yaw += yawOffset * cos(Player.pitch.toRadian())
                Player.pitch += pitchOffset
            }
        }
        if (result != null) lockEntity(result)
        else this.locked = null
    }

    fun onTick2() {
        val locked = locked
        val result = SceneManager.currentScene.getRayTracedResult(
            Player.offsetPos,
            Player.camera.front,
            if (AimAssistOption.bulletAdsorption) adsorptionOffset else { _, _ -> false },
        )
        if (locked != null && result != null && locked.entity == result) {
            val lastPlayerPos = locked.playerPos
            val lastEntityPos = locked.entityPos
            val preVec = lastEntityPos - lastPlayerPos
            val vec = result.pos - Player.pos
            val angle = vec.angle(preVec)
            if (!angle.isNaN()) {
                val feedbackAngle = angle * feedbackRate
                val normal = preVec cross vec
                val mat = Matrix4d().rotate(feedbackAngle.toDouble(), Vector3f(normal.x, normal.y, normal.z))
                val feedbackVec = Vector4d(preVec.x.toDouble(), preVec.y.toDouble(), preVec.z.toDouble(), 0.0).mul(mat)
                    .let { Vec3f(it.x, it.y, it.z) }
                val yawOffset = feedbackVec.yaw - vec.yaw
                val pitchOffset = feedbackVec.pitch - vec.pitch
                Player.yaw += yawOffset
                Player.pitch += pitchOffset
                // debug
                if (yawOffset != 0f && pitchOffset != 0f) {
                    println(result.pos)
                    println(locked.entity.pos)
                    println(angle.toDegree())
                    println(feedbackAngle.toDegree())
                    println(yawOffset.toDegree())
                    println(pitchOffset.toDegree())
                    println()
                }
            }
        }
        if (result != null) lockEntity(result)
        else this.locked = null
    }

}

//fun main() {
//    val lastPlayerPos = Vec3f(0f, 0f, 0f)
//    val lastEntityPos = Vec3f(1f, 1f, 1f)
//    val preVec = lastEntityPos - lastPlayerPos
//    val vec = Vec3f(1f, 2f, 1f) - Vec3f(-1f, 0f, 0f)
//    val angle = vec.angle(preVec)
//    val feedbackAngle = angle * 1f
//    println("Angle $angle, FB $feedbackAngle")
//    val normal = preVec cross vec
//    val mat = Matrix4f().rotate(feedbackAngle, Vector3f(normal.x, normal.y, normal.z))
//    val feedbackVec = Vector4f(preVec.x, preVec.y, preVec.z, 0f).mul(mat).let { Vec3f(it.x, it.y, it.z) }
//    val yawOffset = feedbackVec.yaw - preVec.yaw
//    val pitchOffset = feedbackVec.pitch - preVec.pitch
//    println(yawOffset.toDegree())
//    println(pitchOffset.toDegree())
//}