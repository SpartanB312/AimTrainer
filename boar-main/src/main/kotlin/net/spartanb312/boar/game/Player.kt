package net.spartanb312.boar.game

import net.spartanb312.boar.game.aimassist.HaloInfiniteAA
import net.spartanb312.boar.game.entity.Entity
import net.spartanb312.boar.game.entity.EntityPlayer
import net.spartanb312.boar.game.option.impls.AimAssistOption
import net.spartanb312.boar.graphics.Camera
import net.spartanb312.boar.utils.math.vector.Vec3f

object Player : EntityPlayer() {

    val camera: Camera = Camera.Default
    var lastRayTracedTarget: Entity? = null
    var rayTracedRate: Float = 0f
    val offsetPos get() = pos.plus(0.001f, 0.001f, 0.001f)

    var sensK = -1.0
    val sens get() = sensK / 1000.0
    val raytraced get() = lastRayTracedTarget != null

    private val composition = HaloInfiniteAA()

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