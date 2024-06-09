package net.spartanb312.everett.graphics

import net.spartanb312.everett.graphics.OpenGL.*
import net.spartanb312.everett.utils.misc.NULL
import org.lwjgl.glfw.GLFW
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object GLHelper {

    // Disabled in core mode
    var blend by GLState(false) { if (it) glEnable(GL_BLEND) else glDisable(GL_BLEND) }
    var depth by GLState(false) { if (it) glEnable(GL_DEPTH_TEST) else glDisable(GL_DEPTH_TEST) }
    var cull by GLState(false) { if (it) glEnable(GL_CULL_FACE) else glDisable(GL_CULL_FACE) }
    var lineSmooth by GLState(false) { if (it) glEnable(GL_LINE_SMOOTH) else glDisable(GL_LINE_SMOOTH) }
    var vSync by GLState(true) { if (it) GLFW.glfwSwapInterval(1) else GLFW.glfwSwapInterval(0) }
    var fullScreen by GLState(false) {
        if (it) {
            val xArray = IntArray(1)
            val yArray = IntArray(1)
            GLFW.glfwGetWindowPos(RS.window, xArray, yArray)
            windowedXPos = xArray[0]
            windowedYPos = yArray[0]
            GLFW.glfwGetWindowSize(RS.window, xArray, yArray)
            windowedWidth = xArray[0]
            windowedHeight = yArray[0]
            val monitor = GLFW.glfwGetPrimaryMonitor()
            val mode = GLFW.glfwGetVideoModes(monitor)!!.last()
            GLFW.glfwSetWindowMonitor(RS.window, monitor, 0, 0, mode.width(), mode.height(), mode.refreshRate())
        } else GLFW.glfwSetWindowMonitor(
            RS.window,
            NULL,
            windowedXPos,
            windowedYPos,
            windowedWidth,
            windowedHeight,
            GLFW.GLFW_DONT_CARE
        )
    }

    private var windowedXPos = 0
    private var windowedYPos = 0
    private var windowedWidth = 0
    private var windowedHeight = 0
    var bindProgram = 0; private set
    var mouseMode = GLFW.GLFW_CURSOR_NORMAL; private set

    fun useProgram(id: Int, force: Boolean = false) {
        if (force || id != bindProgram) {
            glUseProgram(id)
            bindProgram = id
        }
    }

    fun mouseMode(mode: Int) {
        if (mode != mouseMode) {
            mouseMode = mode
            GLFW.glfwSetInputMode(RenderSystem.window, GLFW.GLFW_CURSOR, mode)
            if (mode == GLFW.GLFW_CURSOR_NORMAL) GLFW.glfwSetCursorPos(
                RenderSystem.window,
                RenderSystem.centerXD,
                RenderSystem.centerYD
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
        glScissor(x, RS.height - y1, x1 - x, y1 - y)
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