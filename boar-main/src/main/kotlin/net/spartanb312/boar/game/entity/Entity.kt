package net.spartanb312.boar.game.entity

import net.spartanb312.boar.utils.math.vector.Vec3f

abstract class Entity(val pos: Vec3f) {
    abstract fun intersects(origin: Vec3f, ray: Vec3f, offset: (Double, Float) -> Boolean = { _, _ -> false }): Boolean
}