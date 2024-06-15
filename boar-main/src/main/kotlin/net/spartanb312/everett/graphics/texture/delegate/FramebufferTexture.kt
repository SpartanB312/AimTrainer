package net.spartanb312.everett.graphics.texture.delegate

import net.spartanb312.everett.graphics.texture.Texture
import org.lwjgl.opengl.GL11

class FramebufferTexture(
    override var width: Int,
    override var height: Int,
) : Texture {
    private var deleted = false
    override val id = GL11.glGenTextures()
    override val available get() = !deleted
    override fun bindTexture() = GL11.glBindTexture(GL11.GL_TEXTURE_2D, id)
    override fun unbindTexture() = GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
    override fun deleteTexture() {
        GL11.glDeleteTextures(id)
        deleted = true
    }
}