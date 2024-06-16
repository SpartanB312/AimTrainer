package net.spartanb312.everett.game.training

import net.spartanb312.everett.game.medal.ClockStop
import net.spartanb312.everett.game.medal.FlawlessVictory
import net.spartanb312.everett.game.medal.MedalCounter
import net.spartanb312.everett.game.render.FontRendererBig
import net.spartanb312.everett.game.render.gui.Render2DManager
import net.spartanb312.everett.game.render.gui.impls.ScoreboardScreen
import net.spartanb312.everett.game.render.gui.impls.TrainingScreen
import net.spartanb312.everett.game.render.scene.Scene
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.drawing.RenderUtils
import net.spartanb312.everett.utils.color.ColorRGB
import kotlin.random.Random

class ReactionTest : Training(), TrainingInfoContainer by Companion {

    override val errorAngle = 0f
    override val showingScore = 0
    override var shots = 0
    override var hits = 0
    override var score = 0

    private val medalCounter = MedalCounter(500)

    companion object : TrainingInfo("Reaction Test", "Test your reaction", "Miscellaneous") {
        override fun new(scene: Scene): Training {
            return ReactionTest()
        }
    }

    override fun render() {
        // Nothing
    }

    enum class States {
        Waiting,
        Click,
        TooFast,
        Clicked,
        Finished
    }

    private var state = States.Waiting
    private var round = 1
    private var nextTime = System.nanoTime() + Random.nextLong(2000000000, 5000000000)
    private var lastReactionTime = 0L
    private val reactionTimeList = mutableListOf<Int>()
    private var displayed = false
    private var predict = 0

    override fun render2D() {
        if (round == 6) state = States.Finished
        if (state == States.Waiting && System.nanoTime() >= nextTime) state = States.Click
        when (state) {
            States.Waiting -> {
                RenderUtils.drawRect(
                    0,
                    0,
                    RS.width,
                    RS.height,
                    ColorRGB.DARK_RED.mix(ColorRGB.BLACK)
                )
                FontRendererBig.drawCenteredStringWithShadow("Waiting for green($round/5).", RS.centerX, RS.centerY)
            }

            States.Click -> {
                RenderUtils.drawRect(
                    0,
                    0,
                    RS.width,
                    RS.height,
                    ColorRGB.GREEN
                )
                FontRendererBig.drawCenteredStringWithShadow("Click now!!!", RS.centerX, RS.centerY)
            }

            States.TooFast -> {
                RenderUtils.drawRect(
                    0,
                    0,
                    RS.width,
                    RS.height,
                    ColorRGB.DARK_RED
                )
                FontRendererBig.drawCenteredStringWithShadow(
                    "Too early! Please wait for green.",
                    RS.centerX,
                    RS.centerY
                )
            }

            States.Clicked -> {
                RenderUtils.drawRect(
                    0,
                    0,
                    RS.width,
                    RS.height,
                    ColorRGB.BLUE
                )
                FontRendererBig.drawCenteredStringWithShadow(
                    "Reaction Time ${String.format("%.3f", lastReactionTime / 1000000f)}",
                    RS.centerX, RS.centerY
                )
            }

            States.Finished -> {
                RenderUtils.drawRect(
                    0,
                    0,
                    RS.width,
                    RS.height,
                    ColorRGB.BLUE
                )
                if (!displayed) {
                    displayed = true
                    TrainingScreen.endTraining()
                    displayScoreboard()
                }
            }
        }

    }

    private fun displayScoreboard() {
        this["Clock Stop"] = medalCounter.medals.getOrDefault(ClockStop, 0).toString()
        this["Fastest"] = String.format("%.3f", reactionTimeList.min() / 1000000f)
        this["Slowest"] = String.format("%.3f", reactionTimeList.max() / 1000000f)
        this["Average"] = String.format("%.3f", reactionTimeList.average() / 1000000f)
        this["Predict"] = predict.toString()
        var score = predict * -100
        reactionTimeList.forEach {
            val ms = it / 1000000f
            score += if (ms < 180) (20000 / ms).toInt()
            else if (ms < 250) (15000 / ms).toInt()
            else (20000 / ms).toInt()
        }
        if (predict == 0) medalCounter.pushMedal(FlawlessVictory)
        Render2DManager.displayScreen(
            ScoreboardScreen(
                score,
                category,
                trainingName,
                (timeLapsed / 1000f).toInt(),
                results,
                medalCounter
            )
        )
    }

    override fun onClick() {
        if (Render2DManager.currentScreen != null) return
        val currentTime = System.nanoTime()
        when (state) {
            States.Waiting -> {
                predict++
                state = States.TooFast
            }

            States.Click -> {
                round++
                val took = currentTime - nextTime
                lastReactionTime = took
                reactionTimeList.add(took.toInt())
                if (took < 160000000) medalCounter.pushMedal(ClockStop)
                state = States.Clicked
            }

            States.TooFast -> {
                state = States.Waiting
                nextTime = currentTime + Random.nextLong(2000000000, 5000000000)
            }

            States.Clicked -> {
                state = States.Waiting
                nextTime = currentTime + Random.nextLong(2000000000, 5000000000)
            }

            States.Finished -> {}
        }
    }

    override fun reset(): Training {
        state = States.Waiting
        medalCounter.reset()
        nextTime = System.nanoTime() + Random.nextLong(2000000000, 5000000000)
        predict = 0
        round = 1
        lastReactionTime = 0L
        reactionTimeList.clear()
        displayed = false
        return this
    }

}