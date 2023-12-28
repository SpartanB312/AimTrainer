package net.spartanb312.boar

import net.spartanb312.boar.game.input.InputManager
import net.spartanb312.boar.game.render.FontCacheManager
import net.spartanb312.boar.game.render.InfoRenderer
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
import net.spartanb312.boar.graphics.RenderSystem
import net.spartanb312.boar.graphics.matrix.applyOrtho
import net.spartanb312.boar.utils.timing.Timer
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.*

/**
 * Boar Engine
 * Refined Gloom Engine
 */
object Boar : GameGraphics {

    const val VERSION = "1.0"

    override fun onInit() {
        RenderSystem.setTitle("Boar")
        GLHelper.vSync = false
        TextureManager.resume()
        FontCacheManager.readCache()
        Runtime.getRuntime().addShutdownHook(Thread {
            FontCacheManager.saveCache()
        })
    }

    private val timer = Timer()

    override fun onLoop() {
        TextureManager.renderThreadHook(5)
        // Start rendering
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT or GL_STENCIL_BUFFER_BIT)
        glViewport(0, 0, RenderSystem.width, RenderSystem.height)

        // Render3D
        glMatrixScope {
            GLHelper.depth = true
            GLHelper.cull = true
            Camera.Default.project(fov = 119f, updateCamera = Render2DManager.updateCamera) {
                SceneManager.onRender()
            }
            GLHelper.cull = false
            GLHelper.depth = false
        }

        // Render2D
        glMatrixScope {
            applyOrtho(0.0f, RenderSystem.widthF, RenderSystem.heightF, 0.0f, -1.0f, 1.0f)
            Render2DManager.onRender(RenderSystem.mouseXD, RenderSystem.mouseYD)
            InfoRenderer.render()
            CrosshairRenderer.onRender(119f)
        }
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
            GLFW.GLFW_PRESS -> InputManager.onMouseClicked(RenderSystem.mouseX, RenderSystem.mouseY, button)
            GLFW.GLFW_RELEASE -> InputManager.onMouseReleased(RenderSystem.mouseX, RenderSystem.mouseY, button)
        }
    }

    override fun onScrollCallback(direction: Int) {
        InputManager.updateScroll(direction)
    }

}