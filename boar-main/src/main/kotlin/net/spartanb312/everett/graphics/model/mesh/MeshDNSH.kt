package net.spartanb312.everett.graphics.model.mesh

import net.spartanb312.everett.graphics.GLHelper
import net.spartanb312.everett.graphics.OpenGL.*
import net.spartanb312.everett.graphics.matrix.MatrixLayerStack
import net.spartanb312.everett.graphics.model.Mesh
import net.spartanb312.everett.graphics.model.MeshData
import net.spartanb312.everett.graphics.model.MeshRenderer
import net.spartanb312.everett.graphics.shader.Shader
import net.spartanb312.everett.graphics.shader.glUniform
import org.lwjgl.opengl.GL13

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
        private val matrixUniform = shader.getUniformLocation("matrix")
        private val diffuse = shader.getUniformLocation("diffuseTex")
        private val normal = shader.getUniformLocation("normalTex")
        private val specular = shader.getUniformLocation("specularTex")
        private val height = shader.getUniformLocation("heightTex")

        override fun MatrixLayerStack.MatrixScope.draw(mesh: Mesh) {
            if (mesh.textures.isEmpty()) return

            shader.bind()
            layer.matrixArray.glUniform(matrixUniform)
            mesh.diffuseTexture?.let {
                GL13.glActiveTexture(GL_TEXTURE0)
                glUniform1i(diffuse, 0)
                it.bindTexture()
            }

            mesh.normalTexture?.let {
                GL13.glActiveTexture(GL_TEXTURE1)
                glUniform1i(normal, 1)
                it.bindTexture()
            }

            mesh.specularTexture?.let {
                GL13.glActiveTexture(GL_TEXTURE2)
                glUniform1i(specular, 2)
                it.bindTexture()
            }

            mesh.heightTexture?.let {
                GL13.glActiveTexture(GL_TEXTURE3)
                glUniform1i(height, 3)
                it.bindTexture()
            }

            // Draw mesh via VAO
            GLHelper.bindVertexArray(mesh.vao)
            glDrawElements(GL_TRIANGLES, mesh.vertices.size, GL_UNSIGNED_INT, 0)
            GL13.glActiveTexture(GL_TEXTURE0)
        }

    }

    init {
        setupMesh()
    }

    override fun MatrixLayerStack.MatrixScope.draw() = draw(this@MeshDNSH)

}