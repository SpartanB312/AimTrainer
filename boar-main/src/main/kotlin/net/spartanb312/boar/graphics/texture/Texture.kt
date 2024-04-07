package net.spartanb312.boar.graphics.texture

import net.spartanb312.boar.graphics.GLHelper
import net.spartanb312.boar.graphics.OpenGL
import net.spartanb312.boar.graphics.drawing.VertexBuffer.buffer
import net.spartanb312.boar.graphics.drawing.VertexFormat
import net.spartanb312.boar.utils.color.ColorRGB

interface Texture {
    val id: Int
    var width: Int
    var height: Int
    val available:Boolean
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
    GLHelper.texture2d = true
    GLHelper.alpha = true
    GLHelper.blend = true
    OpenGL.GL_QUADS.buffer(VertexFormat.Pos2fTex, 4) {
        v2Tex2fC(endX, startY, endU, startV, colorRGB)
        v2Tex2fC(startX, startY, startU, startV, colorRGB)
        v2Tex2fC(startX, endY, startU, endV, colorRGB)
        v2Tex2fC(endX, endY, endU, endV, colorRGB)
    }
    GLHelper.texture2d = false
}
