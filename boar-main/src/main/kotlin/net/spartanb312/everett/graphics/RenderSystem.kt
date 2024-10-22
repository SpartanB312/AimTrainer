package net.spartanb312.everett.graphics

import net.spartanb312.everett.graphics.OpenGL.*
import net.spartanb312.everett.graphics.drawing.pmvbo.PersistentMappedVBO
import net.spartanb312.everett.graphics.drawing.pmvbo.PersistentMappedVertexBuffer
import net.spartanb312.everett.graphics.event.EngineLoopEvent
import net.spartanb312.everett.graphics.matrix.MatrixLayerStack
import net.spartanb312.everett.launch.Module
import net.spartanb312.everett.launch.Platform
import net.spartanb312.everett.utils.Logger
import net.spartanb312.everett.utils.misc.*
import net.spartanb312.everett.utils.timing.Sync
import net.spartanb312.everett.utils.timing.Timer
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL.createCapabilities
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.NVXGPUMemoryInfo
import org.lwjgl.opengl.WGLAMDGPUAssociation
import java.util.concurrent.LinkedBlockingQueue
import javax.swing.JOptionPane
import kotlin.math.E
import kotlin.math.max
import kotlin.math.sqrt
import kotlin.system.exitProcess

typealias RS = RenderSystem

@Module(
    name = "Everett's Sight",
    version = RenderSystem.ENGINE_VERSION,
    description = "Core game engine based on OpenGL 4.6 Core Profile",
    author = "B_312"
)
object RenderSystem : Thread() {

    const val ENGINE_VERSION = "1.2.3"

    init {
        name = "RenderThread"
    }

    private val fpsCounter = AverageCounter(1000, 8)
    val averageFPS get() = fpsCounter.averageCPS
    var lastJobTime = 0L; private set

    var window = 0L; private set
    var monitor = 0L; private set
    var width = 0; private set
    var height = 0; private set
    inline val widthF get() = width.toFloat()
    inline val heightF get() = height.toFloat()
    inline val diagonalF get() = sqrt(widthF * widthF + heightF * heightF)
    inline val widthD get() = width.toDouble()
    inline val heightD get() = height.toDouble()
    inline val diagonalD get() = sqrt(widthD * widthD + heightD * heightD)
    inline val aspect get() = widthF / heightF
    inline val aspectD get() = widthD / heightD
    inline val centerX get() = width / 2
    inline val centerY get() = height / 2
    inline val centerXF get() = widthF / 2f
    inline val centerYF get() = heightF / 2f
    inline val centerXD get() = widthD / 2.0
    inline val centerYD get() = heightD / 2.0
    inline val maxThreads get() = Runtime.getRuntime().availableProcessors()

    var displayWidth = 0; private set
    var displayHeight = 0; private set
    inline val displayWidthF get() = displayWidth.toFloat()
    inline val displayHeightF get() = displayHeight.toFloat()
    inline val displayWidthD get() = displayWidth.toDouble()
    inline val displayHeightD get() = displayHeight.toDouble()
    var refreshRate = 0; private set

    const val initialMouseValue = Int.MIN_VALUE.toDouble()
    var mouseXD = initialMouseValue; private set
    var mouseYD = initialMouseValue; private set
    inline val mouseXF get() = mouseXD.toFloat()
    inline val mouseYF get() = mouseYD.toFloat()
    inline val mouseX get() = mouseXD.toInt()
    inline val mouseY get() = mouseYD.toInt()
    var originMouseXD = initialMouseValue; private set
    var originMouseYD = initialMouseValue; private set
    inline val originMouseXF get() = originMouseXD.toFloat()
    inline val originMouseYF get() = originMouseYD.toFloat()
    inline val originMouseX get() = originMouseXD.toInt()
    inline val originMouseY get() = originMouseYD.toInt()

    val widthScale get() = widthF / 1920f
    val heightScale get() = heightF / 1080f
    var generalScale = max(widthScale, heightScale)
    var frames = 0L; private set
    var activeFrames = 0L; private set
    var rto = true
    const val rtoTime = 5
    val matrixLayer = MatrixLayerStack()

    private val flexTimer = Timer()
    var flexSync = false
        set(value) {
            if (value) flexTimer.reset()
            field = value
        }

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

    var gpuTime = 0L; private set
    var frameTime = 0L; private set
    private val memoryCheckUpdateTimer = Timer()
    private val profilerResultUpdateTimer = Timer()
    private val profiler = Profiler()
    var lastProfilingResults: MutableMap<String, Long> = mutableMapOf(); private set
    var totalMemory = 0L; private set
    var freeMemory = 0L; private set
    var totalVRam = 0; private set
    val usedMemory get() = totalMemory - freeMemory
    private var renderScale = 1f

    fun <T : GameGraphics> launch(
        graphics: Class<T>,
        initWidth: Int = 1600,
        initHeight: Int = 900,
        title: String = "Boar3D",
        centered: Boolean = false,
        dedicateThread: Boolean = true,
        debugInfo: Boolean = false,
    ) {
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

    private val limitTimer = Timer()
    private val cps = Counter(1000)
    private val mps = Counter(1000)

    override fun run() {
        actualRenderThread = currentThread()
        GLFWErrorCallback.createPrint(System.err).set()
        check(glfwInit()) { "Unable to initialize GLFW" }
        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)

        // check GL compat
        val checkWindow = glfwCreateWindow(initWidth, initHeight, title, NULL, NULL)
        glfwMakeContextCurrent(checkWindow)

        compat = GLCompatibility(createCapabilities())

        if (!compat.openGL45) {
            val result = JOptionPane.showConfirmDialog(
                null,
                "Your graphics card does not support OpenGL 4.5.\n" +
                        "Continuing may face compatibility issues.\n" +
                        "Do you still want to try launching?",
                "Unsupported Graphics Card Detected",
                JOptionPane.YES_NO_OPTION
            )
            if (result == JOptionPane.NO_OPTION) exitProcess(0)
        }

        glfwWindowHint(GLFW_CONTEXT_CREATION_API, GLFW_NATIVE_CONTEXT_API)
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, if (compat.openGL46) 6 else 5)

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
        glfwDestroyWindow(checkWindow)

