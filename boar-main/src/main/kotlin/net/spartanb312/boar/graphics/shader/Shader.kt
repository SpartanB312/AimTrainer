package net.spartanb312.boar.graphics.shader

import net.spartanb312.boar.graphics.GLHelper
import net.spartanb312.boar.graphics.GLObject
import org.lwjgl.opengl.GL20
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

open class Shader(
    private val vsh: String,
    private val fsh: String,
) : GLObject {
    final override val id: Int

    init {
        val id = GLHelper.glCreateProgram()
        val vshID = createShader(vsh, GL20.GL_VERTEX_SHADER)
        val fshID = createShader(fsh, GL20.GL_FRAGMENT_SHADER)

        GLHelper.glAttachShader(id, vshID)
        GLHelper.glAttachShader(id, fshID)
        GLHelper.glLinkProgram(id)

        if (GLHelper.glGetProgrami(id, GL20.GL_LINK_STATUS) == 0) {
            GLHelper.glDeleteProgram(id)
            GLHelper.glDeleteShader(vshID)
            GLHelper.glDeleteShader(fshID)
            throw IllegalStateException("Failed to link shader " + GL20.glGetProgramInfoLog(id, 1024))
        }

        GLHelper.glDeleteShader(vshID)
        GLHelper.glDeleteShader(fshID)
        this.id = id
    }

    val isValidShader get() = id != 0

    private fun createShader(path: String, shaderType: Int): Int =
        createShader(javaClass.getResourceAsStream(path)!!.readBytes(), shaderType)

    private fun createShader(bytes: ByteArray, shaderType: Int): Int {
        val srcString = bytes.decodeToString()
        val shaderId = GLHelper.glCreateShader(shaderType)

        GLHelper.glShaderSource(shaderId, srcString)
        GLHelper.glCompileShader(shaderId)

        if (GLHelper.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == 0) {
            GLHelper.glDeleteShader(shaderId)
            return 0
        }
        return shaderId
    }

    fun safeBind() {
        if (!isValidShader) throw Exception("Invalid shader(vsh:$vsh, fsh:$fsh)")
        else GLHelper.useProgram(id)
    }

    fun bind() = GLHelper.useProgram(id)

    fun unbind() = GLHelper.unbindProgram()

    fun destroy() = GLHelper.glDeleteProgram(id)

    @Deprecated("Get uniform location when called", ReplaceWith("glUniform1i(gl)", "org.lwjgl.opengl.GL20.glUniform1i"))
    fun setBoolean(name: String, value: Boolean) {
        setBoolean(getUniformLocation(name), value)
    }

    @Deprecated("Get uniform location when called", ReplaceWith("glUniform1i(gl)", "org.lwjgl.opengl.GL20.glUniform1i"))
    fun setInt(name: String, value: Int) {
        setInt(getUniformLocation(name), value)
    }

    @Deprecated("Get uniform location when called", ReplaceWith("glUniform1f(gl)", "org.lwjgl.opengl.GL20.glUniform1f"))
    fun setFloat(name: String, value: Float) {
        setFloat(getUniformLocation(name), value)
    }

    fun setVec4(name: String, x: Float, y: Float, z: Float, w: Float) {
        GLHelper.glUniform4(getUniformLocation(name), x, y, z, w)
    }

    fun setVec3(name: String, x: Float, y: Float, z: Float) {
        GLHelper.glUniform3(getUniformLocation(name), x, y, z)
    }

    fun setVec2(name: String, x: Float, y: Float) {
        GLHelper.glUniform2(getUniformLocation(name), x, y)
    }

    fun getUniformLocation(name: String) = GLHelper.glGetUniformLocation(id, name)

    fun setBoolean(location: Int, value: Boolean) {
        GLHelper.glUniform1(location, if (value) 1 else 0)
    }

    fun setInt(location: Int, value: Int) {
        GLHelper.glUniform1(location, value)
    }

    fun setFloat(location: Int, value: Float) {
        GLHelper.glUniform1(location, value)
    }

    //fun uploadMatrix(location: Int, matrix: FloatBuffer) {
    //    GLStateManager.glUniformMatrix4(location, false, matrix)
    //}

}

@OptIn(ExperimentalContracts::class)
inline fun <T : Shader> T.use(block: T.() -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    bind()
    block.invoke(this)
    GLHelper.unbindProgram()
}