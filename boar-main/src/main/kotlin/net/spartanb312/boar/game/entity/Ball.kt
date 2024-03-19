package net.spartanb312.boar.game.entity

import net.spartanb312.boar.game.entity.part.Sphere
import net.spartanb312.boar.utils.math.vector.Vec3f
import net.spartanb312.boar.utils.misc.random

class Ball(pos: Vec3f, var size: Float, var hp: Int) : Entity(pos) {

    private val body = Sphere(this, size)
    private var vec = Vec3f(0f, 0f, 0f)

    override fun raytrace(
        origin: Vec3f,
        ray: Vec3f,
        errorAngle: Float // DistanceToCenter, Radius
    ): Boolean = body.raytrace(origin, ray, errorAngle)

    fun randomMove() {
        val scale = 0.025f
        vec += Vec3f((-scale..scale).random(), (-scale..scale).random(), (-scale..scale).random())
        vec = Vec3f(
            vec.x.coerceIn((-10 * scale)..(10 * scale)),
            vec.y.coerceIn((-10 * scale)..(10 * scale)),
            vec.z.coerceIn((-10 * scale)..(10 * scale))
        )
        val range = -100f..100f
        pos += vec
        pos = Vec3f(pos.x.coerceIn(range), pos.y.coerceIn(range), pos.z.coerceIn(range))
    }

    override fun raytraceRate(origin: Vec3f, ray: Vec3f, errorAngle: Float): Float =
        body.raytraceRate(origin, ray, errorAngle)

    override fun equals(other: Any?): Boolean {
        return other is Ball && other.pos == pos
    }

    override fun hashCode(): Int {
        var result = pos.hashCode()
        result *= 31
        return result
    }

}