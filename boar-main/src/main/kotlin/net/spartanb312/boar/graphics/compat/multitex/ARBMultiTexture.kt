package net.spartanb312.boar.graphics.compat.multitex

import org.lwjgl.opengl.ARBMultitexture

object ARBMultiTexture : IMultiTexture {

    override fun setActiveTexture(texture: Int) =
        ARBMultitexture.glActiveTextureARB(texture)

    override fun setClientActiveTexture(texture: Int) =
        ARBMultitexture.glClientActiveTextureARB(texture)

    override fun setLightmapTextureCoords(target: Int, x: Float, y: Float) =
        ARBMultitexture.glMultiTexCoord2fARB(target, x, y)

}