package net.spartanb312.boar.graphics.model

import net.spartanb312.boar.graphics.shader.Shader

object MeshShaders {

    /**
     * Only diffuse texture
     */
    object Basic : MeshShader(
        "/assets/shaders/model/MeshVertex.vert",
        "/assets/shaders/model/mesh/MeshD1F1H1.frag"
    ) {
        override val diffusePoses = arrayOf(
            getUniformLocation(ModelTexture.Type.DIFFUSE.displayName + 1)
        )
        override val heightPoses = arrayOf(
            getUniformLocation(ModelTexture.Type.HEIGHT.displayName + 1)
        )
        override val normalPoses = arrayOf(
            getUniformLocation(ModelTexture.Type.NORMAL.displayName + 1)
        )
    }

}

abstract class MeshShader(vsh: String, fsh: String) : Shader(vsh, fsh) {

    open val diffusePoses: Array<Int> = emptyArray()
    open val normalPoses: Array<Int> = emptyArray()
    open val specularPoses: Array<Int> = emptyArray()
    open val heightPoses: Array<Int> = emptyArray()

    fun setSampler(type: ModelTexture.Type, index: Int, value: Int) {
        val location = when (type) {
            ModelTexture.Type.DIFFUSE -> {
                if (diffusePoses.size > index - 1) diffusePoses[index - 1]
                else null
            }
            ModelTexture.Type.NORMAL -> {
                if (normalPoses.size > index - 1) normalPoses[index - 1]
                else null
            }
            ModelTexture.Type.SPECULAR -> {
                if (specularPoses.size > index - 1) specularPoses[index - 1]
                else null
            }
            ModelTexture.Type.HEIGHT -> {
                if (heightPoses.size > index - 1) heightPoses[index - 1]
                else null
            }
        }
        if (location != null) setInt(location, value)
        else println("Error ! unknown location!!! Type:${type.displayName} index: $index")
    }

}