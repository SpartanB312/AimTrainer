package net.spartanb312.boar.game.training

import net.spartanb312.boar.game.Player
import net.spartanb312.boar.game.render.gui.SubscribedRenderer
import net.spartanb312.boar.graphics.RenderSystem

abstract class Training(val name: String) : SubscribedRenderer {
    protected val leftUpInfo = mutableListOf(
        { "FPS:&f ${RenderSystem.averageFPS}" },
        { "Acc:&f ${String.format("%.2f", accuracy * 100)}% ($hits/$shots)" },
        { "Score:&f $showingScore" },
        { "Sens:&f ${String.format("%.3f", Player.sens)}" }
    )
    val timeLapsed get() = System.currentTimeMillis() - startTime
    var startTime = System.currentTimeMillis()
    var stage = Stage.Prepare
    abstract fun render()
    abstract fun onClick()
    open fun onTick() {
        Stage.entries.forEach {
            if (timeLapsed in it.range) stage = it
        }
    }

    open fun reset() {
        startTime = System.currentTimeMillis()
        stage = Stage.Prepare
        shots = 0
        hits = 0
        score = 0
    }

    abstract var score: Int
    abstract var hits: Int
    abstract var shots: Int
    abstract val showingScore: Int
    val accuracy get() = hits / shots.coerceAtLeast(1).toFloat()

    enum class Stage(val range: IntRange) {
        Prepare(0..5000),
        Training(5000..65000),
        Finished(65000..Int.MAX_VALUE)
    }
}