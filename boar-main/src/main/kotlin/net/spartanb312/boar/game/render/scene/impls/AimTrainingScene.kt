package net.spartanb312.boar.game.render.scene.impls

import net.spartanb312.boar.game.render.TextureManager
import net.spartanb312.boar.game.render.scene.Scene
import net.spartanb312.boar.game.training.Training
import net.spartanb312.boar.graphics.Skybox

object AimTrainingScene : Scene() {

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

    private var currentTraining: Training? = null

    override fun render3D() {
        skybox.onRender3D()
        currentTraining?.render()
    }

    override fun onMouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean {
        currentTraining?.onClick()
        return true
    }

}