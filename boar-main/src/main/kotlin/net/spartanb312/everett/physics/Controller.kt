package net.spartanb312.everett.physics

import net.spartanb312.everett.physics.world.WorldObject
import net.spartanb312.everett.utils.math.vector.Vec3f

interface Controller : WorldObject {
    var pos: Vec3f
    fun update()
}