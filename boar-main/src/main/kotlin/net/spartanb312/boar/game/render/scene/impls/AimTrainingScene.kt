package net.spartanb312.boar.game.render.scene.impls

import net.spartanb312.boar.game.render.TextureManager
import net.spartanb312.boar.game.render.crosshair.CrosshairRenderer
import net.spartanb312.boar.game.render.gui.Render2DManager
import net.spartanb312.boar.game.render.gui.impls.MainMenuScreen
import net.spartanb312.boar.game.render.gui.impls.OptionScreen
import net.spartanb312.boar.game.render.gui.impls.PauseScreen
import net.spartanb312.boar.game.render.gui.impls.TrainingScreen
import net.spartanb312.boar.game.render.scene.Scene
import net.spartanb312.boar.game.render.scene.SceneManager
import net.spartanb312.boar.game.training.Training
import net.spartanb312.boar.graphics.Skybox
import net.spartanb312.boar.utils.timing.Timer
import org.lwjgl.glfw.GLFW

object AimTrainingScene : Scene() {

    private val tickTimer = Timer()
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
    var currentTraining: Training? = null

    private val pauseScreen = PauseScreen(AimTrainingScene).apply {
        buttons.add(PauseScreen.Button("Resume") {
            Render2DManager.popScreen()
        })
        buttons.add(PauseScreen.Button("Restart") {
            currentTraining?.reset()
        })
        buttons.add(PauseScreen.Button("Options") {
            Render2DManager.closeAll()
            Render2DManager.displayScreen(OptionScreen)
        })
        buttons.add(PauseScreen.Button("Trainings") {
            SceneManager.switchScene(DummyScene)
            Render2DManager.closeAll()
            Render2DManager.displayScreen(TrainingScreen)
            CrosshairRenderer.disable()
        })
        buttons.add(PauseScreen.Button("Menu") {
            SceneManager.switchScene(DummyScene)
            Render2DManager.closeAll()
            Render2DManager.displayScreen(MainMenuScreen)
            CrosshairRenderer.disable()
        })
    }

    override fun render3D() {
        skybox.onRender3D()
        currentTraining?.render()
    }

    override fun render2D() {
        tickTimer.passedAndReset(10) { currentTraining?.onTick() }
        currentTraining?.render2D()
    }

    override fun onMouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean {
        currentTraining?.onClick()
        return true
    }

    override fun onTick() {
        // HaloInfiniteAA
    }

    override fun onKeyTyped(keyCode: Int, modifier: Int): Boolean {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            Render2DManager.displayScreen(pauseScreen)
            return true
        }
        return false
    }

}