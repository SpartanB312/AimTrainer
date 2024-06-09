package net.spartanb312.everett.game.render.gui.impls

import net.spartanb312.everett.game.Configs
import net.spartanb312.everett.game.option.impls.ControlOption.defaultTraining
import net.spartanb312.everett.game.render.Background
import net.spartanb312.everett.game.render.FontRendererMain
import net.spartanb312.everett.game.render.FontRendererROG
import net.spartanb312.everett.game.render.crosshair.CrosshairRenderer
import net.spartanb312.everett.game.render.gui.GuiScreen
import net.spartanb312.everett.game.render.gui.Render2DManager
import net.spartanb312.everett.game.render.scene.SceneManager
import net.spartanb312.everett.game.render.scene.impls.AimTrainingScene
import net.spartanb312.everett.game.render.scene.impls.DummyScene
import net.spartanb312.everett.game.training.ReactionTest
import net.spartanb312.everett.game.training.TrainingInfo
import net.spartanb312.everett.game.training.impls.*
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.drawing.RenderUtils
import net.spartanb312.everett.utils.color.ColorRGB
import org.lwjgl.glfw.GLFW
import kotlin.math.max

object TrainingScreen : GuiScreen() {

    val trainings: List<TrainingInfo> = listOf(
        Grid,
        Grid3x3,
        BallFollowing,
        SixShotWall,
        SixShotSphere,
        SixBallDMR,
        OneShotWall,
        OneShotSphere,
        OneBallDMR,
        ReactionTest
    )
    private var clickArea = mutableListOf<Pair<ClosedFloatingPointRange<Float>, ClosedFloatingPointRange<Float>>>()

    override fun onRender(mouseX: Double, mouseY: Double) {
        Background.renderBackground(mouseX, mouseY)

        val scale = max(RS.widthF / 2560f, RS.heightF / 1369f)
        FontRendererROG.drawCenteredStringWithShadow(
            "Trainings",
            RS.width / 2f,
            RS.heightF * 0.05f,
            scale = scale
        )
        val clampYT = RS.heightF * 0.1f
        val clampYB = RS.heightF * 0.9f
        RenderUtils.drawRect(0f, 0f, RS.widthF, RS.heightF, ColorRGB.BLACK.alpha(32))
        RenderUtils.drawRect(0f, clampYT, RS.widthF, clampYB, ColorRGB.DARK_GRAY.alpha(84))

        val clampXL = RS.widthF * 0.1f
        val clampXR = RS.widthF * 0.9f
        var startY = clampYT + 20f
        var startX = clampXL
        val size = 310f * scale
        clickArea.clear()
        trainings.forEachIndexed { index, it ->
            val range = Pair(
                startX..startX + size,
                startY..startY + size
            )
            clickArea.add(range)
            val hoovered = mouseX in range.first && mouseY in range.second
            RenderUtils.drawRect(
                range.first.start,
                range.second.start,
                range.first.endInclusive,
                range.second.endInclusive,
                ColorRGB.WHITE.alpha(if (hoovered) 128 else 64)
            )
            if (defaultTraining == index) {
                FontRendererMain.drawString(it.trainingName, startX + size * 0.07f, startY + size * 0.8f, ColorRGB.RED)
            } else {
                FontRendererMain.drawString(it.trainingName, startX + size * 0.07f, startY + size * 0.8f)
            }
            startX += size
            startX += 10f
            if (startX + size > clampXR) {
                startX = clampXL
                startY += size
                startY += 10f
            }
        }
    }

    private val scoreboardScreen = object : ScoreboardScreen() {
        override fun onKeyTyped(keyCode: Int, modifier: Int): Boolean {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                AimTrainingScene.currentTraining = null
                Render2DManager.closeAll()
                SceneManager.switchScene(DummyScene)
                Render2DManager.displayScreen(TrainingScreen)
                CrosshairRenderer.disable()
                return true
            }
            return false
        }
    }

    override fun onMouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean {
        clickArea.forEachIndexed { index, range ->
            if (mouseX.toFloat() in range.first && mouseY.toFloat() in range.second) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
                    AimTrainingScene.entities.clear()
                    AimTrainingScene.currentTraining = trainings[index].new(scoreboardScreen, AimTrainingScene).reset()
                    Render2DManager.closeAll()
                    SceneManager.switchScene(AimTrainingScene)
                } else if (button == GLFW.GLFW_MOUSE_BUTTON_2) {
                    defaultTraining = index
                    Configs.saveConfig("configs.json")
                }
                return true
            }
        }
        return false
    }

    override fun onKeyTyped(keyCode: Int, modifier: Int): Boolean {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            Render2DManager.popScreen()
            if (Render2DManager.currentScreen == null) Render2DManager.displayScreen(MainMenuScreen)
            return true
        }
        return false
    }

}