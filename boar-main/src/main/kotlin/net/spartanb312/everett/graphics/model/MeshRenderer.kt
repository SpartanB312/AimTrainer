package net.spartanb312.everett.graphics.model

import net.spartanb312.everett.graphics.matrix.MatrixLayerStack
import net.spartanb312.everett.graphics.shader.Shader

abstract class MeshRenderer {
    abstract val shader: Shader
    abstract fun MatrixLayerStack.MatrixScope.draw(mesh: Mesh)
}