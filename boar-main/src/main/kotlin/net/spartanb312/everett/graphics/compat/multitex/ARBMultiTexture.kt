package net.spartanb312.everett.graphics.compat.multitex

import org.lwjgl.opengl.ARBMultitexture

object ARBMultiTexture : IMultiTexture {

    override fun glActiveTexture(texture: Int) =
        ARBMultitexture.glActiveTextureARB(texture)

    override fun glClientActiveTexture(texture: Int) =
        ARBMultitexture.glClientActiveTextureARB(texture)

    override fun glMultiTexCoord2f(target: Int, x: Float, y: Float) =
        ARBMultitexture.glMultiTexCoord2fARB(target, x, y)

}