package net.spartanb312.boar.game.forge

import net.spartanb312.boar.game.render.crosshair.CrosshairRenderer
import net.spartanb312.boar.game.render.gui.Render2DManager
import net.spartanb312.boar.game.render.gui.impls.MainMenuScreen
import net.spartanb312.boar.game.render.gui.impls.OptionScreen
import net.spartanb312.boar.game.render.gui.impls.PauseScreen
import net.spartanb312.boar.game.render.scene.Scene
import net.spartanb312.boar.game.render.scene.SceneManager
import net.spartanb312.boar.game.render.scene.impls.DummyScene
import net.spartanb312.boar.language.Language.m
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

        override fun render3D() {

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