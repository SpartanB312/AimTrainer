package net.spartanb312.boar.game.entity

import net.spartanb312.boar.utils.math.vector.Vec3f

interface RayTraceable {
    fun raytrace(origin: Vec3f, ray: Vec3f, sphereOffset: (Double, Float) -> Boolean = { _, _ -> false }): Boolean
    fun raytraceRate(origin: Vec3f, ray: Vec3f, sphereOffsetR: (Double, Float) -> Float = { _, _ -> 0f }): Float
}