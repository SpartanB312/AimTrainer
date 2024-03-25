package net.spartanb312.boar.physics.world

interface Object {
    fun intersects(obj: Object): Boolean
}