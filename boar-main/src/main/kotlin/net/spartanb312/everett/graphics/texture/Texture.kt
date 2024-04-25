package net.spartanb312.everett.graphics.texture

import net.spartanb312.everett.graphics.GLHelper
import net.spartanb312.everett.graphics.OpenGL
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.drawing.VertexFormat
import net.spartanb312.everett.graphics.drawing.buffer.ArrayedVertexBuffer.buffer
import net.spartanb312.everett.graphics.drawing.buffer.PersistentMappedVertexBuffer
import net.spartanb312.everett.graphics.drawing.buffer.PersistentMappedVertexBuffer.draw
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
) {
    if (RS.compatMode) drawTextureOld(startX, startY, endX, endY, u, v, u1, v1, colorRGB)
    else drawTextureNew(startX, startY, endX, endY, u, v, u1, v1, colorRGB)
}

fun <T : Texture> T.drawTextureOld(
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
    GLHelper.texture2d = true
    GLHelper.alpha = true
    GLHelper.blend = true
    OpenGL.GL_QUADS.buffer(VertexFormat.Pos2fColorTex, 4) {
        v2Tex2fC(endX, startY, endU, startV, colorRGB)
        v2Tex2fC(startX, startY, startU, startV, colorRGB)
        v2Tex2fC(startX, endY, startU, endV, colorRGB)
        v2Tex2fC(endX, endY, endU, endV, colorRGB)
    }
    GLHelper.texture2d = false
}

fun <T : Texture> T.drawTextureNew(
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
    GLHelper.alpha = true
    GLHelper.blend = true
    OpenGL.GL_TRIANGLE_STRIP.draw(PersistentMappedVertexBuffer.VertexMode.Universal) {
        universal(endX, startY, endU, startV, colorRGB)
        universal(startX, startY, startU, startV, colorRGB)
        universal(endX, endY, endU, endV, colorRGB)
        universal(startX, endY, startU, endV, colorRGB)
    }
}
