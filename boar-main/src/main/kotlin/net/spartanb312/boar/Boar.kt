package net.spartanb312.boar

import net.spartanb312.boar.graphics.*
import net.spartanb312.boar.graphics.GLHelper.glMatrixScope
import net.spartanb312.boar.graphics.OpenGL.GL_COLOR_BUFFER_BIT
import net.spartanb312.boar.graphics.OpenGL.glClear
import net.spartanb312.boar.graphics.matrix.applyOrtho
import net.spartanb312.boar.render.*
import net.spartanb312.boar.render.gui.Render2DManager
import net.spartanb312.boar.utils.timing.Timer
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.*

/**
 * Boar Engine
 * Refined Gloom Engine
 */
object Boar : GameGraphics {

    override fun onInit() {
        RenderSystem.setTitle("Boar")
        GLFW.glfwSwapInterval(0)
        TextureManager.resume()
        FontCacheManager.readCache()
        Runtime.getRuntime().addShutdownHook(Thread {
            FontCacheManager.saveCache()
        })
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

    private var angle = 0f
    private val timer = Timer()

    override fun onLoop() {
        TextureManager.renderThreadHook(5)
        timer.passedAndRun(16, reset = true) {
            angle += 0.1f
        }
        // Start rendering
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT or GL_STENCIL_BUFFER_BIT)
        glViewport(0, 0, RenderSystem.displayWidth, RenderSystem.displayHeight)

        // Render3D
        glMatrixScope {
            GLHelper.depth = true
            GLHelper.cull = true
            Camera.Default.project(fov = 119f) {
                skybox.onRender3D()
            }
            GLHelper.cull = false
            GLHelper.depth = false
        }

        // Render2D
        glMatrixScope {
            applyOrtho(0.0f, RenderSystem.displayWidthF, RenderSystem.displayHeightF, 0.0f, -1.0f, 1.0f)
            Render2DManager.onRender(RenderSystem.mouseX, RenderSystem.mouseY)
            InfoRenderer.render()
            CrosshairRenderer.onRender(120f)
            SF.drawString(200f, 200f)
        }
    }

    override fun onKeyCallback(key: Int, action: Int, modifier: Int) {

    }

    override fun onMouseClicked(key: Int, action: Int, modifier: Int) {

    }

    override fun onScrollCallback(direction: Int) {

    }

}