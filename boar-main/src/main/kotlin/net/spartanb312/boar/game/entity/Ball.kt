package net.spartanb312.boar.game.entity

import net.spartanb312.boar.game.entity.part.Sphere
import net.spartanb312.boar.utils.math.vector.Vec3f

class Ball(pos: Vec3f, var size: Float) : Entity(pos) {

    private val body = Sphere(this, size)

    override fun raytrace(
        origin: Vec3f,
        ray: Vec3f,
        errorAngle:Float // DistanceToCenter, Radius
    ): Boolean = body.raytrace(origin, ray, errorAngle)

    override fun raytraceRate(origin: Vec3f, ray: Vec3f, errorAngle:Float): Float =
        body.raytraceRate(origin, ray, errorAngle)

    override fun equals(other: Any?): Boolean {
        return other is Ball && other.pos == pos && other.size == size
    }

    override fun hashCode(): Int {
        var result = pos.hashCode()
        result = 31 * result + size.hashCode()
        return result
    }

}