package net.spartanb312.everett.graphics

import net.spartanb312.everett.graphics.OpenGL.*
import net.spartanb312.everett.graphics.compat.CompatContext
import net.spartanb312.everett.graphics.compat.Delegate
import net.spartanb312.everett.graphics.compat.multitex.ARBMultiTexture
import net.spartanb312.everett.graphics.compat.multitex.GL13MultiTexture
import net.spartanb312.everett.graphics.compat.multitex.IMultiTexture
import net.spartanb312.everett.graphics.compat.shader.ARBShaders
import net.spartanb312.everett.graphics.compat.shader.GL20Shaders
import net.spartanb312.everett.graphics.compat.shader.IShaders
import net.spartanb312.everett.graphics.compat.vao.ARBVAO
import net.spartanb312.everett.graphics.compat.vao.GL30VAO
import net.spartanb312.everett.graphics.compat.vao.IVAO
import net.spartanb312.everett.graphics.compat.vbo.ARBVBO
import net.spartanb312.everett.graphics.compat.vbo.GL15VBO
import net.spartanb312.everett.graphics.compat.vbo.IVBO
import net.spartanb312.everett.graphics.compat.vertexattrib.ARBVertexAttrib
import net.spartanb312.everett.graphics.compat.vertexattrib.GL20VertexAttrib
import net.spartanb312.everett.graphics.compat.vertexattrib.IVertexAttrib
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.EXTFramebufferObject
import org.lwjgl.opengl.GL30
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Delegate glCalls for multiple platforms & graphics cards
 */
object GLHelper : CompatContext,
    IMultiTexture by if (RenderSystem.compat.arbMultiTexture) ARBMultiTexture else GL13MultiTexture,
    IShaders by if (RenderSystem.compat.arbShaders) ARBShaders else GL20Shaders,
    IVAO by if (!RenderSystem.compat.openGL30) ARBVAO else GL30VAO,
    IVBO by if (RenderSystem.compat.arbVbo) ARBVBO else GL15VBO,
    IVertexAttrib by if (!RenderSystem.compat.openGL20) ARBVertexAttrib else GL20VertexAttrib {

    // Disabled in core mode
    var alpha by GLState(false) { if (it) glEnable(GL_ALPHA_TEST) else glDisable(GL_ALPHA_TEST) }
    var blend by GLState(false) { if (it) glEnable(GL_BLEND) else glDisable(GL_BLEND) }
    var smooth by GLState(false) { if (it) glEnable(GL_SMOOTH) else glDisable(GL_FLAT) }
    var depth by GLState(false) { if (it) glEnable(GL_DEPTH_TEST) else glDisable(GL_DEPTH_TEST) }
    var texture2d by GLState(false) { if (it) glEnable(GL_TEXTURE_2D) else glDisable(GL_TEXTURE_2D) }
    var cull by GLState(false) { if (it) glEnable(GL_CULL_FACE) else glDisable(GL_CULL_FACE) }
    var lighting by GLState(false) { if (it) glEnable(GL_LIGHTING) else glDisable(GL_LIGHTING) }
    var pointSmooth by GLState(false) { if (it) glEnable(GL_POINT_SMOOTH) else glDisable(GL_POINT_SMOOTH) }
    var lineSmooth by GLState(false) { if (it) glEnable(GL_LINE_SMOOTH) else glDisable(GL_LINE_SMOOTH) }
    var polygonSmooth by GLState(false) { if (it) glEnable(GL_POLYGON_SMOOTH) else glDisable(GL_POLYGON_SMOOTH) }
    var vSync by GLState(true) { if (it) GLFW.glfwSwapInterval(1) else GLFW.glfwSwapInterval(0) }
    var shadeModel by GLState(GL_FLAT) { glShadeModel(it) }

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

    @Deprecated("Disabled in core mode")
    fun pushMatrixAll() {
        glMatrixMode(GL_PROJECTION)
        glPushMatrix()
        glMatrixMode(GL_MODELVIEW)
        glPushMatrix()
    }

    @Deprecated("Disabled in core mode")
    fun popMatrixAll() {
        glMatrixMode(GL_MODELVIEW)
        glPopMatrix()
        glMatrixMode(GL_PROJECTION)
        glPopMatrix()
    }

    @Delegate
    fun glGenerateMipmap(glEnum: Int) = with(RenderSystem.compat) {
        if (openGL30) GL30.glGenerateMipmap(glEnum)
        else if (extFramebufferObject) EXTFramebufferObject.glGenerateMipmapEXT(glEnum)
        else if (openGL14) glTexParameteri(glEnum, GL_GENERATE_MIPMAP, GL_TRUE)
        else throw Exception("OpenGL 1.4 and upper is required to enable mipmap texture")
    }

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