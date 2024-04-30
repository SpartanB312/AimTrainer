package net.spartanb312.everett.graphics.shader

import net.spartanb312.everett.graphics.GLHelper
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.drawing.VertexFormat
import net.spartanb312.everett.graphics.drawing.buffer.ArrayedVertexBuffer.buffer
import net.spartanb312.everett.graphics.drawing.buffer.PersistentMappedVertexBuffer
import net.spartanb312.everett.graphics.drawing.buffer.PersistentMappedVertexBuffer.draw
import net.spartanb312.everett.utils.color.ColorRGB
import org.lwjgl.opengl.GL20.GL_QUADS
import org.lwjgl.opengl.GL20.GL_TRIANGLE_STRIP

open class GLSLSandboxShader(vertexShaderPath: String, fragShaderPath: String) :
    Shader(vertexShaderPath, fragShaderPath) {

    private val timeUniform = getUniformLocation("time")
    private val mouseUniform = getUniformLocation("mouse")
    private val resolutionUniform = getUniformLocation("resolution")

    fun render(width: Float, height: Float, mouseX: Float, mouseY: Float, initTime: Long) {
        bind()
        GLHelper.glUniform2(resolutionUniform, width, height)
        GLHelper.glUniform2(mouseUniform, mouseX / width, (height - 1.0f - mouseY) / height)
        GLHelper.glUniform1(timeUniform, ((System.currentTimeMillis() - initTime) / 1000.0).toFloat())
        if (RS.compatMode) GL_QUADS.buffer(VertexFormat.Pos2fColor) {
            v2fc(-1.0f, -1.0f, ColorRGB.WHITE)
            v2fc(1.0f, -1.0f, ColorRGB.WHITE)
            v2fc(1.0f, 1.0f, ColorRGB.WHITE)
            v2fc(-1.0f, 1.0f, ColorRGB.WHITE)
        } else GL_TRIANGLE_STRIP.draw(PersistentMappedVertexBuffer.VertexMode.Pos2fColor, this) {
            putVertex(-1.0f, -1.0f, ColorRGB.WHITE)
            putVertex(1.0f, -1.0f, ColorRGB.WHITE)
            putVertex(-1.0f, 1.0f, ColorRGB.WHITE)
            putVertex(1.0f, 1.0f, ColorRGB.WHITE)
        }
        if (RS.compatMode) unbind()
    }

}