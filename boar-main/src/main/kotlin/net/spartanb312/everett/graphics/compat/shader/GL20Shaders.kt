package net.spartanb312.everett.graphics.compat.shader

import org.lwjgl.opengl.GL20C
import java.nio.FloatBuffer

object GL20Shaders : IShaders {

    override fun glGetProgrami(program: Int, pname: Int): Int =
        GL20C.glGetProgrami(program, pname)

    override fun glAttachShader(program: Int, shaderIn: Int) =
        GL20C.glAttachShader(program, shaderIn)

    override fun glDeleteShader(shaderIn: Int) =
        GL20C.glDeleteShader(shaderIn)

    override fun glCreateShader(type: Int): Int =
        GL20C.glCreateShader(type)

    override fun glShaderSource(shaderIn: Int, string: CharSequence) =
        GL20C.glShaderSource(shaderIn, string)

    override fun glCompileShader(shaderIn: Int) =
        GL20C.glCompileShader(shaderIn)

    override fun glGetShaderi(shaderIn: Int, pname: Int): Int =
        GL20C.glGetShaderi(shaderIn, pname)

    override fun glGetShaderInfoLog(shaderIn: Int, maxLength: Int): String =
        GL20C.glGetShaderInfoLog(shaderIn, maxLength)

    override fun glGetProgramInfoLog(program: Int, maxLength: Int): String =
        GL20C.glGetProgramInfoLog(program, maxLength)

    override fun glUseProgram(program: Int) =
        GL20C.glUseProgram(program)

    override fun glCreateProgram(): Int =
        GL20C.glCreateProgram()

    override fun glDeleteProgram(program: Int) =
        GL20C.glDeleteProgram(program)

    override fun glLinkProgram(program: Int) =
        GL20C.glLinkProgram(program)

    override fun glGetUniformLocation(programObj: Int, name: CharSequence): Int =
        GL20C.glGetUniformLocation(programObj, name)

    override fun glUniform1(location: Int, v0: Int) =
        GL20C.glUniform1i(location, v0)

    override fun glUniform1(location: Int, v0: Float) =
        GL20C.glUniform1f(location, v0)

    override fun glUniform2(location: Int, v0: Int, v1: Int) =
        GL20C.glUniform2i(location, v0, v1)

    override fun glUniform2(location: Int, v0: Float, v1: Float) =
        GL20C.glUniform2f(location, v0, v1)

    override fun glUniform3(location: Int, v0: Int, v1: Int, v2: Int) =
        GL20C.glUniform3i(location, v0, v1, v2)

    override fun glUniform3(location: Int, v0: Float, v1: Float, v2: Float) =
        GL20C.glUniform3f(location, v0, v1, v2)

    override fun glUniform4(location: Int, v0: Int, v1: Int, v2: Int, v3: Int) =
        GL20C.glUniform4i(location, v0, v1, v2, v3)

    override fun glUniform4(location: Int, v0: Float, v1: Float, v2: Float, v3: Float) =
        GL20C.glUniform4f(location, v0, v1, v2, v3)

    override fun glUniformMatrix2(location: Int, transpose: Boolean, matrices: FloatBuffer) =
        GL20C.glUniformMatrix2fv(location, transpose, matrices)

    override fun glUniformMatrix3(location: Int, transpose: Boolean, matrices: FloatBuffer) =
        GL20C.glUniformMatrix3fv(location, transpose, matrices)

    override fun glUniformMatrix4(location: Int, transpose: Boolean, matrices: FloatBuffer) =
        GL20C.glUniformMatrix4fv(location, transpose, matrices)

    override fun glUniformMatrix2(location: Int, transpose: Boolean, matrices: FloatArray) =
        GL20C.glUniformMatrix2fv(location, transpose, matrices)

    override fun glUniformMatrix3(location: Int, transpose: Boolean, matrices: FloatArray) =
        GL20C.glUniformMatrix3fv(location, transpose, matrices)

    override fun glUniformMatrix4(location: Int, transpose: Boolean, matrices: FloatArray) =
        GL20C.glUniformMatrix4fv(location, transpose, matrices)

    override fun glGetAttribLocation(program: Int, name: CharSequence): Int =
        GL20C.glGetAttribLocation(program, name)

    override fun glDetachShader(program: Int, shaderId: Int) =
        GL20C.glDetachShader(program, shaderId)

}