package net.spartanb312.everett

import net.spartanb312.everett.audio.AudioSystem
import net.spartanb312.everett.game.Configs
import net.spartanb312.everett.game.Language
import net.spartanb312.everett.game.Player
import net.spartanb312.everett.game.audio.BGMPlayer
import net.spartanb312.everett.game.audio.GunfireAudio
import net.spartanb312.everett.game.audio.notebox.Harp
import net.spartanb312.everett.game.audio.notebox.Piano
import net.spartanb312.everett.game.audio.notebox.Piano2
import net.spartanb312.everett.game.audio.notebox.pianos
import net.spartanb312.everett.game.audio.noteplayer.MidiPlayer
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
import net.spartanb312.everett.graphics.RenderSystem
import net.spartanb312.everett.graphics.RenderSystem.scaling
import net.spartanb312.everett.graphics.RenderSystem.window
import net.spartanb312.everett.graphics.drawing.pmvbo.PersistentMappedVertexBuffer
import net.spartanb312.everett.graphics.drawing.pmvbo.PersistentMappedVertexBuffer.draw
import net.spartanb312.everett.graphics.font.UnicodeSparseFontRenderer
import net.spartanb312.everett.graphics.matrix.applyOrtho
import net.spartanb312.everett.graphics.matrix.scope
import net.spartanb312.everett.graphics.model.impls.ExternalModel
import net.spartanb312.everett.graphics.model.mesh.MeshDNSH
import net.spartanb312.everett.launch.LaunchScreen
import net.spartanb312.everett.launch.Module
import net.spartanb312.everett.physics.PhysicsSystem
import net.spartanb312.everett.utils.Logger
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.misc.Profiler
import net.spartanb312.everett.utils.thread.ConcurrentTaskManager
import net.spartanb312.everett.utils.timing.Sync
import net.spartanb312.everett.utils.timing.Timer
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.glfwGetCursorPos
import org.lwjgl.opengl.GL11

/**
 * Based on OpenGL 4.5 Core Profile
 * Minimum requirements:
 * CPU: Pentium D or better
 * GPU: 512MB V-RAM with OpenGL 4.5 support
 */
@Module(
    name = "Aim Trainer",
    version = AimTrainer.AIM_TRAINER_VERSION,
    description = "An aim trainer originally for Halo Infinite",
    author = "B_312"
)
object AimTrainer : GameGraphics {

    const val AIM_TRAINER_VERSION = "1.0.0.260206"

    val splash = LaunchScreen()
    var isReady = false
    private val tickTimer = Timer()

    val model = ExternalModel("assets/spartan/spartan.obj", TextureManager) { MeshDNSH(it) }
    val sparseFontRenderers = mutableListOf<UnicodeSparseFontRenderer>()

    val taskManager = ConcurrentTaskManager("AimTrainer TaskManager")
    val sync = Sync()
    var useFramebuffer = false; private set

    val midi = MidiPlayer.readSong("assets/sound/Never Forget from Halo 3 for Piano.mid")

    // insure camera update accuracy in low fps
    object CameraUpdateThread : Thread("CameraUpdateThread") {
        override fun run() {
            while (RenderSystem.isAlive) {
                if (RS.averageFPS <= 500 && ControlOption.hpUpdate && Render2DManager.updateCamera) {
                    val posX = DoubleArray(1)
                    val posY = DoubleArray(1)
                    glfwGetCursorPos(window, posX, posY)
                    val x = posX[0]
                    val y = posY[0]
                    RS.mouseXD = x * scaling.scale
                    RS.mouseYD = y * scaling.scale
                    RS.originMouseX = x
                    RS.originMouseY = y
                    Player.updateCamera(
                        updateCamera = Render2DManager.updateCamera,
                        sensitivity = ControlOption.sensitivity,
                        dpiModifier = ControlOption.dpiModifyRate,
                        hRate = ControlOption.hRate,
                        vRate = ControlOption.vRate
                    )
                }
                sleep(1)
            }
        }
    }

