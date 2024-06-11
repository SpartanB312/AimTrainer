package net.spartanb312.everett.game

import net.spartanb312.everett.game.aimassist.FrictionAA
import net.spartanb312.everett.game.aimassist.MagnetismAA
import net.spartanb312.everett.game.entity.Entity
import net.spartanb312.everett.game.entity.EntityPlayer
import net.spartanb312.everett.game.option.impls.AimAssistOption
import net.spartanb312.everett.game.option.impls.ControlOption
import net.spartanb312.everett.graphics.Camera
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.matrix.newScope
import net.spartanb312.everett.physics.Controller
import net.spartanb312.everett.physics.world.BoundingBox
import net.spartanb312.everett.physics.world.Box
import net.spartanb312.everett.utils.math.ConvergeUtil.converge
import net.spartanb312.everett.utils.math.toRadian
import net.spartanb312.everett.utils.math.vector.Vec3f
import net.spartanb312.everett.utils.math.vector.toVec3d
import net.spartanb312.everett.utils.misc.AverageCounter
import net.spartanb312.everett.utils.timing.Timer
import org.lwjgl.glfw.GLFW
import kotlin.math.cos
import kotlin.math.sin

object Player : EntityPlayer(), Controller {

    val camera: Camera = Camera.HaloSeries
    var lastRayTracedTarget: Entity? = null
    var rayTracedRate: Float = 0f
    val offsetPos get() = pos.plus(0.001f, 0.001f, 0.001f)

    var sensK = -1.0
    val sens get() = sensK / 1000.0
    val raytraced get() = lastRayTracedTarget != null

    private var onGround = false
    val boundingBox
        get() = BoundingBox(
            pos.plus(-0.4f, -1.8f, -0.4f).toVec3d(),
            pos.plus(0.4f, 0.2f, 0.4f).toVec3d()
        )
    override val boundingBoxes: MutableList<Box> get() = mutableListOf(boundingBox)

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

    fun setPosition(position: Vec3f) {
        pos = position
        camera.cameraPos = pos
    }

    private val timer = Timer()
    private var eyeOffset = 0f

    fun move(xDiff: Float, yDiff: Float, zDiff: Float) {
        timer.passedAndRun(10) {
            eyeOffset = eyeOffset.converge(
                if (onGround && GLFW.glfwGetKey(RS.window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS) 100f
                else 0f,
                0.3f
            )
        }
        // Check movement
        var targetPos = pos.plus(xDiff, yDiff, zDiff)

        val prevBB = boundingBox

        var finalX = targetPos.x.toDouble()
        var finalY = targetPos.y.toDouble()
        var finalZ = targetPos.z.toDouble()

        if (yDiff != 0f) onGround = false
        TrainingWorld.objects.forEach { obj ->
            obj.boundingBoxes.forEach { box ->
                val targetBB = BoundingBox(
                    targetPos.plus(-0.4f, -1.8f, -0.4f).toVec3d(),
                    targetPos.plus(0.4f, 0.2f, 0.4f).toVec3d()
                )
                val xOverlaps = box.minX < prevBB.maxX && box.maxX > prevBB.minX
                val yOverlaps = box.minY < prevBB.maxY && box.maxY > prevBB.minY
                val zOverlaps = box.minZ < prevBB.maxZ && box.maxZ > prevBB.minZ

                if (box.intersects(targetBB)) {
                    // Check X
                    if (yOverlaps && zOverlaps) {
                        if (box.maxX < prevBB.minX) finalX = box.maxX + 0.40001f
                        else if (box.minX > prevBB.maxX) finalX = box.minX - 0.40001f
                    }
                    // Check Y
                    if (xOverlaps && zOverlaps) {
                        if (box.maxY < prevBB.minY) {
                            onGround = true
                            finalY = box.maxY + 1.80001f
                        } else if (box.minY > prevBB.maxY) finalY = box.minY - 0.20001f
                    }
                    // Check Z
                    if (xOverlaps && yOverlaps) {
                        if (box.maxZ < prevBB.minZ) finalZ = box.maxZ + 0.40001f
                        else if (box.minZ > prevBB.maxZ) finalZ = box.minZ - 0.40001f
                    }

                }
                targetPos = Vec3f(finalX, finalY, finalZ)
            }
        }
        pos = targetPos
        camera.cameraPos = pos
    }

    private val aaTickTimer = Timer()
    private val aaTPSCounter = AverageCounter(1000, 8)
    val aaTPS get() = aaTPSCounter.averageCPS

    private fun aimAssist(sensitivity: Double) {
        aaTPSCounter.invoke()
        if (AimAssistOption.mcEnabled) {
            MagnetismAA.compensate(sensitivity)
        } else if (AimAssistOption.frEnabled) FrictionAA.compensate(sensitivity)
    }

    fun project(
        fov: Float = camera.fov,
        sensitivity: Double = 2.2,
        dpiModifier: Double = 1.0,
        vRate: Float = 1.0f,
        hRate: Float = 1.0f,
        updateCamera: Boolean = true,
        block: Camera.() -> Unit
    ) {
        if (AimAssistOption.aimAssist.value) {
            if (AimAssistOption.noAATickLimit.value) aimAssist(sensitivity)
            else aaTickTimer.tps(AimAssistOption.aaTPS) {
                aimAssist(sensitivity)
            }
        } else sensK = sensitivity * 1000.0
        with(camera) {
            RS.matrixLayer.newScope.project(
                yaw,
                pitch,
                pos,
                fov,
                camera.zRange.start,
                camera.zRange.endInclusive,
                sens,
                dpiModifier,
                vRate,
                hRate,
                updateCamera,
                block
            )
        }
    }

    fun reset() {
        yaw = 0f
        pitch = 0f
        pos = Vec3f.ZERO
    }

    override var pitch by camera.pitchDelegate
    override var yaw by camera.yawDelegate

}