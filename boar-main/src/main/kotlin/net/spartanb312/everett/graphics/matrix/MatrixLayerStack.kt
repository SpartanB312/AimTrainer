package net.spartanb312.everett.graphics.matrix

import com.soywiz.kds.iterators.fastForEach
import net.spartanb312.everett.graphics.GLHelper
import net.spartanb312.everett.graphics.RS
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11
import java.util.*

class MatrixLayerStack {

    val stack = Stack<Matrix4f>().apply { push(Matrix4f()) }
    val stateStack = Stack<Matrix4f>()
    var checkID = 0L

    fun pushMatrixLayer(matrix4f: Matrix4f = Matrix4f()) {
        checkID++
        if (RS.compatMode) GLHelper.pushMatrixAll()
        stack.push(matrix4f)
        stateStack.push(getMatrixResult())
    }

    fun popMatrixLayer(): Matrix4f {
        checkID++
        if (RS.compatMode) GLHelper.popMatrixAll()
        if (stateStack.isNotEmpty()) stateStack.pop()
        return if (stack.empty()) {
            throw Exception("Stack must has at least 1 matrix!")
        } else if (stack.size == 1) {
            stack.peek().identity()
        } else {
            stack.pop()
        }
    }

    inline val peek: Matrix4f get() = stack.peek()
    inline val matrix: Matrix4f
        get() = if (stateStack.isNotEmpty()) {
            Matrix4f(stateStack.peek()).mul(peek)
        } else peek

    inline val matrixArray get() = matrix.getFloatArray()

    private fun getMatrixResult(): Matrix4f {
        val result = Matrix4f()
        stack.fastForEach { mat ->
            result.mul(mat)
        }
        return result
    }

    fun makeIdentity(): Matrix4f = peek.identity()

    fun apply(matrix4f: Matrix4f): Matrix4f {
        checkID++
        if (RS.compatMode) GL11.glLoadMatrixf(matrix4f.getFloatArray())
        return peek.set(matrix4f)
    }

    fun mul(matrix4f: Matrix4f): Matrix4f {
        checkID++
        if (RS.compatMode) GL11.glMultMatrixf(matrix4f.getFloatArray())
        return peek.mul(matrix4f)
    }

}

fun MatrixLayerStack.scope(block: MatrixLayerStack.() -> Unit) {
    if (RS.compatMode) {
        pushMatrixLayer()
        block()
        popMatrixLayer()
    } else {
        val prevMat = Matrix4f(peek)
        block()
        apply(prevMat)
    }
}