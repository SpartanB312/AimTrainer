package net.spartanb312.everett.game.medal

import net.spartanb312.everett.game.entity.Entity
import net.spartanb312.everett.game.render.MedalRenderer
import net.spartanb312.everett.utils.timing.Timer

class MedalCounter(
    private val killResetTime: Int = 300
) {

    private val medals = mutableMapOf<Medal, Int>()

    fun reset() {
        medals.clear()
        killCount = 0
        killTimer.reset()
    }

    // Kill
    private val killTimer = Timer()
    private var killCount = 0
    fun triggerKill() {
        if (killTimer.passed(killResetTime)) killCount = 1
        else {
            killCount++
            if (killCount > 10) killCount = 1
            KillMedal.getMedal(killCount)?.let {
                val originCount = medals.getOrPut(it) { 0 }
                medals[it] = originCount + 1
                MedalRenderer.pushMedal(it)
            }
        }
        killTimer.reset()
    }

    // Perfect
    private var dmrEntity: Entity? = null
    private var dmrShots = 0
    fun perfectDMR(entity: Entity?) {
        if (entity == null) {
            dmrEntity = null
            return
        }
        if (dmrEntity != entity) {
            dmrEntity = entity
            dmrShots = 1
        } else dmrShots++
        if (dmrShots == 5) {
            dmrShots = 0
            dmrEntity = null
            val originCount = medals.getOrPut(Perfect) { 0 }
            medals[Perfect] = originCount + 1
            MedalRenderer.pushMedal(Perfect)
        }
    }

}