package net.spartanb312.everett.game.render.scene.impls

import net.spartanb312.everett.game.render.TextureManager
import net.spartanb312.everett.game.render.scene.Scene
import net.spartanb312.everett.graphics.Skybox

object SkyboxScene : Scene() {

    private val skybox = Skybox(
        -20000f,
        -20000f,
        -20000f,
        20000f,
        20000f,
        20000f,
        TextureManager.universe_down,
        TextureManager.universe_up,
        TextureManager.universe_left,
        TextureManager.universe_front,
        TextureManager.universe_right,
        TextureManager.universe_back
    )

    override fun render3D() = skybox.onRender3D()

}