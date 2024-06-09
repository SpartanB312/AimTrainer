package net.spartanb312.everett.graphics.shader

import net.spartanb312.everett.graphics.drawing.pmvbo.PersistentMappedVertexBuffer
import net.spartanb312.everett.graphics.drawing.pmvbo.PersistentMappedVertexBuffer.draw
import net.spartanb312.everett.utils.color.ColorRGB
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL20.GL_TRIANGLE_STRIP

open class GLSLSandboxShader(vertexShaderPath: String, fragShaderPath: String) :
    Shader(vertexShaderPath, fragShaderPath) {

    private val timeUniform = getUniformLocation("time")
    private val mouseUniform = getUniformLocation("mouse")
    private val resolutionUniform = getUniformLocation("resolution")

    fun render(width: Float, height: Float, mouseX: Float, mouseY: Float, initTime: Long) {
        bind()
        GL20.glUniform2f(resolutionUniform, width, height)
        GL20.glUniform2f(mouseUniform, mouseX / width, (height - 1.0f - mouseY) / height)
        GL20.glUniform1f(timeUniform, ((System.currentTimeMillis() - initTime) / 1000.0).toFloat())
        GL_TRIANGLE_STRIP.draw(PersistentMappedVertexBuffer.VertexMode.Pos2fColor, this) {
            putVertex(-1.0f, -1.0f, ColorRGB.WHITE)
            putVertex(1.0f, -1.0f, ColorRGB.WHITE)
            putVertex(-1.0f, 1.0f, ColorRGB.WHITE)
            putVertex(1.0f, 1.0f, ColorRGB.WHITE)
        }
    }

}