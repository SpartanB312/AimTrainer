package net.spartanb312.boar.game.render.scene.impls

import net.spartanb312.boar.game.render.TextureManager
import net.spartanb312.boar.game.render.scene.Scene
import net.spartanb312.boar.graphics.Skybox

object SkyboxScene : Scene() {

    private val skybox = Skybox(
        -200.0,
        -200.0,
        -200.0,
        200.0,
        200.0,
        200.0,
        TextureManager.down,
        TextureManager.up,
        TextureManager.left,
        TextureManager.front,
        TextureManager.right,
        TextureManager.back
    )

    override fun onRender() = skybox.onRender3D()

}