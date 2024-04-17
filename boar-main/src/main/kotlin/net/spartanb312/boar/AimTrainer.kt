package net.spartanb312.boar

import net.spartanb312.boar.audio.AudioSystem
import net.spartanb312.boar.game.Player
import net.spartanb312.boar.game.audio.BGMPlayer
import net.spartanb312.boar.game.config.Configs
import net.spartanb312.boar.game.input.InputManager
import net.spartanb312.boar.game.option.impls.AccessibilityOption
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
import net.spartanb312.boar.graphics.drawing.buffer.PersistentMappedVertexBuffer
import net.spartanb312.boar.graphics.matrix.applyOrtho
import net.spartanb312.boar.language.Language
import net.spartanb312.boar.launch.Module
import net.spartanb312.boar.physics.PhysicsSystem
import net.spartanb312.boar.utils.Logger
import net.spartanb312.boar.utils.misc.Profiler
import net.spartanb312.boar.utils.misc.createFile
import net.spartanb312.boar.utils.timing.Sync
import net.spartanb312.boar.utils.timing.Timer
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.*

/**
 * OpenGL 2.1 is Required
 * Migrating to OpenGL 4.5
 */
@Module(
    name = "Aim Trainer",
    version = AimTrainer.AIM_TRAINER_VERSION,
    description = "An aim trainer for Halo Infinite",
    author = "B_312"
)
object AimTrainer : GameGraphics {

    const val AIM_TRAINER_VERSION = "1.0.0.240418"

    var isReady = false
    private val tickTimer = Timer()

    override fun onInit() {
        RS.setTitle("Aim Trainer $AIM_TRAINER_VERSION")
        try {
            Configs.loadConfig("configs.json")
            Configs.saveConfig("configs.json", false)
        } catch (ignore: Exception) {
            Configs.saveConfig("configs.json", false)
            //ignore.printStackTrace()
        }
        GLHelper.vSync = false
        TextureManager.resume()
        FontCacheManager.readCache()
        FontCacheManager.initChunks()
        Runtime.getRuntime().addShutdownHook(Thread {
            FontCacheManager.saveCache()
            val api = AccessibilityOption.getLaunchOGLVersion()
            createFile("launch_option.cfg").apply {
                writeText("API=$api")
            }
        })
        Render2DManager.displayScreen(LoadingScreen)
        PhysicsSystem.launch(Player, 60, true)
        AudioSystem.start()
    }

    override fun Profiler.onLoop() {
        TextureManager.renderThreadHook(5)
        Language.update()

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
                sensitivity = ControlOption.sensitivity,
                hRate = ControlOption.hRate,
                vRate = ControlOption.vRate
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
            Render2DManager.onTick()
            BGMPlayer.onTick()
        }
        profiler("Tick")

        // Physics
        ControlOption.checkPhysicsThread()
        profiler("Physics")
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
        PersistentMappedVertexBuffer.onSync()
    }

    override fun onResolutionUpdate(oldWith: Int, oldHeight: Int, newWidth: Int, newHeight: Int) {
        Logger.info("Resolution updated to $newWidth x $newHeight")
    }

}