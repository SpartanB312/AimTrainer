package net.spartanb312.everett.graphics

import net.spartanb312.everett.graphics.drawing.pmvbo.PersistentMappedVertexBuffer
import net.spartanb312.everett.graphics.drawing.pmvbo.PersistentMappedVertexBuffer.draw
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

    fun onRender3D() {
        val offset = 0.01f
        GL11.glCullFace(GL11.GL_FRONT)

        front.useTexture {
            GL11.GL_TRIANGLE_STRIP.draw(PersistentMappedVertexBuffer.VertexMode.Universal) {
                universal(maxX - offset, maxY - offset, maxZ - offset, 1f, 0f, ColorRGB.WHITE)
                universal(minX + offset, maxY - offset, maxZ - offset, 0f, 0f, ColorRGB.WHITE)
                universal(maxX - offset, minY + offset, maxZ - offset, 1f, 1f, ColorRGB.WHITE)
                universal(minX + offset, minY + offset, maxZ - offset, 0f, 1f, ColorRGB.WHITE)
            }
        }

        right.useTexture {
            GL11.GL_TRIANGLE_STRIP.draw(PersistentMappedVertexBuffer.VertexMode.Universal) {
                universal(maxX - offset, maxY - offset, minZ + offset, 1f, 0f, ColorRGB.WHITE)
                universal(maxX - offset, maxY - offset, maxZ - offset, 0f, 0f, ColorRGB.WHITE)
                universal(maxX - offset, minY + offset, minZ + offset, 1f, 1f, ColorRGB.WHITE)
                universal(maxX - offset, minY + offset, maxZ - offset, 0f, 1f, ColorRGB.WHITE)
            }
        }

        back.useTexture {
            GL11.GL_TRIANGLE_STRIP.draw(PersistentMappedVertexBuffer.VertexMode.Universal) {
                universal(minX + offset, maxY - offset, minZ + offset, 1f, 0f, ColorRGB.WHITE)
                universal(maxX - offset, maxY - offset, minZ + offset, 0f, 0f, ColorRGB.WHITE)
                universal(minX + offset, minY + offset, minZ + offset, 1f, 1f, ColorRGB.WHITE)
                universal(maxX - offset, minY + offset, minZ + offset, 0f, 1f, ColorRGB.WHITE)
            }
        }

        left.useTexture {
            GL11.GL_TRIANGLE_STRIP.draw(PersistentMappedVertexBuffer.VertexMode.Universal) {
                universal(minX + offset, maxY - offset, maxZ - offset, 1f, 0f, ColorRGB.WHITE)
                universal(minX + offset, maxY - offset, minZ + offset, 0f, 0f, ColorRGB.WHITE)
                universal(minX + offset, minY + offset, maxZ - offset, 1f, 1f, ColorRGB.WHITE)
                universal(minX + offset, minY + offset, minZ + offset, 0f, 1f, ColorRGB.WHITE)
            }
        }

        up.useTexture {
            GL11.GL_TRIANGLE_STRIP.draw(PersistentMappedVertexBuffer.VertexMode.Universal) {
                universal(maxX - offset, maxY - offset, minZ + offset, 1f, 0f, ColorRGB.WHITE)
                universal(minX + offset, maxY - offset, minZ + offset, 0f, 0f, ColorRGB.WHITE)
                universal(maxX - offset, maxY - offset, maxZ - offset, 1f, 1f, ColorRGB.WHITE)
                universal(minX + offset, maxY - offset, maxZ - offset, 0f, 1f, ColorRGB.WHITE)
            }
        }

        down.useTexture {
            GL11.GL_TRIANGLE_STRIP.draw(PersistentMappedVertexBuffer.VertexMode.Universal) {
                universal(maxX - offset, minY + offset, maxZ - offset, 1f, 0f, ColorRGB.WHITE)
                universal(minX + offset, minY + offset, maxZ - offset, 0f, 0f, ColorRGB.WHITE)
                universal(maxX - offset, minY + offset, minZ + offset, 1f, 1f, ColorRGB.WHITE)
                universal(minX + offset, minY + offset, minZ + offset, 0f, 1f, ColorRGB.WHITE)
            }
        }

        GL11.glCullFace(GL11.GL_BACK)
    }

}