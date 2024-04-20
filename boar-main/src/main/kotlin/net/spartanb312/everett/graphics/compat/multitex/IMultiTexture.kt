package net.spartanb312.everett.graphics.compat.multitex

import net.spartanb312.everett.graphics.compat.Delegate

interface IMultiTexture {

    @Delegate
    fun setActiveTexture(texture: Int)

    @Delegate
    fun setClientActiveTexture(texture: Int)

    @Delegate
    fun setLightmapTextureCoords(target: Int, x: Float, y: Float)

}