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
import net.spartanb312.everett.game.render.*
import net.spartanb312.everett.game.render.gui.Render2DManager
import net.spartanb312.everett.game.render.gui.impls.LoadingScreen
import net.spartanb312.everett.game.render.scene.SceneManager
import net.spartanb312.everett.graphics.GLHelper
import net.spartanb312.everett.graphics.GameGraphics
import net.spartanb312.everett.graphics.OpenGL.*
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.framebuffer.ResizableFramebuffer
import net.spartanb312.everett.graphics.matrix.applyOrtho
import net.spartanb312.everett.graphics.matrix.scope
import net.spartanb312.everett.graphics.model.impls.ExternalModel
import net.spartanb312.everett.graphics.model.mesh.MeshDNSH
import net.spartanb312.everett.graphics.texture.drawTexture
import net.spartanb312.everett.launch.Module
import net.spartanb312.everett.physics.PhysicsSystem
import net.spartanb312.everett.utils.Logger
import net.spartanb312.everett.utils.misc.Profiler
import net.spartanb312.everett.utils.thread.ConcurrentTaskManager
import net.spartanb312.everett.utils.timing.Sync
import net.spartanb312.everett.utils.timing.Timer
import org.lwjgl.glfw.GLFW

/**
 * Requires OpenGL 4.5 Core Profile
 * OpenGL 2.1 is no longer supported
 */
@Module(
    name = "Aim Trainer",
    version = AimTrainer.AIM_TRAINER_VERSION,
    description = "An aim trainer originally for Halo Infinite",
    author = "B_312"
)
object AimTrainer : GameGraphics {

    const val AIM_TRAINER_VERSION = "1.0.0.240718"

    var isReady = false
    private val tickTimer = Timer()

    val model = ExternalModel("assets/everett/Everett.obj", TextureManager) { MeshDNSH(it) }

    lateinit var framebuffer: ResizableFramebuffer
    lateinit var renderLayer: ResizableFramebuffer.ResizableColorLayer
    val taskManager = ConcurrentTaskManager("AimTrainer TaskManager")
    var useFramebuffer = false; private set

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
        renderLayer = framebuffer.generateColorLayer()
    }

    override fun Profiler.onLoop() {
        useFramebuffer = VideoOption.framebuffer
        TextureManager.renderThreadHook(5)
        Language.update()

        // Start rendering
        profiler("Render Hook")
        if (useFramebuffer) {
            framebuffer.bindFramebuffer()
            renderLayer.bindLayer()
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT or GL_STENCIL_BUFFER_BIT)
            glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA)
        } else {
            RS.setRenderScale(1f)
            GLHelper.bindFramebuffer(0)
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT or GL_STENCIL_BUFFER_BIT)
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
            glViewport(0, 0, RS.width, RS.height)
        }

        // Render3D
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
        RS.matrixLayer.scope {
            GLHelper.blend = true
            applyOrtho(0.0f, RS.widthF, RS.heightF, 0.0f, -1.0f, 1.0f)
            MedalRenderer.onRender()
            Render2DManager.onRender(RS.mouseXD, RS.mouseYD)
            NotificationRenderer.onRender()
            CrosshairRenderer.onRender(VideoOption.dfov)
            DebugInfoRenderer.onRender()
            StatRenderer.onRender()
        }
        if (useFramebuffer) framebuffer.unbindFramebuffer()
        profiler("Render 2D")

        // Framebuffer
        if (useFramebuffer) {
            //glClear(GL_COLOR_BUFFER_BIT)
            RS.matrixLayer.scope {
                GLHelper.blend = true
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
                glClearColor(0f, 0f, 0f, 1f)
                glViewport(0, 0, RS.displayWidth, RS.displayHeight)
                applyOrtho(0.0f, RS.displayWidthF, RS.displayHeightF, 0.0f, -1.0f, 1.0f)
                renderLayer.drawTexture(
                    0f,
                    0f,
                    RS.displayWidthF,
                    RS.displayHeightF,
                    0,
                    framebuffer.height,
                    framebuffer.width,
                    0
                )
            }
            profiler("Framebuffer")
        }

        // Tick (60TPS)
        tickTimer.tps(60) {
            TickEvent.Pre.post()
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
                if (key == GLFW.GLFW_KEY_F3) DebugInfoRenderer.enabled = !DebugInfoRenderer.enabled
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
        if (VideoOption.renderRate.updateTimer.passed(200)
            && GLFW.glfwGetMouseButton(
                RS.window,
                GLFW.GLFW_MOUSE_BUTTON_1
            ) != GLFW.GLFW_PRESS
        ) {
            RS.setRenderScale(VideoOption.renderScale / 100f)
        }
        val vSync = VideoOption.videoMode.value == VideoOption.VideoMode.VSync
        GLHelper.vSync = vSync
        if (!vSync && VideoOption.videoMode.value != VideoOption.VideoMode.Unlimited) Sync.sync(VideoOption.fpsLimit)
    }

    override fun onResolutionUpdate(oldWith: Int, oldHeight: Int, newWidth: Int, newHeight: Int) {
        Logger.info("Resolution updated to $newWidth x $newHeight")
        framebuffer.resize(newWidth, newHeight)
        ResolutionUpdateEvent.post(ResolutionUpdateEvent(oldWith, oldHeight, newWidth, newHeight))
    }

}