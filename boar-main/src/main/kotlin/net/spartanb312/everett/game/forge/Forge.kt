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
import net.spartanb312.everett.game.render.scene.impls.SkyboxScene
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.matrix.scalef
import net.spartanb312.everett.graphics.matrix.scope
import net.spartanb312.everett.graphics.matrix.translatef
import net.spartanb312.everett.graphics.model.mesh.lightPosition
import net.spartanb312.everett.graphics.model.mesh.lightPosition2
import net.spartanb312.everett.graphics.scene.Scene3D
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.math.vector.Vec3f
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

    override fun onRender() {
        SkyboxScene.render3D()
        RS.matrixLayer.scope {
            scalef(10f, 10f, 10f)
            translatef(0f, -0.55f, 0f)
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

        // Physics tick
        override fun onTick() {
        }

        private fun lagrangeInterpolation3D(
            x0: Vec3f, t0: Double,
            x1: Vec3f, t1: Double,
            x2: Vec3f, t2: Double,
            t3: Double
        ): Vec3f {
            val l0 = ((t3 - t1) * (t3 - t2)) / ((t0 - t1) * (t0 - t2))
            val l1 = ((t3 - t0) * (t3 - t2)) / ((t1 - t0) * (t1 - t2))
            val l2 = ((t3 - t0) * (t3 - t1)) / ((t2 - t0) * (t2 - t1))
            val interpolatedX = x0.x * l0 + x1.x * l1 + x2.x * l2
            val interpolatedY = x0.y * l0 + x1.y * l1 + x2.y * l2
            val interpolatedZ = x0.z * l0 + x1.z * l1 + x2.z * l2
            return Vec3f(interpolatedX, interpolatedY, interpolatedZ)
        }

        private fun linearInterpolation3D(
            x0: Vec3f, t0: Double,
            x1: Vec3f, t1: Double,
            t2: Double
        ): Vec3f {
            val alpha = (t2 - t0) / (t1 - t0)
            val interpolatedX = x0.x + alpha * (x1.x - x0.x)
            val interpolatedY = x0.y + alpha * (x1.y - x0.y)
            val interpolatedZ = x0.z + alpha * (x1.z - x0.z)
            return Vec3f(interpolatedX, interpolatedY, interpolatedZ)
        }


        override fun render3D() {
            BallRenderer.render(
                lightPosition.x,
                lightPosition.y,
                lightPosition.z,
                0.5f,
                ColorRGB.LIGHT_PURPLE.mix(ColorRGB.WHITE, 0.5f)
            )
            BallRenderer.render(
                lightPosition2.x,
                lightPosition2.y,
                lightPosition2.z,
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