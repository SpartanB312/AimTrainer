package net.spartanb312.everett.graphics.compat.vao

import org.lwjgl.opengl.ARBVertexArrayObject

object ARBVAO : IVAO {

    override fun glGenVertexArrays(): Int =
        ARBVertexArrayObject.glGenVertexArrays()

    override fun glDeleteVertexArrays(array: Int) =
        ARBVertexArrayObject.glDeleteVertexArrays(array)

    override fun glBindVertexArray(array: Int) =
        ARBVertexArrayObject.glBindVertexArray(array)

}