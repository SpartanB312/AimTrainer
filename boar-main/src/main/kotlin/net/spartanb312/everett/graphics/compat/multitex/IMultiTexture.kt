package net.spartanb312.everett.graphics.compat.multitex

import net.spartanb312.everett.graphics.compat.Delegate

interface IMultiTexture {

    @Delegate
    fun glActiveTexture(texture: Int)

    @Delegate
    fun glClientActiveTexture(texture: Int)

    @Delegate
    fun glMultiTexCoord2f(target: Int, x: Float, y: Float)

}