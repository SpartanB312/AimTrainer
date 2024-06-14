package net.spartanb312.everett.game.render

import com.soywiz.kds.iterators.fastForEachReverse
import net.spartanb312.everett.game.medal.EmptyMedal
import net.spartanb312.everett.game.medal.Medal
import net.spartanb312.everett.game.render.scene.SceneManager
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.texture.drawTexture
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.timing.Timer
import kotlin.math.max

object MedalRenderer {

    private val renderList = mutableListOf<RenderSession>()
    private var scoreTimer = Timer()
    private var renderScore = 0

    private val dogsNames = listOf(
        "aPG",
        "Luciid TW",
        "Renegade JW",
        "Sparty McFIy",
        "Jimbo",
        "WuTum",
        "Agent416SHD",
        "Acid XM",
        "Mint Blitz"
    )
    val randomName get() = dogsNames.random()

    class RenderSession(val medal: Medal, private val endTime: Long, private val fadeOutTime: Int = 500) {
        var shouldRemove = false
        fun getAlphaRate(): Float {
            val currentTime = System.currentTimeMillis()
            return if (currentTime < endTime) 1f
            else if (currentTime < endTime + fadeOutTime) 1f - (currentTime - endTime).toFloat() / fadeOutTime
            else {
                shouldRemove = true
                0f
            }
        }
    }

    fun onRender() {
        if (!SceneManager.inTraining) return
        renderList.removeIf { it.shouldRemove }

        val scale = max(RS.widthF / 2560f, RS.heightF / 1369f)
        val startX = RS.widthF * 0.37f
        var startY = RS.heightF * 0.475f
        // Score
        if (renderList.isEmpty() && scoreTimer.passed(1000)) renderScore = 0
        else {
            val str = renderScore.toString()
            val width = FontRendererBig.getWidth(str, scale)
            FontRendererBig.drawStringWithShadow(
                renderScore.toString(),
                startX + 40f * scale - width,
                startY - 50f * scale,
                scale = 0.8f * scale
            )
        }

        // Medals
        renderList.fastForEachReverse {
            val alpha = (255f * it.getAlphaRate()).toInt()
            val width = FontRendererMain.getWidth(it.medal.str, scale)
            val height = FontRendererMain.getHeight(1.5f * scale)
            FontRendererMain.drawStringWithShadow(
                it.medal.str,
                if (it.medal is EmptyMedal) startX - width + 20f * scale else startX - width,
                startY + height * 0.15f,
                if (it.medal is EmptyMedal) it.medal.color.alpha(alpha) else ColorRGB.WHITE.alpha(alpha),
                scale = scale
            )
            it.medal.texture?.drawTexture(
                startX + 20f * scale,
                startY,
                startX + 20f * scale + height,
                startY + height,
                colorRGB = ColorRGB.WHITE.alpha(alpha)
            )

            startY += height
        }
    }

    fun pushScore(score: Int) {
        scoreTimer.reset()
        renderScore += score
    }

    fun pushMessage(string: String, renderTime: Int = 2000, fadeOutTime: Int = 500, color: ColorRGB) {
        renderList.add(RenderSession(EmptyMedal(string, color), System.currentTimeMillis() + renderTime, fadeOutTime))
    }

    fun pushMedal(medal: Medal, renderTime: Int = 2000, fadeOutTime: Int = 500) {
        renderList.add(RenderSession(medal, System.currentTimeMillis() + renderTime, fadeOutTime))
    }

}