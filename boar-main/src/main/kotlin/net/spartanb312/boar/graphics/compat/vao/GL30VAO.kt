package net.spartanb312.boar.graphics.compat.vao

import org.lwjgl.opengl.GL30

object GL30VAO : IVAO {

    override fun glGenVertexArrays(): Int =
        GL30.glGenVertexArrays()

    override fun glDeleteVertexArrays(array: Int) =
        GL30.glDeleteVertexArrays(array)

    override fun glBindVertexArray(array: Int) =
        GL30.glBindVertexArray(array)

}