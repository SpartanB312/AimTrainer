package net.spartanb312.boar.graphics

import net.spartanb312.boar.graphics.drawing.VertexFormat
import net.spartanb312.boar.graphics.drawing.buffer.ArrayedVertexBuffer.buffer
import net.spartanb312.boar.graphics.drawing.buffer.PersistentMappedVertexBuffer
import net.spartanb312.boar.graphics.drawing.buffer.PersistentMappedVertexBuffer.draw
import net.spartanb312.boar.graphics.texture.Texture
import net.spartanb312.boar.graphics.texture.useTexture
import net.spartanb312.boar.utils.color.ColorRGB
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

        if (RS.compatMode) {
            GLHelper.texture2d = true
            front.useTexture {
                GL11.GL_QUADS.buffer(VertexFormat.Pos3fColorTex, 4) {
                    v3Tex2fC(maxX - offset, maxY - offset, maxZ - offset, 1f, 0f)
                    v3Tex2fC(minX + offset, maxY - offset, maxZ - offset, 0f, 0f)
                    v3Tex2fC(minX + offset, minY + offset, maxZ - offset, 0f, 1f)
                    v3Tex2fC(maxX - offset, minY + offset, maxZ - offset, 1f, 1f)
                }
            }

            right.useTexture {
                GL11.GL_QUADS.buffer(VertexFormat.Pos3fColorTex, 4) {
                    v3Tex2fC(maxX - offset, maxY - offset, minZ + offset, 1f, 0f)
                    v3Tex2fC(maxX - offset, maxY - offset, maxZ - offset, 0f, 0f)
                    v3Tex2fC(maxX - offset, minY + offset, maxZ - offset, 0f, 1f)
                    v3Tex2fC(maxX - offset, minY + offset, minZ + offset, 1f, 1f)
                }
            }

            back.useTexture {
                GL11.GL_QUADS.buffer(VertexFormat.Pos3fColorTex, 4) {
                    v3Tex2fC(minX + offset, maxY - offset, minZ + offset, 1f, 0f)
                    v3Tex2fC(maxX - offset, maxY - offset, minZ + offset, 0f, 0f)
                    v3Tex2fC(maxX - offset, minY + offset, minZ + offset, 0f, 1f)
                    v3Tex2fC(minX + offset, minY + offset, minZ + offset, 1f, 1f)
                }
            }

            left.useTexture {
                GL11.GL_QUADS.buffer(VertexFormat.Pos3fColorTex, 4) {
                    v3Tex2fC(minX + offset, maxY - offset, maxZ - offset, 1f, 0f)
                    v3Tex2fC(minX + offset, maxY - offset, minZ + offset, 0f, 0f)
                    v3Tex2fC(minX + offset, minY + offset, minZ + offset, 0f, 1f)
                    v3Tex2fC(minX + offset, minY + offset, maxZ - offset, 1f, 1f)
                }
            }

            up.useTexture {
                GL11.GL_QUADS.buffer(VertexFormat.Pos3fColorTex, 4) {
                    v3Tex2fC(maxX - offset, maxY - offset, minZ + offset, 1f, 0f)
                    v3Tex2fC(minX + offset, maxY - offset, minZ + offset, 0f, 0f)
                    v3Tex2fC(minX + offset, maxY - offset, maxZ - offset, 0f, 1f)
                    v3Tex2fC(maxX - offset, maxY - offset, maxZ - offset, 1f, 1f)
                }
            }

            down.useTexture {
                GL11.GL_QUADS.buffer(VertexFormat.Pos3fColorTex, 4) {
                    v3Tex2fC(maxX - offset, minY + offset, maxZ - offset, 1f, 0f)
                    v3Tex2fC(minX + offset, minY + offset, maxZ - offset, 0f, 0f)
                    v3Tex2fC(minX + offset, minY + offset, minZ + offset, 0f, 1f)
                    v3Tex2fC(maxX - offset, minY + offset, minZ + offset, 1f, 1f)
                }
            }
            GLHelper.texture2d = false
        } else {
            front.useTexture {
                GL11.GL_QUADS.draw(PersistentMappedVertexBuffer.VertexMode.Universe) {
                    universe(maxX - offset, maxY - offset, maxZ - offset, 1f, 0f, ColorRGB.WHITE)
                    universe(minX + offset, maxY - offset, maxZ - offset, 0f, 0f, ColorRGB.WHITE)
                    universe(minX + offset, minY + offset, maxZ - offset, 0f, 1f, ColorRGB.WHITE)
                    universe(maxX - offset, minY + offset, maxZ - offset, 1f, 1f, ColorRGB.WHITE)
                }
            }

            right.useTexture {
                GL11.GL_QUADS.draw(PersistentMappedVertexBuffer.VertexMode.Universe) {
                    universe(maxX - offset, maxY - offset, minZ + offset, 1f, 0f, ColorRGB.WHITE)
                    universe(maxX - offset, maxY - offset, maxZ - offset, 0f, 0f, ColorRGB.WHITE)
                    universe(maxX - offset, minY + offset, maxZ - offset, 0f, 1f, ColorRGB.WHITE)
                    universe(maxX - offset, minY + offset, minZ + offset, 1f, 1f, ColorRGB.WHITE)
                }
            }

            back.useTexture {
                GL11.GL_QUADS.draw(PersistentMappedVertexBuffer.VertexMode.Universe) {
                    universe(minX + offset, maxY - offset, minZ + offset, 1f, 0f, ColorRGB.WHITE)
                    universe(maxX - offset, maxY - offset, minZ + offset, 0f, 0f, ColorRGB.WHITE)
                    universe(maxX - offset, minY + offset, minZ + offset, 0f, 1f, ColorRGB.WHITE)
                    universe(minX + offset, minY + offset, minZ + offset, 1f, 1f, ColorRGB.WHITE)
                }
            }

            left.useTexture {
                GL11.GL_QUADS.draw(PersistentMappedVertexBuffer.VertexMode.Universe) {
                    universe(minX + offset, maxY - offset, maxZ - offset, 1f, 0f, ColorRGB.WHITE)
                    universe(minX + offset, maxY - offset, minZ + offset, 0f, 0f, ColorRGB.WHITE)
                    universe(minX + offset, minY + offset, minZ + offset, 0f, 1f, ColorRGB.WHITE)
                    universe(minX + offset, minY + offset, maxZ - offset, 1f, 1f, ColorRGB.WHITE)
                }
            }

            up.useTexture {
                GL11.GL_QUADS.draw(PersistentMappedVertexBuffer.VertexMode.Universe) {
                    universe(maxX - offset, maxY - offset, minZ + offset, 1f, 0f, ColorRGB.WHITE)
                    universe(minX + offset, maxY - offset, minZ + offset, 0f, 0f, ColorRGB.WHITE)
                    universe(minX + offset, maxY - offset, maxZ - offset, 0f, 1f, ColorRGB.WHITE)
                    universe(maxX - offset, maxY - offset, maxZ - offset, 1f, 1f, ColorRGB.WHITE)
                }
            }

            down.useTexture {
                GL11.GL_QUADS.draw(PersistentMappedVertexBuffer.VertexMode.Universe) {
                    universe(maxX - offset, minY + offset, maxZ - offset, 1f, 0f, ColorRGB.WHITE)
                    universe(minX + offset, minY + offset, maxZ - offset, 0f, 0f, ColorRGB.WHITE)
                    universe(minX + offset, minY + offset, minZ + offset, 0f, 1f, ColorRGB.WHITE)
                    universe(maxX - offset, minY + offset, minZ + offset, 1f, 1f, ColorRGB.WHITE)
                }
            }
        }

        GL11.glCullFace(GL11.GL_BACK)
    }

}