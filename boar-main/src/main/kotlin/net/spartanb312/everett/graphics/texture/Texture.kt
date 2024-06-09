package net.spartanb312.everett.graphics.texture

import net.spartanb312.everett.graphics.GLHelper
import net.spartanb312.everett.graphics.OpenGL
import net.spartanb312.everett.graphics.drawing.pmvbo.PersistentMappedVertexBuffer
import net.spartanb312.everett.graphics.drawing.pmvbo.PersistentMappedVertexBuffer.draw
import net.spartanb312.everett.utils.color.ColorRGB

interface Texture {
    val id: Int
    var width: Int
    var height: Int
    val available: Boolean
    fun bindTexture()
    fun unbindTexture()
    fun deleteTexture()
}

inline fun <T : Texture> T.useTexture(
    block: Texture.() -> Unit
): T {
    bindTexture()
    block()
    unbindTexture()
    return this
}

fun <T : Texture> T.drawTexture(
    startX: Float,
    startY: Float,
    endX: Float,
    endY: Float,
    u: Int = 0,
    v: Int = 0,
    u1: Int = width,
    v1: Int = height,
    colorRGB: ColorRGB = ColorRGB.WHITE
) = useTexture {
    val startU = u / width.toFloat()
    val endU = u1 / width.toFloat()
    val startV = v / height.toFloat()
    val endV = v1 / height.toFloat()
    GLHelper.blend = true
    OpenGL.GL_TRIANGLE_STRIP.draw(PersistentMappedVertexBuffer.VertexMode.Universal) {
        universal(endX, startY, endU, startV, colorRGB)
        universal(startX, startY, startU, startV, colorRGB)
        universal(endX, endY, endU, endV, colorRGB)
        universal(startX, endY, startU, endV, colorRGB)
    }
}
