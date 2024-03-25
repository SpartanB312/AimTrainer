package net.spartanb312.boar.game.render

import net.spartanb312.boar.game.option.impls.AccessibilityOption
import net.spartanb312.boar.graphics.OpenGL
import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.graphics.texture.Texture
import net.spartanb312.boar.graphics.texture.loader.AsyncTextureLoader
import net.spartanb312.boar.graphics.texture.loader.LazyTextureContainer
import net.spartanb312.boar.graphics.texture.loader.TextureLoader

object TextureManager : TextureLoader by AsyncTextureLoader(RS.maxThreads, { AccessibilityOption.threadsLimit }) {

    fun lazyTexture(
        path: String,
        format: Int = OpenGL.GL_RGBA,
        levels: Int = 3,
        useMipmap: Boolean = true,
        qualityLevel: Int = 2
    ): Texture = LazyTextureContainer(path, format, levels, useMipmap, qualityLevel).register()

    val down = lazyTexture("assets/texture/skybox/1.png")
    val up = lazyTexture("assets/texture/skybox/2.png")
    val left = lazyTexture("assets/texture/skybox/3.png")
    val front = lazyTexture("assets/texture/skybox/4.png")
    val right = lazyTexture("assets/texture/skybox/5.png")
    val back = lazyTexture("assets/texture/skybox/6.png")

    // Menu
    val bg = lazyTexture("assets/texture/menu/halo_infinite_background.png")

}