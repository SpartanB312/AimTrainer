package net.spartanb312.everett.game.entity.part

import net.spartanb312.everett.game.entity.Entity
import net.spartanb312.everett.game.entity.RayTraceable
import net.spartanb312.everett.utils.math.toRadian
import net.spartanb312.everett.utils.math.vector.Vec3f
import net.spartanb312.everett.utils.math.vector.distanceTo
import kotlin.math.asin

class Sphere(val entity: Entity, var radius: Float) : RayTraceable {

    private val pos get() = entity.pos

    override fun raytrace(origin: Vec3f, ray: Vec3f, errorAngle: Float): Boolean {
        val distance = origin.distanceTo(pos)
        if (distance < radius) return true
        val direct = pos - origin
        val currentAngle = direct.angle(ray)
        val maxAngle = asin(radius / distance) + errorAngle.toRadian()
        return currentAngle < maxAngle
    }

    override fun raytraceRate(origin: Vec3f, ray: Vec3f, errorAngle: Float): Float {
        val distance = origin.distanceTo(pos)
        if (distance < radius) return 1f
        val direct = pos - origin
        val currentAngle = direct.angle(ray)
        val innerAngle = asin(radius / distance)
        val maxAngle = innerAngle + errorAngle.toRadian()
        return if (currentAngle < innerAngle) 1f
        else if (currentAngle < maxAngle) {
            val angleDiff = currentAngle - innerAngle
            (angleDiff / errorAngle.toRadian()).toFloat()
        } else 0f
    }

}