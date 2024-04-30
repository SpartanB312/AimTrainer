package net.spartanb312.everett.graphics.compat.vao

import org.lwjgl.opengl.GL30C

object GL30VAO : IVAO {

    override fun glGenVertexArrays(): Int =
        GL30C.glGenVertexArrays()

    override fun glDeleteVertexArrays(array: Int) =
        GL30C.glDeleteVertexArrays(array)

    override fun glBindVertexArray(array: Int) {
        if (IVAO.currentVAO != array) {
            GL30C.glBindVertexArray(array)
            IVAO.currentVAO = array
        }
    }

}