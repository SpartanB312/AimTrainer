package net.spartanb312.everett.game.entity

import net.spartanb312.everett.game.Player
import net.spartanb312.everett.game.entity.part.Sphere
import net.spartanb312.everett.utils.math.vector.Vec3f
import net.spartanb312.everett.utils.math.vector.distanceTo
import net.spartanb312.everett.utils.misc.random
import kotlin.math.absoluteValue

open class Ball(pos: Vec3f, var size: Float, var hp: Int) : Entity(pos) {

    private val body = Sphere(this, size)
    private val oneShot = hp == 1
    var vec = Vec3f.ZERO

    var isAlive = true

    override fun raytrace(
        origin: Vec3f,
        ray: Vec3f,
        errorAngle: Float // DistanceToCenter, Radius
    ): Boolean = body.raytrace(origin, ray, if (!oneShot && hp == 1) errorAngle / 4f else errorAngle)

    fun move(vec: Vec3f, reverse: Boolean) {
        this.pos = if (reverse) this.pos - vec
        else this.pos + vec
    }

    fun applyMovement(ratio: Float) {
        pos += vec.times(ratio)
    }

    fun reverseVec() {
        vec = vec.times(-1f)
    }

    fun updateVec(reverse: Boolean, moveSpeed: Float) {
        val scale = 0.01f * moveSpeed
        fun Float.correct(multiplier: Float = 1f): Float {
            val fixedScale = scale * multiplier
            val temp = this.coerceIn((-10 * fixedScale)..(10 * fixedScale))
            return if (temp.absoluteValue <= 3 * fixedScale) (temp / temp.absoluteValue) * 3 * fixedScale else temp
        }
        if (reverse) vec -= (vec * 2f)
        vec += Vec3f((-scale..scale).random(), (-scale..scale).random() * 0.1f, (-scale..scale).random())
        vec = Vec3f(vec.x.correct(), vec.y.correct(0.3f), vec.z.correct())
        val range = -50f..50f
        var notInRange = 0
        if (pos.x !in range) notInRange++
        if (pos.y !in range) notInRange++
        if (pos.z !in range) notInRange++
        if (notInRange >= 2 && !reverse) {
            applyMovement(-1f)
            updateVec(true, moveSpeed)
        } else pos = Vec3f(pos.x.coerceIn(range), pos.y.coerceIn(range), pos.z.coerceIn(range))
        if (pos.distanceTo(Player.pos) <= 10f) {
            val norm = vec.normalize()
            vec = Vec3f(
                (norm.x * (pos.x - Player.pos.x)).correct(),
                (norm.y * (pos.y - Player.pos.y)).correct(),
                (norm.z * (pos.z - Player.pos.z)).correct()
            )
        }
    }

    override fun raytraceRate(origin: Vec3f, ray: Vec3f, errorAngle: Float): Float =
        body.raytraceRate(origin, ray, errorAngle)

    override fun raytraceAngle(origin: Vec3f, ray: Vec3f): Float =
        body.raytraceAngle(origin, ray)

    override fun equals(other: Any?): Boolean {
        return other is Ball && other.pos == pos
    }

    override fun hashCode(): Int {
        var result = pos.hashCode()
        result *= 31
        return result
    }

}