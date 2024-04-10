package net.spartanb312.boar.physics

import net.spartanb312.boar.physics.world.WorldObject
import net.spartanb312.boar.utils.math.vector.Vec3f

interface Controller : WorldObject {
    var pos: Vec3f
    fun update()
}