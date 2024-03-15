package net.spartanb312.boar

import net.spartanb312.boar.game.config.Configs
import net.spartanb312.boar.game.input.InputManager
import net.spartanb312.boar.game.option.impls.ControlOption
import net.spartanb312.boar.game.option.impls.VideoOption
import net.spartanb312.boar.game.render.FontCacheManager
import net.spartanb312.boar.game.render.TextureManager
import net.spartanb312.boar.game.render.crosshair.CrosshairRenderer
import net.spartanb312.boar.game.render.gui.Render2DManager
import net.spartanb312.boar.game.render.scene.SceneManager
import net.spartanb312.boar.graphics.Camera
import net.spartanb312.boar.graphics.GLHelper
import net.spartanb312.boar.graphics.GLHelper.glMatrixScope
import net.spartanb312.boar.graphics.GameGraphics
import net.spartanb312.boar.graphics.OpenGL.GL_COLOR_BUFFER_BIT
import net.spartanb312.boar.graphics.OpenGL.glClear
import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.graphics.matrix.applyOrtho
import net.spartanb312.boar.utils.misc.Profiler
import net.spartanb312.boar.utils.timing.Sync
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.*

/**
 * Boar Engine
 * Refined Gloom Engine
 */
object Boar : GameGraphics {

    const val VERSION = "1.0"
    const val AIM_TRAINER_VERSION = "1.0.0.240312"

    override fun onInit() {
        RS.setTitle("Boar")
        try {
            Configs.loadConfig("configs.json")
            Configs.saveConfig("configs.json")
        } catch (ignore: Exception) {
            Configs.saveConfig("configs.json")
        }
        GLHelper.vSync = false
        TextureManager.resume()
        FontCacheManager.readCache()
        Runtime.getRuntime().addShutdownHook(Thread {
            FontCacheManager.saveCache()
        })
    }

    override fun Profiler.onLoop() {
        TextureManager.renderThreadHook(5)
        // Start rendering
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT or GL_STENCIL_BUFFER_BIT)
        glViewport(0, 0, RS.width, RS.height)
        profiler("Render Hook")

        // Render3D
        glMatrixScope {
            val lockCamera = !Render2DManager.updateCamera
            GLHelper.depth = true
            GLHelper.cull = true
            Camera.Default.project(
                fov = VideoOption.fov,
                updateCamera = !lockCamera,
                sensitivity = ControlOption.sensitivity
            ) {
                SceneManager.onRender()
            }
            GLHelper.cull = false
            GLHelper.depth = false
        }
        profiler("Render 3D")

        // Render2D
        glMatrixScope {
            applyOrtho(0.0f, RS.widthF, RS.heightF, 0.0f, -1.0f, 1.0f)
            Render2DManager.onRender(RS.mouseXD, RS.mouseYD)
            //InfoRenderer.render()
            CrosshairRenderer.onRender(VideoOption.dfov)
        }
        profiler("Render 2D")
    }

    override fun onKeyCallback(key: Int, action: Int, modifier: Int) {
        when (action) {
            GLFW.GLFW_PRESS -> InputManager.onKeyTyped(key, modifier)
            GLFW.GLFW_REPEAT -> InputManager.onKeyRepeating(key, modifier)
            GLFW.GLFW_RELEASE -> InputManager.onKeyReleased(key, modifier)
        }
    }

    override fun onMouseClicked(button: Int, action: Int, modifier: Int) {
        when (action) {
            GLFW.GLFW_PRESS -> InputManager.onMouseClicked(RS.mouseX, RS.mouseY, button)
            GLFW.GLFW_RELEASE -> InputManager.onMouseReleased(RS.mouseX, RS.mouseY, button)
        }
    }

    override fun onScrollCallback(direction: Int) {
        InputManager.updateScroll(direction)
    }

    override fun onSync() {
        val vSync = VideoOption.videoMode.value == VideoOption.VideoMode.VSync
        GLHelper.vSync = vSync
        if (!vSync && VideoOption.videoMode.value != VideoOption.VideoMode.Unlimited) Sync.sync(VideoOption.fpsLimit)
    }

}