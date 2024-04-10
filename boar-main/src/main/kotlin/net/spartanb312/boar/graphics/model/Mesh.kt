package net.spartanb312.boar.graphics.model

import net.spartanb312.boar.graphics.GLHelper
import net.spartanb312.boar.graphics.drawing.VertexBuffer
import net.spartanb312.boar.graphics.drawing.VertexFormat
import net.spartanb312.boar.utils.misc.createDirectByteBuffer
import org.lwjgl.opengl.GL30.*

class Mesh(
    val vertices: MutableList<Vertex>,
    private val textures: MutableList<ModelTexture>,
    private val indices: IntArray,
) {

    private var vao = 0
    private val diffuseTexture = textures.firstOrNull { it.type == ModelTexture.Type.DIFFUSE }?.also {
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
    }!!

    init {
        setupMesh()
    }

    private fun setupMesh() {
        val byteBuffer = VertexBuffer.getBuffer(VertexFormat.Pos3fNormalTex, vertices.size) {
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

    fun forceDraw() {
        if (indices.size % 3 != 0) throw Exception("error")
        GLHelper.texture2d = true
        diffuseTexture.texture.bindTexture()
        glColor4f(1f, 1f, 1f, 1f)
        glBegin(GL_TRIANGLES)
        for (i in indices) {
            val vertex = vertices[i]
            glTexCoord2f(vertex.texCoords.x, vertex.texCoords.y)
            glVertex3f(vertex.position.x, vertex.position.y, vertex.position.z)
        }
        glEnd()
        diffuseTexture.texture.unbindTexture()
        GLHelper.texture2d = false
    }

    fun draw(shader: MeshShader) {
        if (textures.isEmpty()) println("empt")
        textures.forEachIndexed { _, texture ->
            if (texture.type == ModelTexture.Type.DIFFUSE) {
                glActiveTexture(GL_TEXTURE0)
                glUniform1i(shader.getUniformLocation("texture_diffuse1"), 0)
                texture.texture.bindTexture()
            }
        }

        // Draw mesh
        glBindVertexArray(vao)
        glDrawElements(GL_TRIANGLES, vertices.size, GL_UNSIGNED_INT, 0)
        glBindVertexArray(0)

        glActiveTexture(GL_TEXTURE0)
    }

}