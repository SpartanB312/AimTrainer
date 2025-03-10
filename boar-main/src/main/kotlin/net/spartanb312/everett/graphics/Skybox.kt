package net.spartanb312.everett.graphics

import net.spartanb312.everett.graphics.OpenGL.*
import net.spartanb312.everett.graphics.drawing.pmvbo.PersistentMappedVertexBuffer
import net.spartanb312.everett.graphics.texture.Texture
import net.spartanb312.everett.graphics.texture.useTexture
import net.spartanb312.everett.utils.color.ColorRGB
import org.lwjgl.opengl.GL11

class Skybox(
    private val minX: Float,
    private val minY: Float,
    private val minZ: Float,
    private val maxX: Float,
    private val maxY: Float,
    private val maxZ: Float,
    private val down: Texture,
    private val up: Texture,
    private val left: Texture,
    private val front: Texture,
    private val right: Texture,
    private val back: Texture
) {
    init {
        listOf(up, down, left, front, right, back).forEach {
            it.useTexture {
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_LOD, 0)
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LOD, 0)
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL, 0)
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, 0)
            }
        }
    }

    fun onRender3D() {
        with(PersistentMappedVertexBuffer.VertexMode.Universal) {
            front.bindTexture()
            universal(maxX, maxY, minZ, 0.999f, 0.001f, ColorRGB.WHITE)
            universal(minX, maxY, minZ, 0.001f, 0.001f, ColorRGB.WHITE)
            universal(maxX, minY, minZ, 0.999f, 0.999f, ColorRGB.WHITE)
            universal(minX, minY, minZ, 0.001f, 0.999f, ColorRGB.WHITE)
            draw(GL11.GL_TRIANGLE_STRIP)
            right.bindTexture()
            universal(maxX, maxY, maxZ, 0.999f, 0.001f, ColorRGB.WHITE)
            universal(maxX, maxY, minZ, 0.001f, 0.001f, ColorRGB.WHITE)
            universal(maxX, minY, maxZ, 0.999f, 0.999f, ColorRGB.WHITE)
            universal(maxX, minY, minZ, 0.001f, 0.999f, ColorRGB.WHITE)
            draw(GL11.GL_TRIANGLE_STRIP)
            back.bindTexture()
            universal(minX, maxY, maxZ, 0.999f, 0.001f, ColorRGB.WHITE)
            universal(maxX, maxY, maxZ, 0.001f, 0.001f, ColorRGB.WHITE)
            universal(minX, minY, maxZ, 0.999f, 0.999f, ColorRGB.WHITE)
            universal(maxX, minY, maxZ, 0.001f, 0.999f, ColorRGB.WHITE)
            draw(GL11.GL_TRIANGLE_STRIP)
            left.bindTexture()
            universal(minX, maxY, minZ, 0.999f, 0.001f, ColorRGB.WHITE)
            universal(minX, maxY, maxZ, 0.001f, 0.001f, ColorRGB.WHITE)
            universal(minX, minY, minZ, 0.999f, 0.999f, ColorRGB.WHITE)
            universal(minX, minY, maxZ, 0.001f, 0.999f, ColorRGB.WHITE)
            draw(GL11.GL_TRIANGLE_STRIP)
            up.bindTexture()
            universal(maxX, maxY, maxZ, 0.999f, 0.001f, ColorRGB.WHITE)
            universal(minX, maxY, maxZ, 0.001f, 0.001f, ColorRGB.WHITE)
            universal(maxX, maxY, minZ, 0.999f, 0.999f, ColorRGB.WHITE)
            universal(minX, maxY, minZ, 0.001f, 0.999f, ColorRGB.WHITE)
            draw(GL11.GL_TRIANGLE_STRIP)
            down.bindTexture()
            universal(maxX, minY, minZ, 0.999f, 0.001f, ColorRGB.WHITE)
            universal(minX, minY, minZ, 0.001f, 0.001f, ColorRGB.WHITE)
            universal(maxX, minY, maxZ, 0.999f, 0.999f, ColorRGB.WHITE)
            universal(minX, minY, maxZ, 0.001f, 0.999f, ColorRGB.WHITE)
            draw(GL11.GL_TRIANGLE_STRIP)
        }
    }

}