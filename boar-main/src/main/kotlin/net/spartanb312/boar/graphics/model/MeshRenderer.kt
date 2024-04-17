package net.spartanb312.boar.graphics.model

import net.spartanb312.boar.graphics.shader.Shader

abstract class MeshRenderer {
    abstract val shader: Shader
    abstract fun draw(mesh: Mesh)
}