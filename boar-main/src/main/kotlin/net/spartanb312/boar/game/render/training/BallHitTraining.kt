package net.spartanb312.boar.game.render.training

import net.spartanb312.boar.game.Player
import net.spartanb312.boar.game.entity.Ball
import net.spartanb312.boar.game.option.impls.AimAssistOption.bulletAdsorption
import net.spartanb312.boar.game.render.BallRenderer
import net.spartanb312.boar.game.render.FontRendererBig
import net.spartanb312.boar.game.render.FontRendererMain
import net.spartanb312.boar.game.render.gui.Render2DManager
import net.spartanb312.boar.game.render.gui.impls.ScoreboardScreen
import net.spartanb312.boar.game.render.scene.Scene
import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.graphics.RenderSystem
import net.spartanb312.boar.utils.color.ColorRGB
import net.spartanb312.boar.utils.math.vector.Vec3f
import kotlin.math.roundToInt
import kotlin.random.Random

abstract class BallHitTraining(
    name: String,
    private val scoreboardScreen: ScoreboardScreen,
    private val scene: Scene,
    amount: Int,
    private val size: Float,
    private val gap: Float,
    private val width: Int,
    private val height: Int,
    private val horizontalOffset: Float = 0f,
    private val verticalOffset: Float = 0f,
    private val distance: Float = 50f,
    private val fadeTime: Int = 0,
) : Training(name) {

    private val entities get() = scene.entities
    private val fadeBalls = mutableMapOf<Ball, Long>()
    private val color = ColorRGB(255,31,63) //ColorRGB(0, 220, 200, 192)
    private val outlineC = color.alpha(224)
    protected open val errorOffset = { distance: Double, radius: Float ->
        if (distance < radius + 0.3f) true
        else if (distance < radius + 0.7f) {
            Random.nextDouble(0.0, 1.0) < 1 - (distance - radius - 0.3) / 0.4
        } else false
    }
    protected open val errorOffsetR = { distance: Double, radius: Float ->
        if (distance < radius) 1f
        else if (distance < radius + 0.7f) {
            (1f - (distance - radius) / 0.7f).toFloat()
        } else 0f
    }
    protected open val adsorptionOffset = { distance: Double, radius: Float -> distance < radius + 0.7f }

    override var shots = 0
    override var hits = 0
    override var score = 0
    private val hitTime = mutableListOf<Long>()
    private var lastShotTime = 0L
    private val reactionTimes = mutableListOf<Int>()
    override val showingScore get() = if (score > 0) (score * accuracy).roundToInt() else score

    init {
        repeat(amount) {
            entities.add(generateBall())
        }
    }

    override fun reset() {
        super.reset()
        hitTime.clear()
        reactionTimes.clear()
    }

    override fun render() {
        entities.forEach {
            if (it is Ball) BallRenderer.render(it.pos.x, it.pos.y, it.pos.z, it.size, color, true, outlineC)
        }
        fadeBalls.toList().forEach { (it, time) ->
            val alphaRate = 1f - ((System.currentTimeMillis() - time) / fadeTime.toFloat())
            if (alphaRate > 0f) BallRenderer.render(
                it.pos.x,
                it.pos.y,
                it.pos.z,
                it.size,
                color.alpha((alphaRate.coerceAtMost(1f) * 255).toInt()),
                false,
                outlineC
            ) else fadeBalls.remove(it)
        }
    }

    override fun render2D() {
        scene.getRayTracedResult(
            Player.offsetPos,
            Player.camera.front,
            if (bulletAdsorption) adsorptionOffset else { _, _ -> false },
            //if (bulletAdsorption) errorOffsetR else { _, _ -> 0f }
        )
        var startY = 0f
        val color = ColorRGB.AQUA
        leftUpInfo.forEach {
            val str = it.invoke()
            FontRendererMain.drawStringWithShadow(str, 0f, startY, color)
            startY += FontRendererMain.getHeight()
        }
        when (stage) {
            Stage.Prepare -> {
                val seconds = ((5000 - timeLapsed) / 1000f).roundToInt()
                FontRendererBig.drawCenteredString(
                    "0: 0$seconds",
                    RS.centerXF,
                    RS.centerYF - FontRendererBig.getHeight()
                )
            }

            Stage.Training -> {
                val seconds = ((65000 - timeLapsed) / 1000f).roundToInt()
                FontRendererBig.drawCenteredString(
                    if (seconds >= 10) "0: $seconds" else "0: 0$seconds",
                    RS.centerXF,
                    FontRendererBig.getHeight() / 2f
                )
            }

            else -> RenderSystem.addRenderThreadJob {
                Render2DManager.displayScreen(scoreboardScreen.apply {
                    scoreboard["Score"] = showingScore.toString()
                    scoreboard["Accuracy"] = String.format("%.2f", accuracy * 100) + "%"
                    scoreboard["Fired"] = shots.toString()
                    scoreboard["Hits"] = hits.toString()
                    scoreboard["Reaction Time"] = String.format("%.2f", reactionTimes.average())
                })
            }
        }
    }

    override fun onClick() {
        if (stage != Stage.Training || Render2DManager.displaying) return
        var hit = false
        scene.getRayTracedResult(Player.offsetPos,
            Player.camera.front,
            if (bulletAdsorption) errorOffset else { _, _ -> false },
            if (bulletAdsorption) errorOffsetR else { _, _ -> 0f })?.let {
            if (it is Ball) {
                hit = true
                entities.add(generateBall(it))
                entities.remove(it)
                fadeBalls[it] = System.currentTimeMillis()
            }
        }
        shots++
        val currentTime = System.currentTimeMillis()
        val lastHitTime = hitTime.lastOrNull() ?: (currentTime - 50L)
        val lastHitTimeLapse = (currentTime - lastHitTime).toInt()
        val lastShotTimeLapse = (currentTime - lastShotTime).toInt()
        if (hit) {
            score += onHit(lastHitTimeLapse)
            hitTime.add(currentTime)
            reactionTimes.add(lastHitTimeLapse)
            hits++
        } else score -= onMiss(lastShotTimeLapse)
        lastShotTime = currentTime
    }

    private fun generateBall(hitOn: Ball? = null): Ball {
        val zRange = 1..width
        val yRange = 1..height
        val zOffset = width * gap / 2f - horizontalOffset * gap
        val yOffset = height * gap / 2f - verticalOffset * gap
        val ball = Ball(
            Vec3f(
                distance,
                -yOffset + yRange.random() * gap - gap / 2f,
                -zOffset + zRange.random() * gap - gap / 2f
            ), size
        )
        return if (ball == hitOn || entities.contains(ball) || fadeBalls.keys.contains(ball)) generateBall(hitOn)
        else ball
    }

    abstract fun onHit(timeLapse: Int): Int

    abstract fun onMiss(timeLapse: Int): Int


}