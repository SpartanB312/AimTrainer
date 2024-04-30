package net.spartanb312.everett.graphics

import net.spartanb312.everett.graphics.OpenGL.*
import net.spartanb312.everett.graphics.compat.GLCompatibility
import net.spartanb312.everett.graphics.event.EngineLoopEvent
import net.spartanb312.everett.graphics.matrix.MatrixLayerStack
import net.spartanb312.everett.launch.Module
import net.spartanb312.everett.utils.Logger
import net.spartanb312.everett.utils.collection.CircularArray
import net.spartanb312.everett.utils.misc.*
import net.spartanb312.everett.utils.timing.Timer
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL.createCapabilities
import org.lwjgl.opengl.GL11
import java.util.concurrent.LinkedBlockingQueue

typealias RS = RenderSystem

@Module(
    name = "Everett's Sight",
    version = RenderSystem.ENGINE_VERSION,
    description = "Core game engine based on OpenGL 4.5 Core Profile",
    author = "B_312"
)
object RenderSystem : Thread() {

    const val ENGINE_VERSION = "1.1.5"

    init {
        name = "RenderThread"
    }

    private val counter = Counter(1000)
    private val countArray = CircularArray<Float>(8)
    private val countTimer = Timer()
    var rawFPS = 0; private set
    var averageFPS = 0F; private set

    var window = 0L; private set
    var monitor = 0L; private set
    var width = 0; private set
    var height = 0; private set
    inline val widthF get() = width.toFloat()
    inline val heightF get() = height.toFloat()
    inline val widthD get() = width.toDouble()
    inline val heightD get() = height.toDouble()
    inline val aspect get() = widthF / heightF
    inline val aspectD get() = widthD / heightD
    inline val centerX get() = width / 2
    inline val centerY get() = height / 2
    inline val centerXF get() = widthF / 2f
    inline val centerYF get() = heightF / 2f
    inline val centerXD get() = widthD / 2.0
    inline val centerYD get() = heightD / 2.0
    inline val maxThreads get() = Runtime.getRuntime().availableProcessors()

    const val initialMouseValue = Int.MIN_VALUE.toDouble()
    var mouseXD = initialMouseValue; private set
    var mouseYD = initialMouseValue; private set
    inline val mouseXF get() = mouseXD.toFloat()
    inline val mouseYF get() = mouseYD.toFloat()
    inline val mouseX get() = mouseXD.toInt()
    inline val mouseY get() = mouseYD.toInt()

    val widthScale get() = widthF / 1920f
    val heightScale get() = heightF / 1080f
    var frames = 0L; private set
    var activeFrames = 0L; private set
    var rto = true
    const val rtoTime = 5
    val matrixLayer = MatrixLayerStack()

    private val renderThreadJob = LinkedBlockingQueue<Runnable>()
    fun addRenderThreadJob(runnable: Runnable) = renderThreadJob.add(runnable)

    var actualRenderThread: Thread = this; private set
    fun isRenderThread(): Boolean = currentThread() == actualRenderThread
    fun checkRenderThread() {
        if (!isRenderThread()) throw RuntimeException("Should be called in RenderThread!")
    }

    private object DummyGraphics : GameGraphics

    private var gameGraphics: GameGraphics = DummyGraphics
    lateinit var compat: GLCompatibility; private set

    var debugInfo = false; private set
    private var centered = false
    private var initWidth = 1600
    private var initHeight = 900
    private var title = "Boar3D"
    private var graphics: Class<out GameGraphics>? = null
    private var graphicsAPI = API.GL_210_COMPAT
    val compatMode get() = graphicsAPI == API.GL_210_COMPAT
    val using460 get() = graphicsAPI == API.GL_460_CORE

    private val memoryCheckUpdateTimer = Timer()
    private val profilerResultUpdateTimer = Timer()
    private val profiler = Profiler()
    var lastProfilingResults: MutableMap<String, Long> = mutableMapOf(); private set
    var totalMemory = 0L; private set
    var freeMemory = 0L; private set
    val usedMemory get() = totalMemory - freeMemory

    fun <T : GameGraphics> launch(
        graphics: Class<T>,
        initWidth: Int = 1600,
        initHeight: Int = 900,
        title: String = "Boar3D",
        centered: Boolean = false,
        dedicateThread: Boolean = true,
        debugInfo: Boolean = false,
        graphicsAPI: API = API.GL_450_CORE
    ) {
        this.graphicsAPI = graphicsAPI
        this.graphics = graphics
        this.centered = centered
        this.initWidth = initWidth
        this.initHeight = initHeight
        this.debugInfo = debugInfo
        this.title = title
        if (dedicateThread) {
            currentThread().interrupt()
            this.start()
        } else this.run()
    }

    override fun run() {
        actualRenderThread = currentThread()
        GLFWErrorCallback.createPrint(System.err).set()
        check(glfwInit()) { "Unable to initialize GLFW" }
        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)

        var checkWindow = -1L

