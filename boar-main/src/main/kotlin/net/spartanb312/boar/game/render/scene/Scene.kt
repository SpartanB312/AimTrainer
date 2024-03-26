package net.spartanb312.boar.game.render.scene

import net.spartanb312.boar.game.Player
import net.spartanb312.boar.game.entity.Entity
import net.spartanb312.boar.game.input.interfaces.*
import net.spartanb312.boar.game.option.impls.AimAssistOption
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
    open fun onClosed() {}
    override fun onMouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean = false
    override fun onMouseReleased(mouseX: Int, mouseY: Int, button: Int) {}
    override fun onKeyTyped(keyCode: Int, modifier: Int): Boolean = false
    override fun onKeyRepeating(key: Int, modifier: Int) {}
    override fun onKeyReleased(key: Int, modifier: Int) {}
    fun getRayTracedResults(
        origin: Vec3f,
        ray: Vec3f,
        errorAngle: Float
    ): List<Entity> {
        val results = mutableListOf<Pair<Entity, Float>>()
        entities.forEach {
            if (it.raytrace(origin, ray, errorAngle * AimAssistOption.errorAngleRate)) {
                results.add(Pair(it, it.raytraceRate(origin, ray, errorAngle * AimAssistOption.errorAngleRate)))
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
        errorAngle: Float
    ): Entity? = getRayTracedResults(origin, ray, errorAngle * AimAssistOption.errorAngleRate).firstOrNull()
}