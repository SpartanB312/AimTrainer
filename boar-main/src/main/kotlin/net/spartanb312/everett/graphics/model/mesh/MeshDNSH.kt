package net.spartanb312.everett.graphics.model.mesh

import net.spartanb312.everett.game.Player
import net.spartanb312.everett.graphics.GLHelper
import net.spartanb312.everett.graphics.OpenGL.*
import net.spartanb312.everett.graphics.matrix.MatrixLayerStack
import net.spartanb312.everett.graphics.model.Mesh
import net.spartanb312.everett.graphics.model.MeshData
import net.spartanb312.everett.graphics.model.MeshRenderer
import net.spartanb312.everett.graphics.shader.Shader
import net.spartanb312.everett.graphics.shader.glUniform
import net.spartanb312.everett.utils.color.ColorRGB
import net.spartanb312.everett.utils.math.toRadian
import net.spartanb312.everett.utils.math.vector.Vec3f
import org.lwjgl.opengl.GL13

var lightPosition = Vec3f(45f, 45f, 45f)
var lightPosition2 = Vec3f(-45f, 45f, -45f)

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
        private val viewPos = shader.getUniformLocation("viewPos")
        private val lightColor = shader.getUniformLocation("lightColor")
        private val lightColor2 = shader.getUniformLocation("lightColor2")
        private val lightPos = shader.getUniformLocation("lightPos")
        private val lightPos2 = shader.getUniformLocation("lightPos2")

        override fun MatrixLayerStack.MatrixScope.draw(mesh: Mesh) {
            if (mesh.textures.isEmpty()) return

            val c = ColorRGB.LIGHT_PURPLE.mix(ColorRGB.WHITE, 0.2f)
            val c2 = ColorRGB.GOLD.mix(ColorRGB.WHITE, 0.2f)
            val color = Vec3f(c.rFloat, c.gFloat, c.bFloat)
            val color2 = Vec3f(c2.rFloat, c2.gFloat, c2.bFloat)
            val yaw = (((System.currentTimeMillis() - 114514) % 5000) / 5000f * 360f).toRadian()
            lightPosition = Vec3f(yaw, 10f.toRadian()) * 10f
            lightPosition2 = Vec3f(yaw + 180f.toRadian(), 10f.toRadian()) * 10f

            shader.bind()
            layer.matrixArray.glUniform(matrixUniform)

            // upload Lights
            color.glUniform(lightColor)
            color2.glUniform(lightColor2)
            lightPosition.glUniform(lightPos)
            lightPosition2.glUniform(lightPos2)

            Player.camera.cameraPos.glUniform(viewPos)

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