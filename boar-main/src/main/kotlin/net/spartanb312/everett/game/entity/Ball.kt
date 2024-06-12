package net.spartanb312.everett.game.entity

import net.spartanb312.everett.game.entity.part.Sphere
import net.spartanb312.everett.utils.math.vector.Vec3f
import net.spartanb312.everett.utils.misc.random
import kotlin.math.absoluteValue

class Ball(pos: Vec3f, var size: Float, var hp: Int) : Entity(pos) {

    private val body = Sphere(this, size)
    private var vec = Vec3f(0f, 0f, 0f)

    var isAlive = true

    override fun raytrace(
        origin: Vec3f,
        ray: Vec3f,
        errorAngle: Float // DistanceToCenter, Radius
    ): Boolean = body.raytrace(origin, ray, errorAngle)

    fun move(vec: Vec3f, reverse: Boolean) {
        this.pos = if (reverse) this.pos - vec
        else this.pos + vec
    }

    fun randomMove(reverse: Boolean, moveSpeed: Float) {
        val scale = 0.01f * moveSpeed
        vec += Vec3f((-scale..scale).random(), (-scale..scale).random(), (-scale..scale).random())
        fun Float.correct(): Float {
            val temp = this.coerceIn((-10 * scale)..(10 * scale))
            return if (temp.absoluteValue <= 3 * scale) (temp / temp.absoluteValue) * 3 * scale else temp
        }
        vec = Vec3f(vec.x.correct(), vec.y.correct(), vec.z.correct())
        if (reverse) {
            vec -= (vec * 2f)
        }
        val range = -50f..50f
        pos += vec
        var notInRange = 0
        if (pos.x !in range) notInRange++
        if (pos.y !in range) notInRange++
        if (pos.z !in range) notInRange++
        if (notInRange >= 2) randomMove(!reverse, moveSpeed)
        else pos = Vec3f(pos.x.coerceIn(range), pos.y.coerceIn(range), pos.z.coerceIn(range))
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