package net.spartanb312.boar.game

import net.spartanb312.boar.game.render.TextureManager
import net.spartanb312.boar.game.render.crosshair.CrosshairRenderer
import net.spartanb312.boar.game.render.gui.Render2DManager
import net.spartanb312.boar.game.render.gui.impls.MainMenuScreen
import net.spartanb312.boar.game.render.gui.impls.OptionScreen
import net.spartanb312.boar.game.render.gui.impls.PauseScreen
import net.spartanb312.boar.game.render.gui.impls.ScoreboardScreen
import net.spartanb312.boar.game.render.scene.Scene
import net.spartanb312.boar.game.render.scene.SceneManager
import net.spartanb312.boar.game.render.scene.impls.DummyScene
import net.spartanb312.boar.game.render.training.impls.SixShot
import net.spartanb312.boar.graphics.Skybox
import net.spartanb312.boar.utils.timing.Timer
import org.lwjgl.glfw.GLFW

object Academy {

    fun start() {
        Render2DManager.closeAll()
        SceneManager.switchScene(AcademyScene())
        CrosshairRenderer.enable()
    }

    fun close() {
        SceneManager.switchScene(DummyScene)
        Render2DManager.displayScreen(MainMenuScreen)
        CrosshairRenderer.disable()
    }

    class AcademyScene : Scene() {

        private val tickTimer = Timer()
        private val pauseScreen = PauseScreen(this).apply {
            buttons.add(PauseScreen.Button("Resume") { Render2DManager.popScreen() })
            buttons.add(PauseScreen.Button("Options") {
                Render2DManager.closeAll()
                Render2DManager.displayScreen(OptionScreen)
            })
            buttons.add(PauseScreen.Button("Menu") { close() })
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

        private val skybox = Skybox(
            -200.0,
            -200.0,
            -200.0,
            200.0,
            200.0,
            200.0,
            TextureManager.down,
            TextureManager.up,
            TextureManager.left,
            TextureManager.front,
            TextureManager.right,
            TextureManager.back
        )
        private val training = SixShot(scoreboardScreen, this)

        override fun onRender() {
            skybox.onRender3D()
            training.render()
        }

        override fun render2D() {
            tickTimer.passedAndReset(10) { training.onTick() }
            training.render2D()
        }

        override fun onMouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean {
            training.onClick()
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