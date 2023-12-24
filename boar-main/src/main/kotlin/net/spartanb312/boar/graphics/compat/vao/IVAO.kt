package net.spartanb312.boar.graphics.compat.vao

import net.spartanb312.boar.graphics.compat.Delegate

interface IVAO {

    //GL30
    @Delegate
    fun glGenVertexArrays(): Int

    @Delegate
    fun glDeleteVertexArrays(array: Int)

    @Delegate
    fun glBindVertexArray(array: Int)

}