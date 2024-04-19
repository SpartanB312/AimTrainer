package net.spartanb312.boar.graphics.model

import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL30.glGenVertexArrays

abstract class Mesh(
    val vertices: MutableList<Vertex>,
    val diffuse: MutableList<ModelTexture>,
    val normal: MutableList<ModelTexture>,
    val specular: MutableList<ModelTexture>,
    val height: MutableList<ModelTexture>,
    val indices: IntArray,
) {

    val vao = glGenVertexArrays()

    // Textures
    val diffuseTexture = diffuse.getOrNull(0)
    val normalTexture = normal.getOrNull(0)
    val specularTexture = specular.getOrNull(0)
    val heightTexture = height.getOrNull(0)
    val textures = diffuse.toMutableList().apply {
        addAll(normal)
        addAll(specular)
        addAll(height)
        forEach {
            it.texture.bindTexture()
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        }
    }

    abstract fun setupMesh()
    abstract fun draw()
    abstract fun drawLegacy()

}

data class MeshData(
    val vertices: MutableList<Vertex>,
    val diffuse: MutableList<ModelTexture>,
    val normal: MutableList<ModelTexture>,
    val specular: MutableList<ModelTexture>,
    val height: MutableList<ModelTexture>,
    val indices: IntArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MeshData

        if (vertices != other.vertices) return false
        if (diffuse != other.diffuse) return false
        if (normal != other.normal) return false
        if (specular != other.specular) return false
        if (height != other.height) return false
        return indices.contentEquals(other.indices)
    }

    override fun hashCode(): Int {
        var result = vertices.hashCode()
        result = 31 * result + diffuse.hashCode()
        result = 31 * result + normal.hashCode()
        result = 31 * result + specular.hashCode()
        result = 31 * result + height.hashCode()
        result = 31 * result + indices.contentHashCode()
        return result
    }
}