package net.spartanb312.boar.physics

import net.spartanb312.boar.physics.world.Object
import net.spartanb312.boar.utils.math.vector.Vec3f

interface Controller : Object {
    var pos: Vec3f
    fun update()
}