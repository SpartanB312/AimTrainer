package net.spartanb312.everett.graphics

import net.spartanb312.everett.game.option.impls.VideoOption
import net.spartanb312.everett.graphics.OpenGL.*
import net.spartanb312.everett.utils.math.ceilToInt
import net.spartanb312.everett.utils.math.floorToInt
import net.spartanb312.everett.utils.misc.NULL
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL30
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object GLHelper {

    // Disabled in core mode
    var blend by GLState(false) { if (it) glEnable(GL_BLEND) else glDisable(GL_BLEND) }
    var depth by GLState(false) { if (it) glEnable(GL_DEPTH_TEST) else glDisable(GL_DEPTH_TEST) }
    var cull by GLState(false) { if (it) glEnable(GL_CULL_FACE) else glDisable(GL_CULL_FACE) }
    var lineSmooth by GLState(false) { if (it) glEnable(GL_LINE_SMOOTH) else glDisable(GL_LINE_SMOOTH) }
    var vSync by GLState(true) { if (it) glfwSwapInterval(1) else glfwSwapInterval(0) }

    fun setDisplayMode(prev: VideoOption.DisplayMode, dMode: VideoOption.DisplayMode) {
        if (prev == VideoOption.DisplayMode.Windowed) {
            val xArray = IntArray(1)
            val yArray = IntArray(1)
            glfwGetWindowPos(RS.window, xArray, yArray)
            windowedXPos = xArray[0]
            windowedYPos = yArray[0]
            glfwGetWindowSize(RS.window, xArray, yArray)
            windowedWidth = xArray[0]
            windowedHeight = yArray[0]
        } else if (prev == VideoOption.DisplayMode.Borderless) {
            glfwSetWindowAttrib(RS.window, GLFW_DECORATED, GLFW_TRUE)
        }
        when (dMode) {
            VideoOption.DisplayMode.Windowed -> {
                glfwSetWindowMonitor(
                    RS.window,
                    NULL,
                    windowedXPos,
                    windowedYPos,
                    windowedWidth,
                    windowedHeight,
                    GLFW_DONT_CARE
                )
            }

            VideoOption.DisplayMode.Borderless -> {
                val monitor = glfwGetPrimaryMonitor()
                val mode = glfwGetVideoMode(monitor)!!
                glfwSetWindowMonitor(RS.window, 0L, 0, 0, mode.width(), mode.height(), GLFW_DONT_CARE)
                glfwSetWindowAttrib(RS.window, GLFW_DECORATED, GLFW_FALSE)
                glfwSetWindowPos(RS.window, 0, 0)
                glfwSetWindowSize(RS.window, mode.width(), mode.height())
            }

            VideoOption.DisplayMode.FullScreen -> {
                val monitor = glfwGetPrimaryMonitor()
                val mode = glfwGetVideoMode(monitor)!!
                glfwSetWindowMonitor(RS.window, monitor, 0, 0, mode.width(), mode.height(), mode.refreshRate())
            }
        }
    }

    private var windowedXPos = 0
    private var windowedYPos = 0
    private var windowedWidth = 0
    private var windowedHeight = 0
    var bindProgram = -1; private set
    var bindFBO = -1; private set
    var bindVAO = -1; private set
    var mouseMode = GLFW_CURSOR_NORMAL; private set

    fun bindVertexArray(vao: Int, force: Boolean = false) {
        if (force || vao != bindVAO) {
            GL30.glBindVertexArray(vao)
            bindVAO = vao
        }
    }

    fun bindFramebuffer(fbo: Int, force: Boolean = false) {
        if (force || fbo != bindFBO) {
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo)
            bindFBO = fbo
        }
    }

    fun useProgram(id: Int, force: Boolean = false) {
        if (force || id != bindProgram) {
            glUseProgram(id)
            bindProgram = id
        }
    }

    fun mouseMode(mode: Int) {
        if (mode != mouseMode) {
            mouseMode = mode
            glfwSetInputMode(RenderSystem.window, GLFW_CURSOR, mode)
            if (mode == GLFW_CURSOR_NORMAL) glfwSetCursorPos(
                RenderSystem.window,
                RenderSystem.displayWidthD / 2.0,
                RenderSystem.displayHeightD / 2.0
            )
        }
    }

    fun unbindProgram() = useProgram(0)

    inline fun scissor(
        x: Int,
        y: Int,
        x1: Int,
        y1: Int,
        block: () -> Unit,
    ) {
        val scaledX = (x / RS.scaling.scale).floorToInt()
        val scaledY = (y / RS.scaling.scale).floorToInt()
        val scaledX1 = (x1 / RS.scaling.scale).ceilToInt()
        val scaledY1 = (y1 / RS.scaling.scale).ceilToInt()
        glScissor(scaledX, RS.displayHeight - scaledY1, scaledX1 - scaledX, scaledY1 - scaledY)
        glEnable(GL_SCISSOR_TEST)
        block()
        glDisable(GL_SCISSOR_TEST)
    }

    inline fun scissor(
        x: Float,
        y: Float,
        x1: Float,
        y1: Float,
        block: () -> Unit,
    ) = scissor(x.toInt(), y.toInt(), x1.toInt(), y1.toInt(), block)

    inline fun scissor(
        x: Double,
        y: Double,
        x1: Double,
        y1: Double,
        block: () -> Unit,
    ) = scissor(x.toInt(), y.toInt(), x1.toInt(), y1.toInt(), block)

}

class GLState<T>(valueIn: T, private val action: (T) -> Unit) : ReadWriteProperty<Any?, T> {

    private var value = valueIn

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): T = value

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if (this.value != value) {
            this.value = value
            action.invoke(value)
        }
    }

}