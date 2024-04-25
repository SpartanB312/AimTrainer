package net.spartanb312.everett.graphics.compat.shader

import org.lwjgl.opengl.ARBShaderObjects
import org.lwjgl.opengl.ARBVertexShader
import java.nio.FloatBuffer

object ARBShaders : IShaders {

    override fun glGetProgrami(program: Int, pname: Int): Int =
        ARBShaderObjects.glGetObjectParameteriARB(program, pname)

    override fun glAttachShader(program: Int, shaderIn: Int) =
        ARBShaderObjects.glAttachObjectARB(program, shaderIn)

    override fun glDeleteShader(shaderIn: Int) =
        ARBShaderObjects.glDeleteObjectARB(shaderIn)

    override fun glCreateShader(type: Int): Int =
        ARBShaderObjects.glCreateShaderObjectARB(type)

    override fun glShaderSource(shaderIn: Int, string: CharSequence) =
        ARBShaderObjects.glShaderSourceARB(shaderIn, string)

    override fun glCompileShader(shaderIn: Int) =
        ARBShaderObjects.glCompileShaderARB(shaderIn)

    override fun glGetShaderi(shaderIn: Int, pname: Int): Int =
        ARBShaderObjects.glGetObjectParameteriARB(shaderIn, pname)

    override fun glGetShaderInfoLog(shaderIn: Int, maxLength: Int): String =
        ARBShaderObjects.glGetInfoLogARB(shaderIn, maxLength)

    override fun glGetProgramInfoLog(program: Int, maxLength: Int): String =
        ARBShaderObjects.glGetInfoLogARB(program, maxLength)

    override fun glUseProgram(program: Int) =
        ARBShaderObjects.glUseProgramObjectARB(program)

    override fun glCreateProgram(): Int =
        ARBShaderObjects.glCreateProgramObjectARB()

    override fun glDeleteProgram(program: Int) =
        ARBShaderObjects.glDeleteObjectARB(program)

    override fun glLinkProgram(program: Int) =
        ARBShaderObjects.glLinkProgramARB(program)

    override fun glGetUniformLocation(programObj: Int, name: CharSequence): Int =
        ARBShaderObjects.glGetUniformLocationARB(programObj, name)

    override fun glUniform1(location: Int, v0: Int) =
        ARBShaderObjects.glUniform1iARB(location, v0)

    override fun glUniform1(location: Int, v0: Float) =
        ARBShaderObjects.glUniform1fARB(location, v0)

    override fun glUniform2(location: Int, v0: Int, v1: Int) =
        ARBShaderObjects.glUniform2iARB(location, v0, v1)

    override fun glUniform2(location: Int, v0: Float, v1: Float) =
        ARBShaderObjects.glUniform2fARB(location, v0, v1)

    override fun glUniform3(location: Int, v0: Int, v1: Int, v2: Int) =
        ARBShaderObjects.glUniform3iARB(location, v0, v1, v2)

    override fun glUniform3(location: Int, v0: Float, v1: Float, v2: Float) =
        ARBShaderObjects.glUniform3fARB(location, v0, v1, v2)

    override fun glUniform4(location: Int, v0: Int, v1: Int, v2: Int, v3: Int) =
        ARBShaderObjects.glUniform4iARB(location, v0, v1, v2, v3)

    override fun glUniform4(location: Int, v0: Float, v1: Float, v2: Float, v3: Float) =
        ARBShaderObjects.glUniform4fARB(location, v0, v1, v2, v3)

    override fun glUniformMatrix2(location: Int, transpose: Boolean, matrices: FloatBuffer) =
        ARBShaderObjects.glUniformMatrix2fvARB(location, transpose, matrices)

    override fun glUniformMatrix3(location: Int, transpose: Boolean, matrices: FloatBuffer) =
        ARBShaderObjects.glUniformMatrix3fvARB(location, transpose, matrices)

    override fun glUniformMatrix4(location: Int, transpose: Boolean, matrices: FloatBuffer) =
        ARBShaderObjects.glUniformMatrix4fvARB(location, transpose, matrices)

    override fun glUniformMatrix2(location: Int, transpose: Boolean, matrices: FloatArray) =
        ARBShaderObjects.glUniformMatrix2fvARB(location, transpose, matrices)

    override fun glUniformMatrix3(location: Int, transpose: Boolean, matrices: FloatArray) =
        ARBShaderObjects.glUniformMatrix3fvARB(location, transpose, matrices)

    override fun glUniformMatrix4(location: Int, transpose: Boolean, matrices: FloatArray) =
        ARBShaderObjects.glUniformMatrix4fvARB(location, transpose, matrices)

    override fun glGetAttribLocation(program: Int, name: CharSequence): Int =
        ARBVertexShader.glGetAttribLocationARB(program, name)

    override fun glDetachShader(program: Int, shaderId: Int) =
        ARBShaderObjects.glDetachObjectARB(program, shaderId)

}