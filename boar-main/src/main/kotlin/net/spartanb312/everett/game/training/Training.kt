package net.spartanb312.everett.game.training

import net.spartanb312.everett.game.Player
import net.spartanb312.everett.game.option.impls.AccessibilityOption
import net.spartanb312.everett.game.render.CrosshairRenderer
import net.spartanb312.everett.game.render.NotificationRenderer
import net.spartanb312.everett.game.render.gui.SubscribedRenderer
import net.spartanb312.everett.graphics.RenderSystem
import net.spartanb312.everett.graphics.texture.Texture
import net.spartanb312.everett.physics.PhysicsSystem
import net.spartanb312.everett.utils.Logger

abstract class Training : SubscribedRenderer, TrainingInfoContainer {

    protected val leftUpInfo = mutableListOf(
        { "Average FPS:&f ${String.format("%.1f", RenderSystem.averageFPS)}" },
        { "Physics TPS:&f ${String.format("%.1f", PhysicsSystem.averageTPS)}" },
        { "AimAssist TPS:&f ${String.format("%.1f", Player.aaTPS)}" },
        { "Accuracy:&f ${String.format("%.2f", accuracy * 100)}% ($hits/$shots)" },
        { "Sensitivity:&f ${String.format("%.3f", Player.sens)}" },
        { "Score:&f $showingScore" },
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
        NotificationRenderer.showCenter(trainingName, waitTime * 1000L, false)
    }

    open fun reset(): Training {
        Logger.info("Start training: $trainingName")
        results.clear()
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

    protected val results = mutableMapOf<String, String>()

    operator fun set(title: String, value: String) {
        results[title] = value
    }

    enum class Stage {
        Prepare,
        Training,
        Finished
    }

}