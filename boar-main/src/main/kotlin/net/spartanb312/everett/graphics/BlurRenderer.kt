package net.spartanb312.everett.graphics

import net.spartanb312.everett.graphics.matrix.getFloatArray
import net.spartanb312.everett.graphics.shader.Shader
import net.spartanb312.everett.graphics.shader.useShader
import org.joml.Matrix4f
import org.lwjgl.opengl.GL20

object BlurRenderer {
    //private val passV =

    private class Pass(vsh: String) : Shader(vsh, "assets/shader/general/Blur.fsh") {
        val reverseProjection = getUniformLocation("reverseProjection")
        val resolution = getUniformLocation("resolution")
        val extend = getUniformLocation("extend")

        init {
            useShader {
                updateResolution(RS.widthF, RS.heightF)
                GL20.glUniform1i(getUniformLocation("background"), 0)
            }
        }

        fun updateResolution(width: Float, height: Float) {
            GL20.glUniform2f(resolution, width, height)
            val matrix = Matrix4f()
                .ortho(0.0f, width, 0.0f, height, 1000.0f, 3000.0f)
                .invert()
            GL20.glUniformMatrix4fv(reverseProjection, false, matrix.getFloatArray())
        }

        fun updateExtend(x: Float, y: Float) {
            GL20.glUniform2f(extend, x, y)
        }
    }

}