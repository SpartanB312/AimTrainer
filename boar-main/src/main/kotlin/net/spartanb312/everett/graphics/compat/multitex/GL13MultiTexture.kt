package net.spartanb312.everett.graphics.compat.multitex

import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL13C

object GL13MultiTexture : IMultiTexture {

    override fun glActiveTexture(texture: Int) =
        GL13C.glActiveTexture(texture)

    override fun glClientActiveTexture(texture: Int) =
        GL13.glClientActiveTexture(texture)

    override fun glMultiTexCoord2f(target: Int, x: Float, y: Float) =
        GL13.glMultiTexCoord2f(target, x, y)

}