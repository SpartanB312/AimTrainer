package net.spartanb312.boar.graphics

import net.spartanb312.boar.graphics.drawing.VertexBuffer.buffer
import net.spartanb312.boar.graphics.drawing.VertexFormat
import net.spartanb312.boar.graphics.texture.Texture
import net.spartanb312.boar.graphics.texture.useTexture
import org.lwjgl.opengl.GL11

class Skybox(
    private val minX: Double,
    private val minY: Double,
    private val minZ: Double,
    private val maxX: Double,
    private val maxY: Double,
    private val maxZ: Double,
    private val down: Texture,
    private val up: Texture,
    private val left: Texture,
    private val front: Texture,
    private val right: Texture,
    private val back: Texture
) {

    fun onRender3D() {
        val offset = 0.01
        GLHelper.texture2d = true
        GL11.glCullFace(GL11.GL_FRONT)
        front.useTexture {
            GL11.GL_QUADS.buffer(VertexFormat.Pos3dTex, 4) {
                v3Tex2dC(maxX - offset, maxY - offset, maxZ - offset, 1f, 0f)
                v3Tex2dC(minX + offset, maxY - offset, maxZ - offset, 0f, 0f)
                v3Tex2dC(minX + offset, minY + offset, maxZ - offset, 0f, 1f)
                v3Tex2dC(maxX - offset, minY + offset, maxZ - offset, 1f, 1f)
            }
        }

        right.useTexture {
            GL11.GL_QUADS.buffer(VertexFormat.Pos3dTex, 4) {
                v3Tex2dC(maxX - offset, maxY - offset, minZ + offset, 1f, 0f)
                v3Tex2dC(maxX - offset, maxY - offset, maxZ - offset, 0f, 0f)
                v3Tex2dC(maxX - offset, minY + offset, maxZ - offset, 0f, 1f)
                v3Tex2dC(maxX - offset, minY + offset, minZ + offset, 1f, 1f)
            }
        }

        back.useTexture {
            GL11.GL_QUADS.buffer(VertexFormat.Pos3dTex, 4) {
                v3Tex2dC(minX + offset, maxY - offset, minZ + offset, 1f, 0f)
                v3Tex2dC(maxX - offset, maxY - offset, minZ + offset, 0f, 0f)
                v3Tex2dC(maxX - offset, minY + offset, minZ + offset, 0f, 1f)
                v3Tex2dC(minX + offset, minY + offset, minZ + offset, 1f, 1f)
            }
        }

        left.useTexture {
            GL11.GL_QUADS.buffer(VertexFormat.Pos3dTex, 4) {
                v3Tex2dC(minX + offset, maxY - offset, maxZ - offset, 1f, 0f)
                v3Tex2dC(minX + offset, maxY - offset, minZ + offset, 0f, 0f)
                v3Tex2dC(minX + offset, minY + offset, minZ + offset, 0f, 1f)
                v3Tex2dC(minX + offset, minY + offset, maxZ - offset, 1f, 1f)
            }
        }

        up.useTexture {
            GL11.GL_QUADS.buffer(VertexFormat.Pos3dTex, 4) {
                v3Tex2dC(maxX - offset, maxY - offset, minZ + offset, 1f, 0f)
                v3Tex2dC(minX + offset, maxY - offset, minZ + offset, 0f, 0f)
                v3Tex2dC(minX + offset, maxY - offset, maxZ - offset, 0f, 1f)
                v3Tex2dC(maxX - offset, maxY - offset, maxZ - offset, 1f, 1f)
            }
        }

        down.useTexture {
            GL11.GL_QUADS.buffer(VertexFormat.Pos3dTex, 4) {
                v3Tex2dC(maxX - offset, minY + offset, maxZ - offset, 1f, 0f)
                v3Tex2dC(minX + offset, minY + offset, maxZ - offset, 0f, 0f)
                v3Tex2dC(minX + offset, minY + offset, minZ + offset, 0f, 1f)
                v3Tex2dC(maxX - offset, minY + offset, minZ + offset, 1f, 1f)
            }
        }
        GL11.glCullFace(GL11.GL_BACK)
        GLHelper.texture2d = false
    }

}