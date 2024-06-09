package net.spartanb312.everett.game.training

import net.spartanb312.everett.game.Player
import net.spartanb312.everett.game.option.impls.AccessibilityOption
import net.spartanb312.everett.game.render.crosshair.CrosshairRenderer
import net.spartanb312.everett.game.render.gui.Notification
import net.spartanb312.everett.game.render.gui.SubscribedRenderer
import net.spartanb312.everett.graphics.RenderSystem
import net.spartanb312.everett.graphics.texture.Texture
import net.spartanb312.everett.utils.Logger

abstract class Training : SubscribedRenderer, TrainingInfoContainer {
    protected val leftUpInfo = mutableListOf(
        { "FPS:&f ${RenderSystem.averageFPS}" },
        { "Yaw:&f ${String.format("%.3f", Player.yaw)}" },
        { "Pitch:&f ${String.format("%.3f", Player.pitch)}" },
        { "Acc:&f ${String.format("%.2f", accuracy * 100)}% ($hits/$shots)" },
        { "Score:&f $showingScore" },
        { "Sens:&f ${String.format("%.3f", Player.sens)}" },
    )
    protected var waitTime = AccessibilityOption.waitTime
    open val icon: Texture? = null
    val timeLapsed get() = System.currentTimeMillis() - startTime
    var startTime = System.currentTimeMillis()
    var stage = Stage.Prepare
    abstract val errorAngle: Float
    abstract fun render()
    abstract fun onClick()
    open fun onTick() {
        stage = if (timeLapsed <= waitTime * 1000) Stage.Prepare
        else if (timeLapsed <= waitTime * 1000 + 60000) Stage.Training
        else Stage.Finished
    }

    private fun showTitle() {
        Notification.showCenter(trainingName, waitTime * 1000L, false)
    }

    open fun reset(): Training {
        Logger.info("Start training: $trainingName")
        waitTime = AccessibilityOption.waitTime
        Player.reset()
        CrosshairRenderer.disable()
        showTitle()
        startTime = System.currentTimeMillis()
        stage = Stage.Prepare
        shots = 0
        hits = 0
        score = 0
        return this
    }

    abstract var score: Int
    abstract var hits: Int
    abstract var shots: Int
    abstract val showingScore: Int
    val accuracy get() = hits / shots.coerceAtLeast(1).toFloat()

    enum class Stage {
        Prepare,
        Training,
        Finished
    }
}