package net.spartanb312.boar.game

import net.spartanb312.boar.game.entity.Entity
import net.spartanb312.boar.game.entity.EntityPlayer
import net.spartanb312.boar.game.option.impls.AimAssistOption
import net.spartanb312.boar.game.render.gui.Render2DManager
import net.spartanb312.boar.game.render.scene.SceneManager
import net.spartanb312.boar.game.render.scene.impls.AimTrainingScene
import net.spartanb312.boar.graphics.Camera
import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.utils.math.ConvergeUtil.converge
import net.spartanb312.boar.utils.math.vector.Vec3f
import net.spartanb312.boar.utils.timing.Timer
import org.lwjgl.glfw.GLFW

object Player : EntityPlayer() {

    val camera: Camera = Camera.Default
    var lastRayTracedTarget: Entity? = null
    var rayTracedRate: Float = 0f
    val offsetPos get() = pos.plus(0.001f, 0.001f, 0.001f)

    private var sensK = -1.0
    private val aaTimer = Timer()
    val sens get() = sensK / 1000.0
    val raytraced get() = lastRayTracedTarget != null

    fun project(
        yaw: Float = camera.yaw,
        pitch: Float = camera.pitch,
        position: Vec3f = camera.cameraPos,
        fov: Float = camera.fov,
        zNear: Float = camera.zRange.start,
        zFar: Float = camera.zRange.endInclusive,
        sensitivity: Double = 2.2,
        vRate: Float = 1.0f,
        hRate: Float = 1.0f,
        updateCamera: Boolean = true,
        block: Camera.() -> Unit
    ) {
        //if (rayTracedRate != 0f && rayTracedRate != 1f) println(rayTracedRate)
        aaTimer.passedAndReset(5) {
            sensK = if (sensK == -1.0) sensitivity * 1000.0
            else {
                val firing = Render2DManager.currentScreen == null
                        && (SceneManager.currentScene == AimTrainingScene || SceneManager.currentScene is Academy.AcademyScene)
                        && GLFW.glfwGetMouseButton(RS.window, GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS
                val targetAA = when {
                    raytraced && firing -> AimAssistOption.firingCoefficient
                    raytraced && !firing -> AimAssistOption.raytraceCoefficient
                    !raytraced && firing -> AimAssistOption.raytraceCoefficient
                    else -> 1.0
                }
                if (targetAA == 1.0) sensK.converge(sensitivity * 1000.0, 0.3)
                else sensK.converge(sensitivity * targetAA * 1000.0, 0.5)
            }
        }
        camera.project(yaw, pitch, position, fov, zNear, zFar, sens, vRate, hRate, updateCamera, block)
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