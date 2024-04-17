package net.spartanb312.boar.game.forge

import net.spartanb312.boar.physics.world.World
import net.spartanb312.boar.physics.world.WorldObject

object ForgeWorld : World {
    override val objects = mutableListOf<WorldObject>()
}