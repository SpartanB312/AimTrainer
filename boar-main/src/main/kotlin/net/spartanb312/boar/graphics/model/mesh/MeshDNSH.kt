package net.spartanb312.boar.graphics.model.mesh

import net.spartanb312.boar.graphics.GLHelper
import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.graphics.drawing.VertexFormat
import net.spartanb312.boar.graphics.drawing.buffer.ArrayedVertexBuffer.buffer
import net.spartanb312.boar.graphics.model.Mesh
import net.spartanb312.boar.graphics.model.MeshData
import net.spartanb312.boar.graphics.model.MeshRenderer
import net.spartanb312.boar.graphics.shader.Shader
import net.spartanb312.boar.utils.color.ColorRGB
import net.spartanb312.boar.utils.misc.createDirectByteBuffer
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30

class MeshDNSH(meshData: MeshData) : Mesh(
    meshData.vertices,
    meshData.diffuse,
    meshData.normal,
    meshData.specular,
    meshData.height,
    meshData.indices
) {

    companion object DrawShader : MeshRenderer() {

        override val shader = Shader("assets/shader/model/MeshVertex.vsh", "assets/shader/model/MeshDNSH.fsh")
        private val diffuse = shader.getUniformLocation("diffuseTex")
        private val normal = shader.getUniformLocation("normalTex")
        private val specular = shader.getUniformLocation("specularTex")
        private val height = shader.getUniformLocation("heightTex")

        override fun draw(mesh: Mesh) {
            if (mesh.textures.isEmpty()) return

            shader.bind()
            mesh.diffuseTexture?.let {
                GL30.glActiveTexture(GL30.GL_TEXTURE0)
                GL30.glUniform1i(diffuse, 0)
                it.bindTexture()
            }

            mesh.normalTexture?.let {
                GL30.glActiveTexture(GL30.GL_TEXTURE1)
                GL30.glUniform1i(normal, 1)
                it.bindTexture()
            }

            mesh.specularTexture?.let {
                GL30.glActiveTexture(GL30.GL_TEXTURE2)
                GL30.glUniform1i(specular, 2)
                it.bindTexture()
            }

            mesh.heightTexture?.let {
                GL30.glActiveTexture(GL30.GL_TEXTURE3)
                GL30.glUniform1i(height, 3)
                it.bindTexture()
            }

            // Draw mesh
            GL30.glBindVertexArray(mesh.vao)
            GL30.glDrawElements(GL30.GL_TRIANGLES, mesh.vertices.size, GL30.GL_UNSIGNED_INT, 0)
            GL30.glBindVertexArray(0)

            GL30.glActiveTexture(GL30.GL_TEXTURE0)
            if (RS.compatMode) shader.unbind()
        }

    }

    init {
        setupMesh()
    }

    override fun draw() = DrawShader.draw(this)

    override fun drawLegacy() {
        GLHelper.texture2d = true
        diffuseTexture?.let {
            it.bindTexture()
            GL11.GL_TRIANGLES.buffer(VertexFormat.Pos3fColorTex, indices.size) {
                for (i in indices) {
                    val vert = vertices[i]
                    v3Tex2fC(
                        vert.position.x,
                        vert.position.y,
                        vert.position.z,
                        vert.texCoords.x,
                        vert.texCoords.y,
                        ColorRGB.WHITE
                    )
                }
            }
        }
        specularTexture?.let {
            it.bindTexture()
            GL11.GL_TRIANGLES.buffer(VertexFormat.Pos3fColorTex, indices.size) {
                for (i in indices) {
                    val vert = vertices[i]
                    v3Tex2fC(
                        vert.position.x,
                        vert.position.y,
                        vert.position.z,
                        vert.texCoords.x,
                        vert.texCoords.y,
                        ColorRGB.WHITE
                    )
                }
            }
        }
    }

    override fun setupMesh() {
        val positionBuffer = createDirectByteBuffer(vertices.size * 12).apply {
            vertices.forEach {
                putFloat(it.position.x)
                putFloat(it.position.y)
                putFloat(it.position.z)
            }
            flip()
        }
        val texCoordsBuffer = createDirectByteBuffer(vertices.size * 8).apply {
            vertices.forEach {
                putFloat(it.texCoords.x)
                putFloat(it.texCoords.y)
            }
            flip()
        }

        val indicesBuffer = createDirectByteBuffer(indices.size * 4).apply {
            indices.forEach {
                putInt(it)
            }
            flip()
        }
        val positionVbo = GL30.glGenBuffers()
        val uvVbo = GL30.glGenBuffers()
        val ibo = GL30.glGenBuffers()

        GL30.glBindVertexArray(vao)

        // Vertex
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, positionVbo)
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, positionBuffer, GL30.GL_STATIC_DRAW)
        GL30.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, 12, 0)
        GL30.glEnableVertexAttribArray(0)

        // TexCoords
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, uvVbo)
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, texCoordsBuffer, GL30.GL_STATIC_DRAW)
        GL30.glVertexAttribPointer(1, 2, GL30.GL_FLOAT, false, 8, 0)
        GL30.glEnableVertexAttribArray(1)

        // Buffer EBO
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, ibo)
        GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL30.GL_STATIC_DRAW)

        GL30.glBindVertexArray(0)
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0)
    }

}