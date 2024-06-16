package net.spartanb312.everett.game.render

import net.spartanb312.everett.game.option.impls.AccessibilityOption
import net.spartanb312.everett.graphics.OpenGL
import net.spartanb312.everett.graphics.RS
import net.spartanb312.everett.graphics.texture.Texture
import net.spartanb312.everett.graphics.texture.loader.AsyncTextureLoader
import net.spartanb312.everett.graphics.texture.loader.LazyTextureContainer
import net.spartanb312.everett.graphics.texture.loader.TextureLoader

object TextureManager : TextureLoader by AsyncTextureLoader(RS.maxThreads, { AccessibilityOption.threadsLimit }) {

    fun lazyTexture(
        path: String,
        format: Int = OpenGL.GL_RGBA,
        levels: Int = 3,
        useMipmap: Boolean = true,
        qualityLevel: Int = 2
    ): Texture = LazyTextureContainer(path, format, levels, useMipmap, qualityLevel).register()

    // Medals
    val kill2 = lazyTexture("assets/texture/medals/double_kill.png", qualityLevel = 1)
    val kill3 = lazyTexture("assets/texture/medals/triple_kill.png", qualityLevel = 1)
    val kill4 = lazyTexture("assets/texture/medals/overkill.png", qualityLevel = 1)
    val kill5 = lazyTexture("assets/texture/medals/killtacular.png", qualityLevel = 1)
    val kill6 = lazyTexture("assets/texture/medals/killtrocity.png", qualityLevel = 1)
    val kill7 = lazyTexture("assets/texture/medals/killamanjaro.png", qualityLevel = 1)
    val kill8 = lazyTexture("assets/texture/medals/killtastrophe.png", qualityLevel = 1)
    val kill9 = lazyTexture("assets/texture/medals/killpocalypse.png", qualityLevel = 1)
    val kill10 = lazyTexture("assets/texture/medals/killionaire.png", qualityLevel = 1)
    val perfect = lazyTexture("assets/texture/medals/perfect.png", qualityLevel = 1)
    val clockStop = lazyTexture("assets/texture/medals/clock_stop.png", qualityLevel = 1)
    val flawlessVictory = lazyTexture("assets/texture/medals/flawless_victory.png", qualityLevel = 1)

    // Skybox
    val down = lazyTexture("assets/texture/skybox/1.png")
    val up = lazyTexture("assets/texture/skybox/2.png")
    val left = lazyTexture("assets/texture/skybox/3.png")
    val front = lazyTexture("assets/texture/skybox/4.png")
    val right = lazyTexture("assets/texture/skybox/5.png")
    val back = lazyTexture("assets/texture/skybox/6.png")

    // Menu
    val bg = lazyTexture("assets/texture/menu/halo_infinite_background.png")

    // Spartan
    val everett = lazyTexture("assets/texture/EVRT.png")

    // Misc
    val warning = lazyTexture("assets/texture/misc/warning.png")

}