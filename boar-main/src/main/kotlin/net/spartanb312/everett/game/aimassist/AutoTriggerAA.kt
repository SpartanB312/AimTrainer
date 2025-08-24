package net.spartanb312.everett.game.aimassist

import net.spartanb312.everett.game.Player.lastRayTracedTarget
import net.spartanb312.everett.game.render.CrosshairRenderer

object AutoTriggerAA : AimAssist {

    override fun compensate(sensitivity: Double) {
        if (lastRayTracedTarget != null) CrosshairRenderer.onClick()
    }

}