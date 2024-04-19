package net.spartanb312.boar.graphics.shader

import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.graphics.drawing.VertexFormat
import net.spartanb312.boar.graphics.drawing.buffer.ArrayedVertexBuffer.buffer
import net.spartanb312.boar.graphics.drawing.buffer.PersistentMappedVertexBuffer
import net.spartanb312.boar.graphics.drawing.buffer.PersistentMappedVertexBuffer.draw
import net.spartanb312.boar.utils.color.ColorRGB
import org.lwjgl.opengl.GL20.*

open class GLSLSandboxShader(vertexShaderPath: String, fragShaderPath: String) :
    Shader(vertexShaderPath, fragShaderPath) {

    private val timeUniform = glGetUniformLocation(id, "time")
    private val mouseUniform = glGetUniformLocation(id, "mouse")
    private val resolutionUniform = glGetUniformLocation(id, "resolution")

    fun render(width: Float, height: Float, mouseX: Float, mouseY: Float, initTime: Long) {
        bind()
        glUniform2f(resolutionUniform, width, height)
        glUniform2f(mouseUniform, mouseX / width, (height - 1.0f - mouseY) / height)
        glUniform1f(timeUniform, ((System.currentTimeMillis() - initTime) / 1000.0).toFloat())
        if (RS.compatMode) GL_QUADS.buffer(VertexFormat.Pos2fColor) {
            v2fc(-1.0f, -1.0f, ColorRGB.WHITE)
            v2fc(1.0f, -1.0f, ColorRGB.WHITE)
            v2fc(1.0f, 1.0f, ColorRGB.WHITE)
            v2fc(-1.0f, 1.0f, ColorRGB.WHITE)
        } else GL_QUADS.draw(PersistentMappedVertexBuffer.VertexMode.Pos2fColor, this) {
            putVertex(-1.0f, -1.0f, ColorRGB.WHITE)
            putVertex(1.0f, -1.0f, ColorRGB.WHITE)
            putVertex(1.0f, 1.0f, ColorRGB.WHITE)
            putVertex(-1.0f, 1.0f, ColorRGB.WHITE)
        }
        if (RS.compatMode) unbind()
    }

}