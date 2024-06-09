package net.spartanb312.everett.graphics.matrix

import com.soywiz.kds.iterators.fastForEach
import org.joml.Matrix4f
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class MatrixLayerStack {

    val stack = Stack<Matrix4f>().apply { push(Matrix4f()) }
    val stateStack = Stack<Matrix4f>()
    var checkID = 0L

    fun pushMatrixLayer(matrix4f: Matrix4f = Matrix4f()) {
        stack.push(matrix4f)
        stateStack.push(getMatrixResult())
    }

    fun popMatrixLayer(): Matrix4f {
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

    fun apply(checkInc: Int, matrix4f: Matrix4f): Matrix4f {
        checkID += checkInc
        return peek.set(matrix4f)
    }

    fun mul(checkInc: Int, matrix4f: Matrix4f): Matrix4f {
        checkID += checkInc
        return peek.mul(matrix4f)
    }

    private val id = AtomicInteger()
    private fun genID() = id.getAndIncrement()
    fun resetID() {
        id.set(0)
    }

    inner class MatrixScope(val layer: MatrixLayerStack, preMat: Matrix4f) {
        private val prevMat = Matrix4f(preMat)
        val checkInc = genID()
        fun recover() {
            layer.apply(checkInc, prevMat)
        }
    }

}

inline val MatrixLayerStack.newScope get() = this.MatrixScope(this, peek)

inline fun MatrixLayerStack.useScope(
    scope: MatrixLayerStack.MatrixScope,
    block: MatrixLayerStack.MatrixScope.() -> Unit
) {
    val preCheckID = checkID
    scope.block()
    scope.recover()
    checkID = preCheckID
}

inline fun MatrixLayerStack.scope(
    block: MatrixLayerStack.MatrixScope.() -> Unit
) = useScope(newScope, block)