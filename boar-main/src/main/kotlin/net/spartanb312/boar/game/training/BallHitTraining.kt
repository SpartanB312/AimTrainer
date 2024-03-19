package net.spartanb312.boar.game.training

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
import net.spartanb312.boar.utils.misc.asRange
import net.spartanb312.boar.utils.misc.random
import kotlin.math.roundToInt

abstract class BallHitTraining(
    private val scoreboardScreen: ScoreboardScreen,
    private val scene: Scene,
    private val amount: Int,
    private val sizeRange: ClosedFloatingPointRange<Float>,
    private val gap: Float,
    private val width: Int,
    private val height: Int,
    private val errorAngle: Float,
    private val renderAngle: Float = errorAngle,
    private val horizontalOffset: Float = 0f,
    private val verticalOffset: Float = 0f,
    private val distanceRange: ClosedFloatingPointRange<Float> = 50f.asRange,
    protected val ballHP: Int = 1,
    protected val fadeTime: Int = 100,
) : Training() {

    protected val entities get() = scene.entities
    protected val fadeBalls = mutableMapOf<Ball, Long>()
    protected val color = ColorRGB(255, 31, 63, 200) //ColorRGB(0, 220, 200, 192)
    protected val outlineC = color.alpha(224)

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
        entities.clear()
        repeat(amount) {
            entities.add(generateBall())
        }
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
            if (bulletAdsorption) renderAngle else 0f
        ) // Cache raytraced target
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
        scene.getRayTracedResult(
            Player.offsetPos,
            Player.camera.front,
            if (bulletAdsorption) errorAngle else 0f
        )?.let {
            if (it is Ball) {
                hit = true
                it.hp -= 1
                if (it.hp == 0) {
                    entities.add(generateBall(it))
                    entities.remove(it)
                    fadeBalls[it] = System.currentTimeMillis()
                }
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
                distanceRange.random(),
                -yOffset + yRange.random() * gap - gap / 2f,
                -zOffset + zRange.random() * gap - gap / 2f
            ), sizeRange.random(),
            ballHP
        )
        return if (ball == hitOn || entities.contains(ball) || fadeBalls.keys.contains(ball)) generateBall(hitOn)
        else ball
    }

    abstract fun onHit(timeLapse: Int): Int

    abstract fun onMiss(timeLapse: Int): Int


}