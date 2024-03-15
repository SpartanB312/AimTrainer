package net.spartanb312.boar.game.entity

import net.spartanb312.boar.utils.math.vector.Vec3f
import net.spartanb312.boar.utils.math.vector.distanceTo
import kotlin.math.sqrt

class Ball(pos: Vec3f, val size: Float) : Entity(pos) {

    override fun intersects(
        origin: Vec3f,
        ray: Vec3f,
        offset: (Double, Float) -> Boolean // DistanceToCenter, Radius
    ): Boolean {
        val distance = origin.distanceTo(pos)
        if (distance < size) return true
        val direct = pos - origin
        val cos = (direct dot ray) / (ray.length * direct.length)
        val s = cos * distance
        val l = sqrt(distance * distance - s * s)
        return if (l < size) true
        else offset.invoke(l, size)
    }

    override fun equals(other: Any?): Boolean {
        return other is Ball && other.pos == pos && other.size == size
    }

    override fun hashCode(): Int {
        var result = pos.hashCode()
        result = 31 * result + size.hashCode()
        return result
    }

}