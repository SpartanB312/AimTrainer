package net.spartanb312.boar.graphics.compat.shader

import net.spartanb312.boar.graphics.compat.Delegate
import java.nio.FloatBuffer

interface IShaders {

    @Delegate
    fun glGetProgrami(program: Int, pname: Int): Int

    @Delegate
    fun glAttachShader(program: Int, shaderIn: Int)

    @Delegate
    fun glDeleteShader(shaderIn: Int)

    @Delegate
    fun glCreateShader(type: Int): Int

    @Delegate
    fun glShaderSource(shaderIn: Int, string: CharSequence)

    @Delegate
    fun glCompileShader(shaderIn: Int)

    @Delegate
    fun glGetShaderi(shaderIn: Int, pname: Int): Int

    @Delegate
    fun glGetShaderInfoLog(shaderIn: Int, maxLength: Int): String

    @Delegate
    fun glGetProgramInfoLog(program: Int, maxLength: Int): String

    @Delegate
    fun glUseProgram(program: Int)

    @Delegate
    fun glCreateProgram(): Int

    @Delegate
    fun glDeleteProgram(program: Int)

    @Delegate
    fun glLinkProgram(program: Int)

    @Delegate
    fun glGetUniformLocation(programObj: Int, name: CharSequence): Int

    @Delegate
    fun glUniform1(location: Int, v0: Int)

    @Delegate
    fun glUniform1(location: Int, v0: Float)

    @Delegate
    fun glUniform2(location: Int, v0: Int, v1: Int)

    @Delegate
    fun glUniform2(location: Int, v0: Float, v1: Float)

    @Delegate
    fun glUniform3(location: Int, v0: Int, v1: Int, v2: Int)

    @Delegate
    fun glUniform3(location: Int, v0: Float, v1: Float, v2: Float)

    @Delegate
    fun glUniform4(location: Int, v0: Int, v1: Int, v2: Int, v3: Int)

    @Delegate
    fun glUniform4(location: Int, v0: Float, v1: Float, v2: Float, v3: Float)

    @Delegate
    fun glUniformMatrix2(location: Int, transpose: Boolean, matrices: FloatBuffer)

    @Delegate
    fun glUniformMatrix3(location: Int, transpose: Boolean, matrices: FloatBuffer)

    @Delegate
    fun glUniformMatrix4(location: Int, transpose: Boolean, matrices: FloatBuffer)

    @Delegate
    fun glGetAttribLocation(program: Int, name: CharSequence): Int

    @Delegate
    fun glDetachShader(program: Int, shaderId: Int)

}