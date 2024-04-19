package net.spartanb312.boar.graphics.model.mesh

import net.spartanb312.boar.graphics.GLHelper
import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.graphics.drawing.VertexFormat
import net.spartanb312.boar.graphics.drawing.buffer.ArrayedVertexBuffer.buffer
import net.spartanb312.boar.graphics.matrix.MatrixLayerStack
import net.spartanb312.boar.graphics.model.Mesh
import net.spartanb312.boar.graphics.model.MeshData
import net.spartanb312.boar.graphics.model.MeshRenderer
import net.spartanb312.boar.graphics.shader.Shader
import net.spartanb312.boar.utils.color.ColorRGB
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
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

        override val shader =
            if (RS.compatMode) Shader("assets/shader/model/210/MeshVertex.vsh", "assets/shader/model/210/MeshDNSH.fsh")
            else Shader("assets/shader/model/450/MeshVertex.vsh", "assets/shader/model/450/MeshDNSH.fsh")
        private val matrixUniform = shader.getUniformLocation("matrix")
        private val diffuse = shader.getUniformLocation("diffuseTex")
        private val normal = shader.getUniformLocation("normalTex")
        private val specular = shader.getUniformLocation("specularTex")
        private val height = shader.getUniformLocation("heightTex")

        override fun MatrixLayerStack.draw(mesh: Mesh) {
            if (mesh.textures.isEmpty()) return

            shader.bind()
            GL20.glUniformMatrix4fv(matrixUniform, false, matrixArray)
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

    override fun MatrixLayerStack.draw() = draw(this@MeshDNSH)

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

}