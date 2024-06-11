package net.spartanb312.everett

import net.spartanb312.everett.audio.AudioSystem
import net.spartanb312.everett.game.Configs
import net.spartanb312.everett.game.Language
import net.spartanb312.everett.game.Player
import net.spartanb312.everett.game.audio.BGMPlayer
import net.spartanb312.everett.game.audio.GunfireAudio
import net.spartanb312.everett.game.event.ResolutionUpdateEvent
import net.spartanb312.everett.game.event.TickEvent
import net.spartanb312.everett.game.input.InputManager
import net.spartanb312.everett.game.option.impls.ControlOption
import net.spartanb312.everett.game.option.impls.VideoOption
import net.spartanb312.everett.game.render.BlurRenderer
import net.spartanb312.everett.game.render.DebugInfo
import net.spartanb312.everett.game.render.FontCacheManager
import net.spartanb312.everett.game.render.TextureManager
import net.spartanb312.everett.game.render.crosshair.CrosshairRenderer
import net.spartanb312.everett.game.render.gui.MedalRenderer
import net.spartanb312.everett.game.render.gui.Notification
import net.spartanb312.everett.game.render.gui.Render2DManager
import net.spartanb312.everett.game.render.gui.impls.LoadingScreen
import net.spartanb312.everett.game.render.scene.SceneManager
import net.spartanb312.everett.graphics.GLHelper
import net.spartanb312.everett.graphics.GameGraphics
import net.spartanb312.everett.graphics.OpenGL.GL_COLOR_BUFFER_BIT
import net.spartanb312.everett.graphics.OpenGL.glClear
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.drawing.pmvbo.PersistentMappedVBO
import net.spartanb312.everett.graphics.drawing.pmvbo.PersistentMappedVertexBuffer
import net.spartanb312.everett.graphics.framebuffer.ResizableFramebuffer
import net.spartanb312.everett.graphics.matrix.applyOrtho
import net.spartanb312.everett.graphics.matrix.scope
import net.spartanb312.everett.graphics.model.Model
import net.spartanb312.everett.graphics.model.mesh.MeshDNSH
import net.spartanb312.everett.graphics.texture.drawTexture
import net.spartanb312.everett.launch.Module
import net.spartanb312.everett.physics.PhysicsSystem
import net.spartanb312.everett.utils.Logger
import net.spartanb312.everett.utils.misc.Profiler
import net.spartanb312.everett.utils.timing.Sync
import net.spartanb312.everett.utils.timing.Timer
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.*

/**
 * An engine based on OpenGL 4.5 Core Profile
 * OpenGL 2.1 is no longer supported
 */
@Module(
    name = "Aim Trainer",
    version = AimTrainer.AIM_TRAINER_VERSION,
    description = "An aim trainer for Halo Infinite",
    author = "B_312"
)
object AimTrainer : GameGraphics {

    const val AIM_TRAINER_VERSION = "1.0.0.240610"

    var isReady = false
    private val tickTimer = Timer()

    val model = Model("assets/everett/Everett.obj", TextureManager) { MeshDNSH(it) }

    lateinit var framebuffer: ResizableFramebuffer

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
        })
        Render2DManager.displayScreen(LoadingScreen)
        PhysicsSystem.launch(Player, 60, true)
        AudioSystem.start()
        GunfireAudio
        model.loadModel()
        framebuffer = ResizableFramebuffer(RS.width, RS.height, true)
    }

    override fun Profiler.onLoop() {
        TextureManager.renderThreadHook(5)
        Language.update()

        // Start rendering
        profiler("Render Hook")

        // Render3D
        framebuffer.bindFramebuffer()
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT or GL_STENCIL_BUFFER_BIT)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        RS.matrixLayer.scope {
            GLHelper.blend = true
            GLHelper.depth = true
            GLHelper.cull = true
            Player.project(
                fov = VideoOption.fov,
                updateCamera = Render2DManager.updateCamera,
                sensitivity = ControlOption.sensitivity,
                dpiModifier = ControlOption.dpiModifyRate,
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
        glClearColor(0f, 0f, 0f, 1f)
        RS.matrixLayer.scope {
            GLHelper.blend = true
            applyOrtho(0.0f, RS.widthF, RS.heightF, 0.0f, -1.0f, 1.0f)
            MedalRenderer.onRender()
            Render2DManager.onRender(RS.mouseXD, RS.mouseYD)
            Notification.onRender()
            CrosshairRenderer.onRender(VideoOption.dfov)
            DebugInfo.render2D()

            BlurRenderer.render(400f, 400f, 900f, 900f, 10)
        }
        framebuffer.unbindFramebuffer()
        profiler("Render 2D")

        // Framebuffer
        glClear(GL_COLOR_BUFFER_BIT)
        RS.matrixLayer.scope {
            GLHelper.blend = true
            //glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
            glClearColor(1f, 1f, 1f, 1f)
            applyOrtho(0.0f, RS.widthF, RS.heightF, 0.0f, -1.0f, 1.0f)
            framebuffer.texture.drawTexture(
                0f,
                0f,
                RS.widthF,
                RS.heightF,
                0,
                framebuffer.height,
                framebuffer.width,
                0
            )
        }
        profiler("Framebuffer")

        // Tick (60TPS)
        tickTimer.tps(60) {
            TickEvent.Pre.post()
            Player.onTick()
            SceneManager.onTick()
            Render2DManager.onTick()
            BGMPlayer.onTick()
            GunfireAudio.onTick()
            TickEvent.Post.post()
        }
        profiler("Tick")

        // Physics
        ControlOption.checkPhysicsThread()
        profiler("Physics")
    }

    override fun onKeyCallback(key: Int, action: Int, modifier: Int) {
        when (action) {
            GLFW.GLFW_PRESS -> {
                if (key == GLFW.GLFW_KEY_F3) DebugInfo.enabled = !DebugInfo.enabled
                InputManager.onKeyTyped(key, modifier)
            }

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
        PersistentMappedVBO.onSync()
    }

    override fun onResolutionUpdate(oldWith: Int, oldHeight: Int, newWidth: Int, newHeight: Int) {
        Logger.info("Resolution updated to $newWidth x $newHeight")
        BlurRenderer.updateResolution(newWidth, newHeight)
        framebuffer.resize(newWidth, newHeight)
        ResolutionUpdateEvent.post(ResolutionUpdateEvent(oldWith, oldHeight, newWidth, newHeight))
    }

}