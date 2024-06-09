package net.spartanb312.everett.graphics.model

import net.spartanb312.everett.graphics.OpenGL.*
import net.spartanb312.everett.graphics.matrix.MatrixLayerStack
import net.spartanb312.everett.utils.misc.createDirectByteBuffer
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30

abstract class Mesh(
    val vertices: MutableList<Vertex>,
    val diffuse: MutableList<ModelTexture>,
    val normal: MutableList<ModelTexture>,
    val specular: MutableList<ModelTexture>,
    val height: MutableList<ModelTexture>,
    val indices: IntArray,
) {

    // Nvidia GeForce 6000/7000 series supports VAO via ARB
    val vao = GL30.glGenVertexArrays()

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

    open fun setupMesh() {
        val buffer = createDirectByteBuffer(vertices.size * 32).apply {
            vertices.forEach {
                putFloat(it.position.x)
                putFloat(it.position.y)
                putFloat(it.position.z)
                putFloat(it.texCoords.x)
                putFloat(it.texCoords.y)
                putFloat(it.normal.x)
                putFloat(it.normal.y)
                putFloat(it.normal.z)
            }
            flip()
        }

        val indicesBuffer = createDirectByteBuffer(indices.size * 4).apply {
            indices.forEach {
                putInt(it)
            }
            flip()
        }
        val vbo = GL15.glGenBuffers()
        val ibo = GL15.glGenBuffers()

        GL30.glBindVertexArray(vao)

        // Buffer data
        GL15.glBindBuffer(GL_ARRAY_BUFFER, vbo)
        GL15.glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW)

        // Vertex
        GL20.glVertexAttribPointer(0, 3, GL_FLOAT, false, 32, 0)
        GL20.glEnableVertexAttribArray(0)

        // TexCoords
        GL20.glVertexAttribPointer(1, 2, GL_FLOAT, false, 32, 12)
        GL20.glEnableVertexAttribArray(1)

        // Normal
        GL20.glVertexAttribPointer(2, 3, GL_FLOAT, false, 32, 20)
        GL20.glEnableVertexAttribArray(2)

        // Buffer EBO
        GL15.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo)
        GL15.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW)

        GL30.glBindVertexArray(0)
        GL15.glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    abstract fun MatrixLayerStack.MatrixScope.draw()

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