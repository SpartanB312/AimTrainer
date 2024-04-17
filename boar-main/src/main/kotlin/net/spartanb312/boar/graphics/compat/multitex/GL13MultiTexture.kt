package net.spartanb312.boar.graphics.compat.multitex

import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL13C

object GL13MultiTexture : IMultiTexture {

    override fun setActiveTexture(texture: Int) =
        GL13C.glActiveTexture(texture)

    override fun setClientActiveTexture(texture: Int) =
        GL13.glClientActiveTexture(texture)

    override fun setLightmapTextureCoords(target: Int, x: Float, y: Float) =
        GL13.glMultiTexCoord2f(target, x, y)

}