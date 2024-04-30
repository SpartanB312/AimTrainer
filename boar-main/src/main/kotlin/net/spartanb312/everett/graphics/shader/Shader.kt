package net.spartanb312.everett.graphics.shader

import net.spartanb312.everett.graphics.GLHelper
import net.spartanb312.everett.graphics.GLObject
import net.spartanb312.everett.graphics.OpenGL.*
import net.spartanb312.everett.graphics.compat.Delegate
import net.spartanb312.everett.graphics.matrix.getFloatArray
import net.spartanb312.everett.utils.Logger
import net.spartanb312.everett.utils.ResourceHelper
import net.spartanb312.everett.utils.math.vector.Vec2f
import net.spartanb312.everett.utils.math.vector.Vec2i
import net.spartanb312.everett.utils.math.vector.Vec3f
import net.spartanb312.everett.utils.math.vector.Vec3i
import org.joml.Matrix4f
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
        val vshID = createShader(vsh, GL_VERTEX_SHADER)
        val fshID = createShader(fsh, GL_FRAGMENT_SHADER)

        GLHelper.glAttachShader(id, vshID)
        GLHelper.glAttachShader(id, fshID)
        GLHelper.glLinkProgram(id)

        if (GLHelper.glGetProgrami(id, GL_LINK_STATUS) == 0) {
            GLHelper.glDeleteProgram(id)
            GLHelper.glDeleteShader(vshID)
            GLHelper.glDeleteShader(fshID)
            throw IllegalStateException("Failed to link shader " + glGetProgramInfoLog(id, 1024))
        }

        GLHelper.glDeleteShader(vshID)
        GLHelper.glDeleteShader(fshID)
        this.id = id
    }

    val isValidShader get() = id != 0

    private fun createShader(path: String, shaderType: Int): Int =
        createShader(path, ResourceHelper.getResourceStream(path)!!.readBytes(), shaderType)

    private fun createShader(path: String, bytes: ByteArray, shaderType: Int): Int {
        val srcString = bytes.decodeToString()
        val shaderId = GLHelper.glCreateShader(shaderType)

        GLHelper.glShaderSource(shaderId, srcString)
        GLHelper.glCompileShader(shaderId)

        if (GLHelper.glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            GLHelper.glDeleteShader(shaderId)
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

    fun destroy() = GLHelper.glDeleteProgram(id)

    @Delegate
    fun getUniformLocation(name: String) = GLHelper.glGetUniformLocation(id, name)

}

@Delegate
fun Boolean.glUniform(location: Int) {
    GLHelper.glUniform1(location, if (this) 1 else 0)
}

@Delegate
fun Int.glUniform(location: Int) {
    GLHelper.glUniform1(location, this)
}

@Delegate
fun Float.glUniform(location: Int) {
    GLHelper.glUniform1(location, this)
}

@Delegate
fun Vec2i.glUniform(location: Int) {
    GLHelper.glUniform2(location, this.x, this.y)
}

@Delegate
fun Vec2f.glUniform(location: Int) {
    GLHelper.glUniform2(location, this.x, this.y)
}

@Delegate
fun Vec3i.glUniform(location: Int) {
    GLHelper.glUniform3(location, this.x, this.y, this.z)
}

@Delegate
fun Vec3f.glUniform(location: Int) {
    GLHelper.glUniform3(location, this.x, this.y, this.z)
}

@Delegate
fun Matrix4f.glUniform(location: Int, transpose: Boolean = false) {
    GLHelper.glUniformMatrix4(location, transpose, this.getFloatArray())
}

@Delegate
fun FloatArray.glUniform(location: Int, transpose: Boolean = false) {
    GLHelper.glUniformMatrix4(location, transpose, this)
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