package net.spartanb312.boar.game.entity.part

import net.spartanb312.boar.game.entity.Entity
import net.spartanb312.boar.game.entity.RayTraceable
import net.spartanb312.boar.utils.math.vector.Vec3f
import net.spartanb312.boar.utils.math.vector.distanceTo
import kotlin.math.sqrt

class Sphere(val entity: Entity, var radius: Float) : RayTraceable {

    private val pos get() = entity.pos

    override fun raytrace(origin: Vec3f, ray: Vec3f, sphereOffset: (Double, Float) -> Boolean): Boolean {
        val distance = origin.distanceTo(pos)
        if (distance < radius) return true
        val direct = pos - origin
        val cos = (direct dot ray) / (ray.length * direct.length)
        val s = cos * distance
        val l = sqrt(distance * distance - s * s)
        return if (l < radius) true
        else sphereOffset.invoke(l, radius)
    }

    override fun raytraceRate(origin: Vec3f, ray: Vec3f, sphereOffsetR: (Double, Float) -> Float): Float {
        val distance = origin.distanceTo(pos)
        if (distance < radius) return 1f
        val direct = pos - origin
        val cos = (direct dot ray) / (ray.length * direct.length)
        val s = cos * distance
        val l = sqrt(distance * distance - s * s)
        return if (l < radius) 1f
        else sphereOffsetR.invoke(l, radius)
    }

}