package net.spartanb312.everett.game.forge

import net.spartanb312.everett.AimTrainer
import net.spartanb312.everett.game.Language
import net.spartanb312.everett.game.Language.lang
import net.spartanb312.everett.game.render.BallRenderer
import net.spartanb312.everett.game.render.CrosshairRenderer
import net.spartanb312.everett.game.render.gui.Render2DManager
import net.spartanb312.everett.game.render.gui.impls.MainMenuScreen
import net.spartanb312.everett.game.render.gui.impls.OptionScreen
import net.spartanb312.everett.game.render.gui.impls.PauseScreen
import net.spartanb312.everett.game.render.scene.Scene
import net.spartanb312.everett.game.render.scene.SceneManager
import net.spartanb312.everett.game.render.scene.impls.DummyScene
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.matrix.scalef
import net.spartanb312.everett.graphics.matrix.scope
import net.spartanb312.everett.graphics.matrix.translatef
import net.spartanb312.everett.graphics.model.mesh.lightPosition
import net.spartanb312.everett.graphics.scene.Scene3D
import net.spartanb312.everett.utils.color.ColorRGB
import org.lwjgl.glfw.GLFW

object Forge : Scene3D() {

    fun start() {
        SceneManager.switchScene(ForgeScene)
        Render2DManager.closeAll()
        CrosshairRenderer.enable()
    }

    fun close() {
        SceneManager.switchScene(DummyScene)
        Render2DManager.displayScreen(MainMenuScreen)
        CrosshairRenderer.disable()
    }

    //private val attribute = buildAttribute(24) {
    //    float(0, 3, GLDataType.GL_FLOAT, false) // Pos
    //    float(1, 4, GLDataType.GL_UNSIGNED_BYTE, true) // Color
    //    float(2, 2, GLDataType.GL_FLOAT, false) // UV
    //    float(3,)
    //}
    //private val vao = PersistentMappedVBO.createVao(attribute)
    //private val shader = Shader("assets/shader/lighting/test.vsh", "assets/shader/lighting/test.fsh")

    override fun onRender() {
        RS.matrixLayer.scope {
            scalef(10f, 10f, 10f)
            translatef(0f,-0.55f,0f)
            AimTrainer.model.drawModel(this)
        }
    }

    object ForgeScene : Scene() {

        private val pauseScreen = PauseScreen(this).apply {
            buttons.add(PauseScreen.Button("Resume".lang("继续", "繼續")) { Render2DManager.popScreen() })
            buttons.add(PauseScreen.Button("Options".lang("设置", "設定")) {
                Render2DManager.closeAll()
                Render2DManager.displayScreen(OptionScreen)
            })
            buttons.add(PauseScreen.Button("Menu".lang("菜单", "菜單")) { close() })
        }

        override fun onInit() {
            Language.update(true)
        }

        override fun render2D() {

        }

        override fun render3D() {
            BallRenderer.render(
                lightPosition.x,
                lightPosition.y,
                lightPosition.z,
                0.5f,
                ColorRGB.GOLD.mix(ColorRGB.WHITE, 0.5f)
            )
            onRender()
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