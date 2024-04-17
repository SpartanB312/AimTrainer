package net.spartanb312.boar.graphics.model.mesh

import net.spartanb312.boar.graphics.GLHelper
import net.spartanb312.boar.graphics.RS
import net.spartanb312.boar.graphics.model.Mesh
import net.spartanb312.boar.graphics.model.MeshRenderer
import net.spartanb312.boar.graphics.shader.Shader
import org.lwjgl.opengl.GL30

// Only diffuse texture
object MeshBasic : MeshRenderer() {

    override val shader = Shader("", "")

    override fun draw(mesh: Mesh) {
        if (mesh.textures.isEmpty()) return

        shader.bind()
        mesh.diffuseTexture?.let {
            GL30.glActiveTexture(GL30.GL_TEXTURE0)
            GL30.glUniform1i(shader.getUniformLocation("diffuseTex"), 0)
            it.texture.bindTexture()
        }

        // Draw mesh
        GLHelper.glBindVertexArray(mesh.vao)
        GL30.glDrawElements(GL30.GL_TRIANGLES, mesh.vertices.size, GL30.GL_UNSIGNED_INT, 0)
        GLHelper.glBindVertexArray(0)

        GL30.glActiveTexture(GL30.GL_TEXTURE0)
        if (RS.compatMode) shader.unbind()
    }

}