    override fun onInit() {
        RS.setTitle("Aim Trainer $AIM_TRAINER_VERSION")
        Logger.info("ARB_sparse_texture: ${RS.compat.arbSparseTexture}")
        splash.updateSplash(0.4f, "Reading configs...")
        try {
            Configs.loadConfig("configs.json")
            Configs.saveConfig("configs.json", false)
        } catch (ignore: Exception) {
            Configs.saveConfig("configs.json", false)
            //ignore.printStackTrace()
        }
        splash.updateSplash(0.5f, "Initializing audio system...")
        pianos
        Harp
        GLHelper.vSync = false
        splash.updateSplash(0.6f, "Initializing texture manager...")
        TextureManager.resume()
        FontCacheManager.readCache()
        FontCacheManager.initChunks()
        Runtime.getRuntime().addShutdownHook(Thread {
            FontCacheManager.saveCache()
        })
        splash.updateSplash(0.7f, "Initializing render manager...")
        Render2DManager.displayScreen(LoadingScreen)
        PhysicsSystem.launch(Player, 60, true)
        AudioSystem.start()
        splash.updateSplash(0.9f, "Loading game assets...")
        GunfireAudio
        model.loadModel()
        CameraUpdateThread.start()
        splash.stop()
    }

    override fun Profiler.onLoop() {
        useFramebuffer = VideoOption.framebuffer
        RS.antiAlias.setMode(VideoOption.antiAlias)
        TextureManager.renderThreadHook(5)
        UnicodeSparseFontRenderer.renderThreadHook(sparseFontRenderers, 5)
        Language.update()

        // Start rendering
        profiler("Render Hook")
        if (useFramebuffer) {
            RS.antiAlias.startRendering()
            glViewport(0, 0, RS.scaledWidth, RS.scaledHeight)
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
            Player.updateCamera(
                updateCamera = Render2DManager.updateCamera,
                sensitivity = ControlOption.sensitivity,
                dpiModifier = ControlOption.dpiModifyRate,
                hRate = ControlOption.hRate,
                vRate = ControlOption.vRate
            )
            Player.project(VideoOption.fov) {
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
            Background.bgHook()
            if (!useFramebuffer) renderUI()
        }
        profiler("Render 2D")

        // Tick (60TPS)
        tickTimer.tps(60) {
            TickEvent.Pre.post()
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

    fun drawTexture(
        texture: Int,
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        u: Float,
        v: Float,
        u1: Float,
        v1: Float,
        colorRGB: ColorRGB = ColorRGB.WHITE
    ) {
        glBindTexture(GL_TEXTURE_2D, texture)
        GL_TRIANGLE_STRIP.draw(PersistentMappedVertexBuffer.VertexMode.Universal) {
            universal(endX, startY, u1, v, colorRGB)
            universal(startX, startY, u, v, colorRGB)
            universal(endX, endY, u1, v1, colorRGB)
            universal(startX, endY, u, v1, colorRGB)
        }
    }

    private fun renderUI() {
        MedalRenderer.onRender()
        Render2DManager.onRender(RS.mouseXD, RS.mouseYD)
        NotificationRenderer.onRender()
        CrosshairRenderer.onRender(VideoOption.dfov)
        DebugInfoRenderer.onRender()
        StatRenderer.onRender()
    }

    override fun onFramebufferDrawing() {
        // Framebuffer
        if (useFramebuffer) {
            RS.matrixLayer.scope {
                //RS.setRenderScale(1f)
                GLHelper.blend = true
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
                glClearColor(0f, 0f, 0f, 1f)
                RS.antiAlias.endRendering()
                glViewport(0, 0, RS.displayWidth, RS.displayHeight)
                applyOrtho(0.0f, RS.widthF, RS.heightF, 0.0f, -1.0f, 1.0f)
                renderUI()
            }
        }
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
        if (!vSync && VideoOption.videoMode.value == VideoOption.VideoMode.Custom) sync.sync(VideoOption.fpsLimit)
    }

    override fun onResolutionUpdate(oldWidth: Int, oldHeight: Int, newWidth: Int, newHeight: Int, newDpiRate: Float) {
        Logger.info("Resolution updated to $newWidth x $newHeight, DPI: $newDpiRate")
        val oldScaledWidth = RS.scaling.scaledWidth
        val oldScaledHeight = RS.scaling.scaledHeight
        RS.scaling.update(newWidth, newHeight)
        val newScaledWidth = RS.scaling.scaledWidth
        val newScaledHeight = RS.scaling.scaledHeight
        ResolutionUpdateEvent(
            oldWidth, oldHeight,
            newWidth, newHeight,
            oldScaledWidth, oldScaledHeight,
            newScaledWidth, newScaledHeight
        ).post()
    }

}