package net.spartanb312.boar.graphics.model

import net.spartanb312.boar.graphics.drawing.VertexFormat
import net.spartanb312.boar.graphics.drawing.buffer.ArrayedVertexBuffer
import net.spartanb312.boar.utils.misc.createDirectByteBuffer
import org.lwjgl.opengl.GL30.*

class Mesh(
    val vertices: MutableList<Vertex>,
    val diffuse: MutableList<ModelTexture>,
    val normal: MutableList<ModelTexture>,
    val specular: MutableList<ModelTexture>,
    val height: MutableList<ModelTexture>,
    val indices: IntArray,
) {

    var vao = 0

    // Textures
    val diffuseTexture = diffuse.getOrNull(0)
    val normalTexture = normal.getOrNull(0)
    val specularTexture = specular.getOrNull(0)
    val heightTexture = height.getOrNull(0)
    val textures = diffuse.toMutableList().apply {
        addAll(normal)
        addAll(specular)
        addAll(height)
    }

    init {
        setupMesh()
    }

    private fun setupMesh() {
        val byteBuffer = ArrayedVertexBuffer.getBuffer(VertexFormat.Pos3fNormalTex, vertices.size) {
            vertices.forEach {
                v3f(it.position.x, it.position.y, it.position.z)
                n3f(it.normal.x, it.normal.y, it.normal.z)
                tex(it.texCoords.x, it.texCoords.y)
            }
        }

        val indicesBuffer = createDirectByteBuffer(indices.size * 4).apply {
            indices.forEach {
                putInt(it)
            }
            flip()
        }

        vao = glGenVertexArrays()
        val vbo = glGenBuffers()
        val ibo = glGenBuffers()

        glBindVertexArray(vao)

        // Buffer VBO
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glBufferData(GL_ARRAY_BUFFER, byteBuffer, GL_STATIC_DRAW)

        // Buffer EBO
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW)

        // Vertex
        glEnableVertexAttribArray(0)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 32, 0)
        // Normal
        glEnableVertexAttribArray(1)
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 32, 12)
        // TexCoords
        glEnableVertexAttribArray(2)
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 32, 24)

        glBindBuffer(GL_ARRAY_BUFFER, 0)

        glBindVertexArray(0)

    }

}