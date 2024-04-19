package net.spartanb312.boar.graphics.model

import net.spartanb312.boar.graphics.matrix.MatrixLayerStack
import net.spartanb312.boar.graphics.shader.Shader

abstract class MeshRenderer {
    abstract val shader: Shader
    abstract fun MatrixLayerStack.draw(mesh: Mesh)
}