package net.spartanb312.everett.game.entity

import net.spartanb312.everett.utils.math.vector.Vec3f

interface RayTraceable {
    fun raytrace(origin: Vec3f, ray: Vec3f,errorAngle:Float): Boolean
    fun raytraceRate(origin: Vec3f, ray: Vec3f, errorAngle: Float): Float
}