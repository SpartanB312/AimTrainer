package net.spartanb312.everett.game.entity

import net.spartanb312.everett.utils.math.vector.Vec3f

abstract class Entity(open var pos: Vec3f) : RayTraceable {
    var isRaytraced = false
}