package net.spartanb312.boar.game

import net.spartanb312.boar.game.aimassist.HaloInfiniteAA
import net.spartanb312.boar.game.entity.Entity
import net.spartanb312.boar.game.entity.EntityPlayer
import net.spartanb312.boar.game.option.impls.AimAssistOption
import net.spartanb312.boar.game.option.impls.ControlOption
import net.spartanb312.boar.graphics.Camera
import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.physics.Controller
import net.spartanb312.boar.physics.world.Object
import net.spartanb312.boar.utils.math.toRadian
import net.spartanb312.boar.utils.math.vector.Vec3f
import org.lwjgl.glfw.GLFW
import kotlin.math.cos
import kotlin.math.sin

object Player : EntityPlayer(), Controller {

    val camera: Camera = Camera.Default
    var lastRayTracedTarget: Entity? = null
    var rayTracedRate: Float = 0f
    val offsetPos get() = pos.plus(0.001f, 0.001f, 0.001f)

    var sensK = -1.0
    val sens get() = sensK / 1000.0
    val raytraced get() = lastRayTracedTarget != null

    private val composition = HaloInfiniteAA()

    override fun update() {
        val facing = Vec3f(cos(yaw.toRadian()), 0, sin(yaw.toRadian()))
        val rightV = Vec3f(0, 1, 0) cross facing

        val moveSpeed = ControlOption.moveSpeed
        val ySpeed = ControlOption.ySpeed

        var xDiff = 0.0
        var yDiff = 0.0
        var zDiff = 0.0

        if (GLFW.glfwGetKey(RS.window, GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
            xDiff += facing.x * moveSpeed
            zDiff += facing.z * moveSpeed
        }
        if (GLFW.glfwGetKey(RS.window, GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS) {
            xDiff -= facing.x * moveSpeed
            zDiff -= facing.z * moveSpeed
        }
        if (GLFW.glfwGetKey(RS.window, GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS) {
            xDiff += rightV.x * moveSpeed
            zDiff += rightV.z * moveSpeed
        }
        if (GLFW.glfwGetKey(RS.window, GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS) {
            xDiff -= rightV.x * moveSpeed
            zDiff -= rightV.z * moveSpeed
        }
        if (GLFW.glfwGetKey(RS.window, GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS) {
            yDiff += moveSpeed * ySpeed
        }
        if (GLFW.glfwGetKey(RS.window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS) {
            yDiff -= moveSpeed * ySpeed
        }
        move(xDiff.toFloat(), yDiff.toFloat(), zDiff.toFloat())
    }

    override fun intersects(obj: Object): Boolean {
        return false
    }

    fun setPosition(position: Vec3f) {
        pos = position
        camera.cameraPos = pos
    }

    fun move(x: Float, y: Float, z: Float) {
        pos = pos.plus(x, y, z)
        camera.cameraPos = pos
    }

    fun project(
        fov: Float = camera.fov,
        sensitivity: Double = 2.2,
        vRate: Float = 1.0f,
        hRate: Float = 1.0f,
        updateCamera: Boolean = true,
        block: Camera.() -> Unit
    ) {
        sensK = sensitivity * 1000.0
        if (AimAssistOption.aimAssist.value) {
            composition.aimComposite(sensitivity)
            composition.moveComposite()
        }
        camera.project(
            yaw,
            pitch,
            pos,
            fov,
            camera.zRange.start,
            camera.zRange.endInclusive,
            sens,
            vRate,
            hRate,
            updateCamera,
            block
        )
    }

    override var pitch: Float
        get() = camera.pitch
        set(value) {
            camera.pitch = value
        }

    override var yaw: Float
        get() = camera.yaw
        set(value) {
            camera.yaw = value
        }

}