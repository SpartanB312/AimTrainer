package net.spartanb312.everett.graphics.compat.vao

import net.spartanb312.everett.graphics.compat.Delegate

interface IVAO {

    //GL30
    @Delegate
    fun glGenVertexArrays(): Int

    @Delegate
    fun glDeleteVertexArrays(array: Int)

    @Delegate
    fun glBindVertexArray(array: Int)

}