package net.spartanb312.everett.game.training

import net.spartanb312.everett.game.Language
import net.spartanb312.everett.game.Language.lang
import net.spartanb312.everett.game.option.impls.ControlOption.defaultTraining
import net.spartanb312.everett.game.render.CrosshairRenderer
import net.spartanb312.everett.game.render.gui.Render2DManager
import net.spartanb312.everett.game.render.gui.impls.*
import net.spartanb312.everett.game.render.scene.Scene
import net.spartanb312.everett.game.render.scene.SceneManager
import net.spartanb312.everett.game.render.scene.impls.DummyScene
import net.spartanb312.everett.utils.timing.Timer
import org.lwjgl.glfw.GLFW

object QuickPlay {

    fun start() {
        Render2DManager.closeAll()
        SceneManager.switchScene(QuickPlayScene())
    }

    fun close() {
        SceneManager.switchScene(DummyScene)
        Render2DManager.displayScreen(MainMenuScreen)
        CrosshairRenderer.disable()
    }

    class QuickPlayScene : Scene() {

        private val tickTimer = Timer()
        private val pauseScreen = PauseScreen(this).apply {
            buttons.add(PauseScreen.Button("Resume".lang("继续", "繼續")) { Render2DManager.popScreen() })
            buttons.add(PauseScreen.Button("Options".lang("设置", "設定")) {
                Render2DManager.closeAll()
                Render2DManager.displayScreen(OptionScreen)
            })
            buttons.add(PauseScreen.Button("Menu".lang("菜单", "菜單")) { close() })
        }
        private val scoreboardScreen = object : ScoreboardScreen() {
            override fun onKeyTyped(keyCode: Int, modifier: Int): Boolean {
                if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                    Render2DManager.closeAll()
                    close()
                    return true
                }
                return false
            }
        }

        val training = TrainingScreen.trainings[defaultTraining].new(scoreboardScreen, this).reset()
        val errorAngle = training.errorAngle

        override fun onInit() {
            Language.update(true)
        }

        override fun render3D() {
            training.render()
        }

        override fun render2D() {
            tickTimer.passedAndReset(10) { training.onTick() }
            training.render2D()
        }

        override fun onMouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean {
            //training?.onClick() see @CrosshairRenderer
            if (training is ReactionTest) training.onClick()
            return true
        }

        override fun onKeyTyped(keyCode: Int, modifier: Int): Boolean {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                Render2DManager.displayScreen(pauseScreen)
                return true
            }
            return false
        }

    }

}