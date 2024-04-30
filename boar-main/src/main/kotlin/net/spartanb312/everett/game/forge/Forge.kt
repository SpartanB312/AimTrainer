package net.spartanb312.everett.game.forge

import net.spartanb312.everett.AimTrainer
import net.spartanb312.everett.game.Language.m
import net.spartanb312.everett.game.render.TextureManager
import net.spartanb312.everett.game.render.crosshair.CrosshairRenderer
import net.spartanb312.everett.game.render.gui.Render2DManager
import net.spartanb312.everett.game.render.gui.impls.MainMenuScreen
import net.spartanb312.everett.game.render.gui.impls.OptionScreen
import net.spartanb312.everett.game.render.gui.impls.PauseScreen
import net.spartanb312.everett.game.render.scene.Scene
import net.spartanb312.everett.game.render.scene.SceneManager
import net.spartanb312.everett.game.render.scene.impls.DummyScene
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.Skybox
import net.spartanb312.everett.graphics.matrix.newScope
import org.lwjgl.glfw.GLFW

object Forge {

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

    object ForgeScene : Scene() {

        private val pauseScreen = PauseScreen(this).apply {
            buttons.add(PauseScreen.Button("Resume".m("继续", "繼續")) { Render2DManager.popScreen() })
            buttons.add(PauseScreen.Button("Options".m("设置", "設定")) {
                Render2DManager.closeAll()
                Render2DManager.displayScreen(OptionScreen)
            })
            buttons.add(PauseScreen.Button("Menu".m("菜单", "菜單")) { close() })
        }

        private val skybox = Skybox(
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

        override fun render2D() {
            //Radar.render2D()
            //EnergyShield.render2D()
        }

        override fun render3D() {
            skybox.onRender3D()
            AimTrainer.model.drawModel(RS.matrixLayer.newScope)
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