package net.spartanb312.boar.graphics.model.mesh

import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.graphics.model.Mesh
import net.spartanb312.boar.graphics.model.MeshRenderer
import net.spartanb312.boar.graphics.shader.Shader
import org.lwjgl.opengl.GL30

object MeshSimple : MeshRenderer() {

    override val shader = Shader("", "")

    override fun draw(mesh: Mesh) {
        if (mesh.textures.isEmpty()) return

        shader.bind()
        mesh.diffuseTexture?.let {
            GL30.glActiveTexture(GL30.GL_TEXTURE0)
            GL30.glUniform1i(shader.getUniformLocation("diffuseTex"), 0)
            it.texture.bindTexture()
        }

        mesh.normalTexture?.let {
            GL30.glActiveTexture(GL30.GL_TEXTURE1)
            GL30.glUniform1i(shader.getUniformLocation("normalTex"), 1)
            it.texture.bindTexture()
        }

        mesh.specularTexture?.let {
            GL30.glActiveTexture(GL30.GL_TEXTURE2)
            GL30.glUniform1i(shader.getUniformLocation("specularTex"), 2)
            it.texture.bindTexture()
        }

        mesh.heightTexture?.let {
            GL30.glActiveTexture(GL30.GL_TEXTURE3)
            GL30.glUniform1i(shader.getUniformLocation("heightTex"), 3)
            it.texture.bindTexture()
        }

        // Draw mesh
        GL30.glBindVertexArray(mesh.vao)
        GL30.glDrawElements(GL30.GL_TRIANGLES, mesh.vertices.size, GL30.GL_UNSIGNED_INT, 0)
        GL30.glBindVertexArray(0)

        GL30.glActiveTexture(GL30.GL_TEXTURE0)
        if (RS.compatMode) shader.unbind()
    }

}