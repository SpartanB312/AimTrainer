package net.spartanb312.boar.graphics

import net.spartanb312.boar.graphics.OpenGL.*
import net.spartanb312.boar.graphics.compat.CompatContext
import net.spartanb312.boar.graphics.compat.Delegate
import net.spartanb312.boar.graphics.compat.multitex.ARBMultiTexture
import net.spartanb312.boar.graphics.compat.multitex.GL13MultiTexture
import net.spartanb312.boar.graphics.compat.multitex.IMultiTexture
import net.spartanb312.boar.graphics.compat.shader.ARBShaders
import net.spartanb312.boar.graphics.compat.shader.GL20Shaders
import net.spartanb312.boar.graphics.compat.shader.IShaders
import net.spartanb312.boar.graphics.compat.vao.ARBVAO
import net.spartanb312.boar.graphics.compat.vao.GL30VAO
import net.spartanb312.boar.graphics.compat.vao.IVAO
import net.spartanb312.boar.graphics.compat.vbo.ARBVBO
import net.spartanb312.boar.graphics.compat.vbo.GL15VBO
import net.spartanb312.boar.graphics.compat.vbo.IVBO
import net.spartanb312.boar.graphics.compat.vertexattrib.ARBVertexAttrib
import net.spartanb312.boar.graphics.compat.vertexattrib.GL20VertexAttrib
import net.spartanb312.boar.graphics.compat.vertexattrib.IVertexAttrib
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
    var vSync by GLState(false) { if (it) GLFW.glfwSwapInterval(1) else GLFW.glfwSwapInterval(0) }
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
                RenderSystem.widthD / 2.0,
                RenderSystem.heightD / 2.0
            )
        }
    }

    fun unbindProgram() = useProgram(0)

    fun pushMatrix(mode: Int) {
        when (mode) {
            GL_MODELVIEW -> {
                glMatrixMode(GL_MODELVIEW)
                glPushMatrix()
            }

            GL_PROJECTION -> {
                glMatrixMode(GL_PROJECTION)
                glPushMatrix()
            }

            else -> throw IllegalArgumentException("Unknown matrix mode,it should be one of GL_MODELVIEW and GL_PROJECTION.")
        }
    }

    fun popMatrix(mode: Int) {
        when (mode) {
            GL_MODELVIEW -> {
                glMatrixMode(GL_MODELVIEW)
                glPopMatrix()
            }

            GL_PROJECTION -> {
                glMatrixMode(GL_PROJECTION)
                glPopMatrix()
            }

            else -> throw IllegalArgumentException("Unknown matrix mode,it should be one of GL_MODELVIEW and GL_PROJECTION.")
        }
    }

    fun pushMatrixAll() {
        glMatrixMode(GL_PROJECTION)
        glPushMatrix()
        glMatrixMode(GL_MODELVIEW)
        glPushMatrix()
    }

    fun popMatrixAll() {
        glMatrixMode(GL_MODELVIEW)
        glPopMatrix()
        glMatrixMode(GL_PROJECTION)
        glPopMatrix()
    }

    inline fun glMatrixScope(block: () -> Unit) {
        pushMatrixAll()
        block()
        popMatrixAll()
    }

    @Delegate
    fun glGenerateMipmap(glEnum: Int) = with(RenderSystem.compat) {
        if (openGL30) GL30.glGenerateMipmap(glEnum)
        else if (extFramebufferObject) EXTFramebufferObject.glGenerateMipmapEXT(glEnum)
        else if (openGL14) glTexParameteri(glEnum, GL_GENERATE_MIPMAP, GL_TRUE)
        else throw Exception("OpenGL 1.4 and upper is required to enable mipmap texture")
    }

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