package net.spartanb312.everett.game.forge

import net.spartanb312.everett.physics.world.World
import net.spartanb312.everett.physics.world.WorldObject

object ForgeWorld : World {
    override val objects = mutableListOf<WorldObject>()
}