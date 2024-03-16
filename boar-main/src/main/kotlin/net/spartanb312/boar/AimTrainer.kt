package net.spartanb312.boar

import net.spartanb312.boar.game.Player
import net.spartanb312.boar.game.config.Configs
import net.spartanb312.boar.game.input.InputManager
import net.spartanb312.boar.game.option.impls.ControlOption
import net.spartanb312.boar.game.option.impls.VideoOption
import net.spartanb312.boar.game.render.FontCacheManager
import net.spartanb312.boar.game.render.TextureManager
import net.spartanb312.boar.game.render.crosshair.CrosshairRenderer
import net.spartanb312.boar.game.render.gui.Render2DManager
import net.spartanb312.boar.game.render.gui.impls.LoadingScreen
import net.spartanb312.boar.game.render.scene.SceneManager
import net.spartanb312.boar.graphics.GLHelper
import net.spartanb312.boar.graphics.GLHelper.glMatrixScope
import net.spartanb312.boar.graphics.GameGraphics
import net.spartanb312.boar.graphics.OpenGL.GL_COLOR_BUFFER_BIT
import net.spartanb312.boar.graphics.OpenGL.glClear
import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.graphics.matrix.applyOrtho
import net.spartanb312.boar.launch.Module
import net.spartanb312.boar.utils.math.toRadian
import net.spartanb312.boar.utils.math.vector.Vec3f
import net.spartanb312.boar.utils.misc.Profiler
import net.spartanb312.boar.utils.timing.Sync
import net.spartanb312.boar.utils.timing.Timer
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * Based on Boar Engine
 * Everett, You'll always be mine
 */
@Module(
    name = "Aim Trainer",
    version = AimTrainer.AIM_TRAINER_VERSION,
    description = "An aim trainer for Halo Infinite",
    author = "B_312"
)
object AimTrainer : GameGraphics {

    const val AIM_TRAINER_VERSION = "1.0.0.240316"

    private val tickTimer = Timer()

    override fun onInit() {
        RS.setTitle("Aim Trainer $AIM_TRAINER_VERSION")
        try {
            Configs.loadConfig("configs.json")
            Configs.saveConfig("configs.json")
        } catch (ignore: Exception) {
            Configs.saveConfig("configs.json")
            ignore.printStackTrace()
        }
        GLHelper.vSync = false
        TextureManager.resume()
        FontCacheManager.readCache()
        Runtime.getRuntime().addShutdownHook(Thread {
            FontCacheManager.saveCache()
        })
        //FontRendererMain.initAllChunks()
        Render2DManager.displayScreen(LoadingScreen)
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
            Player.project(
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

        // Tick
        tickTimer.passedAndReset(16) {
            SceneManager.onTick()
            val facing = Vec3f(cos(Player.yaw.toRadian()), 0, sin(Player.yaw.toRadian()))
            val rightV = Vec3f(0, 1, 0) cross facing

            val moveSpeed = 0.3f

            var xDiff = 0f
            var yDiff = 0f
            var zDiff = 0f

            if (GLFW.glfwGetKey(RS.window, GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
                xDiff += facing.x * moveSpeed
                zDiff += facing.z * moveSpeed
            }
            if (GLFW.glfwGetKey(RS.window, GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS) {
                xDiff -= facing.x * moveSpeed
                zDiff -= facing.z * moveSpeed
            }
            if (GLFW.glfwGetKey(RS.window, GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS) {
                xDiff += rightV.x * moveSpeed
                zDiff += rightV.z * moveSpeed
            }
            if (GLFW.glfwGetKey(RS.window, GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS) {
                xDiff -= rightV.x * moveSpeed
                zDiff -= rightV.z * moveSpeed
            }
            if (GLFW.glfwGetKey(RS.window, GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS) {
                yDiff += moveSpeed
            }
            if (GLFW.glfwGetKey(RS.window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS) {
                yDiff -= moveSpeed
            }
            Player.move(xDiff, yDiff, zDiff)
        }
        profiler("Tick")
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