        if (graphicsAPI != API.GL_210_COMPAT) {
            // check GL compat
            checkWindow = glfwCreateWindow(initWidth, initHeight, title, NULL, NULL)
            glfwMakeContextCurrent(checkWindow)
            compat = GLCompatibility(createCapabilities())

            if (!compat.openGL45) {
                Logger.error("OpenGL 4.5 is not supported by this device! using OpenGL 2.1 compat mode!")
                graphicsAPI = API.GL_210_COMPAT
            } else {
                if (!compat.openGL46 && graphicsAPI == API.GL_460_CORE) {
                    Logger.error("OpenGL 4.6 is not supported by this device! using OpenGL 4.5 Core Profile!")
                    graphicsAPI = API.GL_450_CORE
                }
                glfwWindowHint(GLFW_CONTEXT_CREATION_API, GLFW_NATIVE_CONTEXT_API)
                glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
                glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4)
                glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, if (graphicsAPI == API.GL_460_CORE) 6 else 5)
            }
        }

        Logger.info("Using API: ${graphicsAPI.fullName}")

        window = glfwCreateWindow(initWidth, initHeight, title, NULL, NULL)
        if (window == NULL) throw RuntimeException("Failed to create the GLFW window")

        monitor = glfwGetPrimaryMonitor()
        if (monitor == NULL) throw RuntimeException("Can't find current monitor")

        if (centered) {
            val widthBuffer = mallocInt(1)
            val heightBuffer = mallocInt(1)
            glfwGetWindowSize(window, widthBuffer, heightBuffer)

            glfwGetVideoMode(monitor)?.let { videoMode ->
                glfwSetWindowPos(
                    window,
                    (videoMode.width() - widthBuffer[0]) / 2,
                    (videoMode.height() - heightBuffer[0]) / 2
                )
            }
        }

        glfwMakeContextCurrent(window)
        if (graphicsAPI == API.GL_210_COMPAT) compat = GLCompatibility(createCapabilities())
        if (checkWindow != -1L) glfwDestroyWindow(checkWindow)

        gameGraphics = graphics!!.instance!!
        if (gameGraphics == DummyGraphics) throw Exception("This GFX is not supported")

        // Keyboard callback
        glfwSetKeyCallback(window) { _: Long, key: Int, _: Int, action: Int, modifier: Int ->
            gameGraphics.onKeyCallback(key, action, modifier)
        }

        // Mouse callback
        glfwSetCursorPosCallback(window) { _: Long, x: Double, y: Double ->
            mouseXD = x
            mouseYD = y
        }

        glfwSetScrollCallback(window) { _: Long, _: Double, y: Double ->
            gameGraphics.onScrollCallback(y.toInt())
        }

        glfwSetMouseButtonCallback(window) { _: Long, button: Int, action: Int, mods: Int ->
            gameGraphics.onMouseClicked(button, action, mods)
        }

        // Preparing depth test
        glClearDepth(1.0)
        glDepthFunc(GL11.GL_LEQUAL)
        glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST)
        glFrontFace(GL11.GL_CCW)

        glClearColor(0f, 0f, 0f, 1f)
        updateResolution(false)

        gameGraphics.onInit()

        glfwShowWindow(window)

        while (!glfwWindowShouldClose(window)) {
            frames++
            val rtoLimit = (averageFPS * rtoTime).toLong()
            if (activeFrames < rtoLimit) {
                activeFrames++
                activeFrames = activeFrames.coerceAtMost(rtoLimit)
            }
            EngineLoopEvent.Sync.Pre.post()
            gameGraphics.onSync()
            EngineLoopEvent.Sync.Post.post()
            profiler.start()

            // Pre-render
            counter.invoke {
                rawFPS = it
                averageFPS = countArray.toList().sum() / 8f
            }
            countTimer.passedAndReset(125) { countArray.add(rawFPS) }
            updateResolution()
            updateMemory()
            EngineLoopEvent.Task.Pre.post()
            matrixLayer.resetID()
            while (true) {
                val task = renderThreadJob.poll()
                if (task != null) task.run()
                else break
            }
            EngineLoopEvent.Task.Post.post()
            EngineLoopEvent.PollEvents.Pre.post()
            glfwPollEvents()
            EngineLoopEvent.PollEvents.Post.post()
            profiler.profiler("Pre Render")

            // On Loop
            with(gameGraphics) {
                EngineLoopEvent.Loop.Pre.post()
                profiler.onLoop()
                EngineLoopEvent.Loop.Post.post()
            }

            EngineLoopEvent.SwapBuffer.Pre.post()
            glfwSwapBuffers(window)
            EngineLoopEvent.SwapBuffer.Post.post()
            profiler.profiler("Swap Buffer")
            if (profilerResultUpdateTimer.passed(1000)) {
                profilerResultUpdateTimer.reset()
                lastProfilingResults = profiler.endWithResult()
            }
        }

        Callbacks.glfwFreeCallbacks(window)
        glfwDestroyWindow(window)
        glfwTerminate()
        glfwSetErrorCallback(null)!!.free()
    }

    fun setTitle(title: String, debug: Boolean = false) {
        if (debugInfo && debug) Logger.debug("Changed title to $title")
        glfwSetWindowTitle(window, title)
    }

    private val widthArray = IntArray(1)
    private val heightArray = IntArray(1)

    private fun updateResolution(updateBlock: Boolean = true) {
        glfwGetFramebufferSize(window, widthArray, heightArray)
        val newWidth = widthArray[0]
        val newHeight = heightArray[0]
        if (updateBlock && (newWidth != width || newHeight != height)) gameGraphics.onResolutionUpdate(
            width,
            height,
            newWidth,
            newHeight
        )
        width = newWidth
        height = newHeight
    }

    private fun updateMemory() {
        memoryCheckUpdateTimer.passedAndReset(1000) {
            totalMemory = Runtime.getRuntime().totalMemory() / 1048576
            freeMemory = Runtime.getRuntime().freeMemory() / 1048576
        }
    }

    enum class API(override val displayName: String, val saveName: String, val fullName: String) : DisplayEnum {
        GL_210_COMPAT("OpenGL 2.1", "2.1", "OpenGL 2.1 Compatibility Profile"),
        GL_450_CORE("OpenGL 4.5", "4.5", "OpenGL 4.5 Core Profile"),
        GL_460_CORE("OpenGL 4.6", "4.6", "OpenGL 4.6 Core Profile")
    }

}