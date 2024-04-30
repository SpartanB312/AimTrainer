package net.spartanb312.everett.game.training

import net.spartanb312.everett.game.Player
import net.spartanb312.everett.game.render.crosshair.CrosshairRenderer
import net.spartanb312.everett.game.render.gui.Notification
import net.spartanb312.everett.game.render.gui.SubscribedRenderer
import net.spartanb312.everett.graphics.RenderSystem
import net.spartanb312.everett.graphics.texture.Texture

abstract class Training : SubscribedRenderer, TrainingInfoContainer {
    protected val leftUpInfo = mutableListOf(
        { "FPS:&f ${RenderSystem.averageFPS}" },
        { "Acc:&f ${String.format("%.2f", accuracy * 100)}% ($hits/$shots)" },
        { "Score:&f $showingScore" },
        { "Sens:&f ${String.format("%.3f", Player.sens)}" }
    )
    open val icon: Texture? = null
    val timeLapsed get() = System.currentTimeMillis() - startTime
    var startTime = System.currentTimeMillis()
    var stage = Stage.Prepare
    abstract val errorAngle: Float
    abstract fun render()
    abstract fun onClick()
    open fun onTick() {
        Stage.entries.forEach {
            if (timeLapsed in it.range) stage = it
        }
    }

    private fun showTitle() {
        Notification.showCenter(trainingName, 5000)
    }

    open fun reset() {
        Player.reset()
        CrosshairRenderer.disable()
        showTitle()
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