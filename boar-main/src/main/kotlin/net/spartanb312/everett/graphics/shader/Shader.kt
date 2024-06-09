package net.spartanb312.everett.graphics.shader

import net.spartanb312.everett.graphics.GLHelper
import net.spartanb312.everett.graphics.GLObject
import net.spartanb312.everett.graphics.OpenGL.*
import net.spartanb312.everett.graphics.matrix.getFloatArray
import net.spartanb312.everett.utils.Logger
import net.spartanb312.everett.utils.ResourceHelper
import net.spartanb312.everett.utils.math.vector.Vec2f
import net.spartanb312.everett.utils.math.vector.Vec2i
import net.spartanb312.everett.utils.math.vector.Vec3f
import net.spartanb312.everett.utils.math.vector.Vec3i
import org.joml.Matrix4f
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
        val id = GL20.glCreateProgram()
        val vshID = createShader(vsh, GL_VERTEX_SHADER)
        val fshID = createShader(fsh, GL_FRAGMENT_SHADER)

        GL20.glAttachShader(id, vshID)
        GL20.glAttachShader(id, fshID)
        GL20.glLinkProgram(id)

        if (GL20.glGetProgrami(id, GL_LINK_STATUS) == 0) {
            GL20.glDeleteProgram(id)
            GL20.glDeleteShader(vshID)
            GL20.glDeleteShader(fshID)
            throw IllegalStateException("Failed to link shader " + glGetProgramInfoLog(id, 1024))
        }

        GL20.glDeleteShader(vshID)
        GL20.glDeleteShader(fshID)
        this.id = id
    }

    val isValidShader get() = id != 0

    private fun createShader(path: String, shaderType: Int): Int =
        createShader(path, ResourceHelper.getResourceStream(path)!!.readBytes(), shaderType)

    private fun createShader(path: String, bytes: ByteArray, shaderType: Int): Int {
        val srcString = bytes.decodeToString()
        val shaderId = GL20.glCreateShader(shaderType)

        GL20.glShaderSource(shaderId, srcString)
        GL20.glCompileShader(shaderId)

        if (GL20.glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            GL20.glDeleteShader(shaderId)
            Logger.error("Failed to create ${if (shaderType == GL_VERTEX_SHADER) "vertex shader" else "frag shader"}: $path")
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

    fun destroy() = GL20.glDeleteProgram(id)

    fun getUniformLocation(name: String) = GL20.glGetUniformLocation(id, name)

}

fun Boolean.glUniform(location: Int) {
    GL20.glUniform1i(location, if (this) 1 else 0)
}

fun Int.glUniform(location: Int) {
    GL20.glUniform1i(location, this)
}

fun Float.glUniform(location: Int) {
    GL20.glUniform1f(location, this)
}

fun Vec2i.glUniform(location: Int) {
    GL20.glUniform2i(location, this.x, this.y)
}

fun Vec2f.glUniform(location: Int) {
    GL20.glUniform2f(location, this.x, this.y)
}

fun Vec3i.glUniform(location: Int) {
    GL20.glUniform3i(location, this.x, this.y, this.z)
}

fun Vec3f.glUniform(location: Int) {
    GL20.glUniform3f(location, this.x, this.y, this.z)
}

fun Matrix4f.glUniform(location: Int, transpose: Boolean = false) {
    GL20.glUniformMatrix4fv(location, transpose, this.getFloatArray())
}

fun FloatArray.glUniform(location: Int, transpose: Boolean = false) {
    GL20.glUniformMatrix4fv(location, transpose, this)
}

@OptIn(ExperimentalContracts::class)
inline fun <T : Shader> T.useShader(block: T.() -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    bind()
    block.invoke(this)
    GLHelper.unbindProgram()
}