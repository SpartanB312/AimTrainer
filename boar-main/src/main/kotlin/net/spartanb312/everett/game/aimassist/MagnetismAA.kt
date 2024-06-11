package net.spartanb312.everett.game.aimassist

import net.spartanb312.everett.game.Player
import net.spartanb312.everett.game.entity.Entity
import net.spartanb312.everett.game.option.impls.AimAssistOption
import net.spartanb312.everett.game.render.crosshair.CrosshairRenderer
import net.spartanb312.everett.game.render.scene.SceneManager
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.utils.math.Plane
import net.spartanb312.everett.utils.math.toDegree
import net.spartanb312.everett.utils.math.toRadian
import net.spartanb312.everett.utils.math.vector.Vec3f
import org.lwjgl.glfw.GLFW
import kotlin.math.abs
import kotlin.math.cos

object MagnetismAA : AimAssist {

    private var locked: LockData? = null

    private fun lockEntity(entity: Entity) {
        locked = LockData(entity, entity.pos, Player.pos, Player.yaw, Player.pitch)
    }

    class LockData(
        val entity: Entity,
        val entityPos: Vec3f,
        val playerPos: Vec3f,
        val yaw: Float,
        val pitch: Float
    )

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
            if (AimAssistOption.bulletAdsorption.value) {
                if (CrosshairRenderer.overrideErrorAngle != -1f) CrosshairRenderer.overrideErrorAngle
                else SceneManager.errorAngle
            } else 0f
        )
        if (locked != null && result != null && locked.entity == result) {
            val lastPlayerPos = locked.playerPos
            val lastEntityPos = locked.entityPos
            val preVec = lastEntityPos - lastPlayerPos
            val vec = result.pos - Player.pos
            val angle = vec.angle(preVec)
            val zeroAngle = vec == preVec

            if (!angle.isNaN()) {
                if (zeroAngle) {
                    if (AimAssistOption.zeroAngleFriction) FrictionAA.compensate(sensitivity, true)
                } else {
                    val facing = Vec3f(Player.yaw.toRadian(), Player.pitch.toRadian())

                    // Clamp range
                    val xzDelta = abs(vec.yaw - preVec.yaw).toDegree()
                    val xzRange = -xzDelta..xzDelta
                    val yDelta = abs(vec.pitch - preVec.pitch).toDegree()
                    val yRange = -yDelta..yDelta

                    // Vertical
                    val vRefPlane = Plane(Vec3f(0f, 1f, 0f), facing)
                    val vPre = vRefPlane.projectOnPlane(preVec)
                    val vNow = vRefPlane.projectOnPlane(vec)
                    val pitchOffset = ((vNow.pitch - vPre.pitch).toDegree() * feedbackRate).toFloat().coerceIn(yRange)
                    if (!pitchOffset.isNaN()) Player.pitch = (locked.pitch + pitchOffset).coerceIn(-89.5f..89.5f)

                    // Horizontal
                    if (xzDelta <= 90.0) {
                        val hRefPlane = Plane(Vec3f(1f, 0f, 0f), Vec3f(0f, 0f, 1f))
                        val hPre = hRefPlane.projectOnPlane(preVec)
                        val hNow = hRefPlane.projectOnPlane(vec)
                        val yawOffset = ((hNow.yaw - hPre.yaw).toDegree() * feedbackRate).toFloat().coerceIn(xzRange)
                        if (!yawOffset.isNaN()) Player.yaw = locked.yaw +
                                yawOffset * if (AimAssistOption.pitchOptimization) cos(Player.pitch.toRadian()) else 1f
                    } // Cancel
                }
            }
        } else Player.sensK = sensitivity * 1000.0
        if (result != null) lockEntity(result)
        else this.locked = null
    }

}