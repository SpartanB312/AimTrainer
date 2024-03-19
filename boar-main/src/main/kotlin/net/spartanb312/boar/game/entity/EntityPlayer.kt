package net.spartanb312.boar.game.entity

import net.spartanb312.boar.utils.math.vector.Vec3f

open class EntityPlayer : Entity(Vec3f.ZERO) {

    open var yaw: Float = 0f
    open var pitch: Float = 0f

    override fun raytrace(origin: Vec3f, ray: Vec3f, errorAngle: Float): Boolean {
        return false
    }

    override fun raytraceRate(origin: Vec3f, ray: Vec3f, errorAngle: Float): Float {
        return 0f
    }

}