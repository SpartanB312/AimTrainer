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
import org.lwjgl.opengl.GL20

class MeshBasic(meshData: MeshData) : Mesh(
    meshData.vertices,
    meshData.diffuse,
    meshData.normal,
    meshData.specular,
    meshData.height,
    meshData.indices
) {

    companion object DrawShader : MeshRenderer() {

        override val shader = Shader("assets/shader/model/MeshVertex.vsh", "assets/shader/model/MeshBasic.fsh")
        private val matrixUniform = shader.getUniformLocation("matrix")
        private val diffuse = shader.getUniformLocation("diffuseTex")

        override fun MatrixLayerStack.MatrixScope.draw(mesh: Mesh) {
            if (mesh.textures.isEmpty()) return
            GLHelper.cull = false

            shader.bind()
            layer.matrixArray.glUniform(matrixUniform)
            mesh.diffuseTexture?.let {
                GL13.glActiveTexture(GL_TEXTURE0)
                GL20.glUniform1i(diffuse, 0)
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

    override fun MatrixLayerStack.MatrixScope.draw() = draw(this@MeshBasic)

}