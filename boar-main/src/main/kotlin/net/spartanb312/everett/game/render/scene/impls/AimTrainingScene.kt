package net.spartanb312.everett.game.render.scene.impls

import net.spartanb312.everett.game.Language.lang
import net.spartanb312.everett.game.render.CrosshairRenderer
import net.spartanb312.everett.game.render.TextureManager
import net.spartanb312.everett.game.render.gui.GuiScreen
import net.spartanb312.everett.game.render.gui.Render2DManager
import net.spartanb312.everett.game.render.gui.impls.MainMenuScreen
import net.spartanb312.everett.game.render.gui.impls.OptionScreen
import net.spartanb312.everett.game.render.gui.impls.PauseScreen
import net.spartanb312.everett.game.render.gui.impls.TrainingScreen
import net.spartanb312.everett.game.render.scene.Scene
import net.spartanb312.everett.game.render.scene.SceneManager
import net.spartanb312.everett.game.training.ReactionTest
import net.spartanb312.everett.game.training.Training
import net.spartanb312.everett.graphics.Skybox
import net.spartanb312.everett.utils.timing.Timer
import org.lwjgl.glfw.GLFW

object AimTrainingScene : Scene() {

    private val tickTimer = Timer()
    val skybox = Skybox(
        -200f,
        -200f,
        -200f,
        200f,
        200f,
        200f,
        TextureManager.down,
        TextureManager.up,
        TextureManager.left,
        TextureManager.front,
        TextureManager.right,
        TextureManager.back
    )
    var currentTraining: Training? = null
    var jumBack: GuiScreen = TrainingScreen

    private val pauseScreen = PauseScreen(AimTrainingScene).apply {
        buttons.add(PauseScreen.Button("Resume".lang("继续", "繼續")) {
            Render2DManager.popScreen()
        })
        buttons.add(PauseScreen.Button("Restart".lang("重置", "重置")) {
            currentTraining?.reset()
        })
        buttons.add(PauseScreen.Button("Options".lang("设置", "設定")) {
            Render2DManager.closeAll()
            Render2DManager.displayScreen(OptionScreen)
        })
        buttons.add(PauseScreen.Button("Trainings".lang("重选", "重選")) {
            SceneManager.switchScene(DummyScene)
            Render2DManager.closeAll()
            Render2DManager.displayScreen(jumBack)
            CrosshairRenderer.disable()
        })
        buttons.add(PauseScreen.Button("Menu".lang("菜单", "菜單")) {
            SceneManager.switchScene(DummyScene)
            Render2DManager.closeAll()
            Render2DManager.displayScreen(MainMenuScreen)
            CrosshairRenderer.disable()
        })
    }

    override fun render3D() {
        currentTraining?.render()
    }

    override fun render2D() {
        tickTimer.passedAndReset(10) { currentTraining?.onTick() }
        currentTraining?.render2D()
    }

    override fun onMouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean {
        //currentTraining?.onClick() see @CrosshairRenderer
        if (currentTraining is ReactionTest) currentTraining?.onClick()
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