package net.spartanb312.boar.game.render.scene

import net.spartanb312.boar.game.Player
import net.spartanb312.boar.game.entity.Entity
import net.spartanb312.boar.game.input.interfaces.*
import net.spartanb312.boar.game.render.gui.SubscribedRenderer
import net.spartanb312.boar.utils.math.vector.Vec3f
import net.spartanb312.boar.utils.math.vector.distanceTo

abstract class Scene :
    KeyReleaseListener,
    KeyRepeatingListener,
    KeyTypedListener,
    MouseClickListener,
    MouseReleaseListener,
    SubscribedRenderer {
    val entities: MutableList<Entity> = mutableListOf()
    abstract fun render3D()
    open fun onTick() {}
    open fun onInit() {}
    override fun onMouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean = false
    override fun onMouseReleased(mouseX: Int, mouseY: Int, button: Int) {}
    override fun onKeyTyped(keyCode: Int, modifier: Int): Boolean = false
    override fun onKeyRepeating(key: Int, modifier: Int) {}
    override fun onKeyReleased(key: Int, modifier: Int) {}
    fun getRayTracedResults(
        origin: Vec3f,
        ray: Vec3f,
        sphereOffset: (Double, Float) -> Boolean = { _, _ -> false }, // Only for sphere TODO: Make Offset class
        sphereOffsetR: (Double, Float) -> Float = { _, _ -> 0f }
    ): List<Entity> {
        val results = mutableListOf<Pair<Entity, Float>>()
        entities.forEach {
            if (it.raytrace(origin, ray, sphereOffset)) {
                results.add(Pair(it, it.raytraceRate(origin, ray, sphereOffsetR)))
            }
        }
        results.sortBy { origin.distanceTo(it.first.pos) }
        val result = results.firstOrNull()
        Player.lastRayTracedTarget = result?.first
        Player.rayTracedRate = result?.second ?: 0f

        return results.map { it.first }
    }

    fun getRayTracedResult(
        origin: Vec3f,
        ray: Vec3f,
        sphereOffset: (Double, Float) -> Boolean = { _, _ -> false },
        sphereOffsetR: (Double, Float) -> Float = { _, _ -> 0f }
    ): Entity? = getRayTracedResults(origin, ray, sphereOffset, sphereOffsetR).firstOrNull()
}