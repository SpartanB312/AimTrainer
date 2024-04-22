package net.spartanb312.everett.graphics.model

import net.spartanb312.everett.graphics.matrix.MatrixLayerStack
import net.spartanb312.everett.graphics.texture.MipmapTexture
import net.spartanb312.everett.graphics.texture.loader.TextureLoader
import net.spartanb312.everett.utils.Logger
import net.spartanb312.everett.utils.math.vector.Vec2f
import net.spartanb312.everett.utils.math.vector.Vec3f
import org.lwjgl.assimp.*
import org.lwjgl.system.MemoryUtil
import java.io.File
import java.nio.IntBuffer

class Model(
    private val path: String,
    private val textureLoader: TextureLoader? = null,
    private val processMesh: (MeshData) -> Mesh
) {

    private val dir = path.substringBeforeLast("/")
    private val textures = mutableListOf<ModelTexture>()
    private val meshes = mutableListOf<Mesh>()

    fun drawModel(matrixLayerStack: MatrixLayerStack) {
        meshes.forEach { with(it) { matrixLayerStack.draw() } }
    }

    fun loadModel() {
        Logger.info("Loading model $path")
        val scene = Assimp.aiImportFile(
            File(path).absolutePath,
            Assimp.aiProcess_Triangulate or Assimp.aiProcess_GenSmoothNormals or Assimp.aiProcess_FixInfacingNormals or Assimp.aiProcess_GenUVCoords
        )
        if (scene?.mRootNode() == null) {
            Logger.error("Failed to load model: $path")
            return
        }
        processNode(scene.mRootNode()!!, scene)
    }

    private fun processNode(node: AINode, scene: AIScene) {
        val meshesPointers = scene.mMeshes()!!
        for (index in 0 until node.mNumMeshes()) {
            val mesh = AIMesh.create(meshesPointers.get(node.mMeshes()!!.get(index)))
            meshes.add(processMesh(mesh, scene))
        }
        for (index in 0 until node.mNumChildren()) {
            processNode(AINode.create(node.mChildren()!!.get(index)), scene)
        }
    }

    private fun processMesh(mesh: AIMesh, scene: AIScene): Mesh {
        val vertices = mutableListOf<Vertex>()
        val indices = mutableListOf<Int>()
        val diffuseTextures = mutableListOf<ModelTexture>()
        val specularTextures = mutableListOf<ModelTexture>()
        val normalTextures = mutableListOf<ModelTexture>()
        val heightTextures = mutableListOf<ModelTexture>()

        // Vertex
        val aiV = mesh.mVertices()
        val aiN = mesh.mNormals()
        val aiT = mesh.mTextureCoords(0)
        val aiTan = mesh.mTangents()
        val aiBitan = mesh.mBitangents()
        for (index in 0 until mesh.mNumVertices()) {
            val pos = vec3f(aiV.get(index))
            val normal = if (aiN != null) vec3f(aiN.get(index)) else Vec3f()
            val texCoords = if (aiT != null) vec2f(aiT.get(index)) else Vec2f()
            val tangent = if (aiTan != null) vec3f(aiTan.get(index)) else Vec3f()
            val bitangent = if (aiBitan != null) vec3f(aiBitan.get(index)) else Vec3f()
            vertices.add(Vertex(pos, normal, texCoords, tangent, bitangent))
        }
        aiV.free()
        aiN?.free()
        aiT?.free()
        aiTan?.free()
        aiBitan?.free()

        // Face
        val aiF = mesh.mFaces()
        for (index in 0 until mesh.mNumFaces()) {
            val face = aiF.get(index)
            val aiI = face.mIndices()
            for (i in 0 until face.mNumIndices()) {
                indices.add(aiI.get(i))
            }
        }

        // Materials
        val aiMP = scene.mMaterials()?.get(mesh.mMaterialIndex())
        if (aiMP != null && aiMP != MemoryUtil.NULL) {
            val aiM = AIMaterial.create(aiMP)
            diffuseTextures.addAll(loadTextures(aiM, ModelTexture.Type.DIFFUSE))
            specularTextures.addAll(loadTextures(aiM, ModelTexture.Type.SPECULAR))
            normalTextures.addAll(loadTextures(aiM, ModelTexture.Type.NORMAL))
            heightTextures.addAll(loadTextures(aiM, ModelTexture.Type.HEIGHT))
        }

        // Post process mesh
        val meshData = MeshData(
            vertices,
            diffuseTextures,
            normalTextures,
            specularTextures,
            heightTextures,
            indices.toIntArray()
        )

        return processMesh.invoke(meshData)
    }

    private fun loadTextures(material: AIMaterial, type: ModelTexture.Type): List<ModelTexture> {
        val textures = mutableListOf<ModelTexture>()
        for (index in 0 until Assimp.aiGetMaterialTextureCount(material, type.type)) {
            val aiString = AIString.create()
            Assimp.aiGetMaterialTexture(
                material,
                type.type,
                0,
                aiString,
                null as IntBuffer?, null, null, null, null, null
            )

            val name = aiString.dataString().split("\\\\").last()
            // Find the existed texture
            val exist = this.textures.find { it.name == name }
            if (exist != null) {
                textures.add(exist)
            } else {
                Logger.debug("/$dir/$name")
                val tex = textureLoader?.lazyLoad("/$dir/$name") ?: MipmapTexture("/$dir/$name")
                val meshTexture = ModelTexture(tex, type, name)
                textures.add(meshTexture)
                this.textures.add(meshTexture)
            }
        }
        return textures
    }

    private fun vec3f(aiV: AIVector3D) = Vec3f(aiV.x(), aiV.y(), aiV.z())
    private fun vec2f(aiV: AIVector3D) = Vec2f(aiV.x(), aiV.y())

}