        gameGraphics = graphics!!.instance!!
        if (gameGraphics == DummyGraphics) throw Exception("This GFX is not supported")

        // Keyboard callback
        glfwSetKeyCallback(window) { _: Long, key: Int, _: Int, action: Int, modifier: Int ->
            gameGraphics.onKeyCallback(key, action, modifier)
        }

        // Mouse callback
        glfwSetCursorPosCallback(window) { _: Long, x: Double, y: Double ->
            originMouseXD = x
            originMouseYD = y
            mouseXD = x * renderScale
            mouseYD = y * renderScale
        }

        glfwSetScrollCallback(window) { _: Long, _: Double, y: Double ->
            gameGraphics.onScrollCallback(y.toInt())
        }

        glfwSetMouseButtonCallback(window) { _: Long, button: Int, action: Int, mods: Int ->
            gameGraphics.onMouseClicked(button, action, mods)
        }

        totalVRam =
            if (compat.nvidiaGraphics) GL11.glGetInteger(NVXGPUMemoryInfo.GL_GPU_MEMORY_INFO_TOTAL_AVAILABLE_MEMORY_NVX) / 1024
            else if (compat.amdGraphics) {
                if (Platform.getPlatform().os == Platform.OS.Windows) {
                    val array = IntArray(8)
                    val numOfGPU = WGLAMDGPUAssociation.wglGetGPUIDsAMD(array)
                    if (numOfGPU > 0) {
                        val mem = IntArray(1)
                        WGLAMDGPUAssociation.wglGetGPUInfoAMD(
                            array[0],
                            WGLAMDGPUAssociation.WGL_GPU_RAM_AMD,
                            GL_UNSIGNED_INT,
                            mem
                        )
                        mem[0]
                    } else 1
                } else 1 // TODO: AMD on Linux
            } else 1 // TODO: Intel on Windows & Linux

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
            val frameStartTime = System.nanoTime()
            frames++
            generalScale = max(widthScale, heightScale)
            val rtoLimit = (averageFPS * rtoTime).toLong()
            if (activeFrames < rtoLimit) {
                activeFrames++
                activeFrames = activeFrames.coerceAtMost(rtoLimit)
            }
            EngineLoopEvent.Sync.Pre.post()
            PersistentMappedVertexBuffer.onSync()
            PersistentMappedVBO.onSync()
            gameGraphics.onSync()
            EngineLoopEvent.Sync.Post.post()
            profiler.start()

            // Pre-render
            fpsCounter.invoke()
            updateResolution()
            updateMemory()
            EngineLoopEvent.Task.Pre.post()
            matrixLayer.resetID()
            while (true) {
                val task = renderThreadJob.poll()
                if (task != null) task.run()
                else break
            }
            lastJobTime = System.currentTimeMillis()
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
            val gpuStartTime = System.nanoTime()
            if (flexSync) flexSync() else {
                gameGraphics.onFramebufferDrawing()
                glfwSwapBuffers(window)
            }
            gpuTime = System.nanoTime() - gpuStartTime
            EngineLoopEvent.SwapBuffer.Post.post()
            profiler.profiler("Swap Buffer")
            if (profilerResultUpdateTimer.passed(1000)) {
                profilerResultUpdateTimer.reset()
                lastProfilingResults = profiler.endWithResult()
            }
            frameTime = System.nanoTime() - frameStartTime
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
        val displayWidth = widthArray[0]
        val displayHeight = heightArray[0]

        val newWidth = (displayWidth * renderScale).toInt()
        val newHeight = (displayHeight * renderScale).toInt()

        if (updateBlock && (width != newWidth || height != newHeight)) gameGraphics.onResolutionUpdate(
            width,
            height,
            newWidth,
            newHeight
        )

        val monitor = glfwGetPrimaryMonitor()
        val mode = glfwGetVideoModes(monitor)!!.last()
        refreshRate = mode.refreshRate()

        this.displayWidth = displayWidth
        this.displayHeight = displayHeight
        width = newWidth
        height = newHeight
    }

    private fun updateMemory() {
        memoryCheckUpdateTimer.passedAndReset(1000) {
            totalMemory = Runtime.getRuntime().totalMemory() / 1048576
            freeMemory = Runtime.getRuntime().freeMemory() / 1048576
        }
    }

    fun setRenderScale(scale: Float) {
        if (renderScale != scale) {
            renderScale = scale
            updateResolution()
            mouseXD = originMouseXD * renderScale
            mouseYD = originMouseYD * renderScale
        }
    }

    private fun flexSync() {
        var flag = false
        limitTimer.tps((refreshRate * E).toInt()) {
            limitTimer.reset()
            flag = true
        }
        if (flag) {
            gameGraphics.onFramebufferDrawing()
            glfwSwapBuffers(window)
        } else Sync.sync(
            if (!flexTimer.passed(1000)) Int.MAX_VALUE
            else refreshRate + (averageFPS * 1.2f).toInt()
        )
    }

}