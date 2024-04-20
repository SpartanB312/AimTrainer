package net.spartanb312.everett.game.render.scene.impls

import net.spartanb312.everett.game.render.TextureManager
import net.spartanb312.everett.game.render.scene.Scene
import net.spartanb312.everett.graphics.Skybox

object SkyboxScene : Scene() {

    private val skybox = Skybox(
        -200f,
        -200f,
        -200f,
        200f,
        200f,
        200f,
        TextureManager.down,
        TextureManager.up,
        TextureManager.left,
        TextureManager.front,
        TextureManager.right,
        TextureManager.back
    )

    override fun render3D() = skybox.onRender3D()

}